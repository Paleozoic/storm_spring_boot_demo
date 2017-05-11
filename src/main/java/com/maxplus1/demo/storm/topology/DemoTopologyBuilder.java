package com.maxplus1.demo.storm.topology;

import com.maxplus1.demo.storm.bolt.SplitSentenceBolt;
import com.maxplus1.demo.storm.bolt.WordCountBolt;
import com.maxplus1.demo.storm.bolt.WordCountToRedisBolt;
import com.maxplus1.demo.storm.bolt.WordCountTopNToRedisBolt;
import com.maxplus1.demo.storm.bolt.builder.*;
import com.maxplus1.demo.storm.spout.KafkaProducerSpout;
import com.maxplus1.demo.storm.spout.builder.KafkaClientSpoutBuilder;
import com.maxplus1.demo.storm.spout.builder.KafkaProducerSpoutBuilder;
import org.apache.storm.jdbc.bolt.JdbcInsertBolt;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.starter.bolt.IntermediateRankingsBolt;
import org.apache.storm.starter.bolt.RollingCountBolt;
import org.apache.storm.starter.bolt.TotalRankingsBolt;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Resource;

/**
 * http://www.cnblogs.com/swanspouse/p/5130117.html
 * http://www.michael-noll.com/blog/2013/01/18/implementing-real-time-trending-topics-in-storm/
 * Created by xiaolong.qiu on 2017/3/28.
 */
@Configuration
@DependsOn({"splitSentenceBolt","wordCountBolt","kafkaProducerSpout"})
public class DemoTopologyBuilder {

    @Autowired
    private KafkaProducerSpoutBuilder kafkaProducerSpoutBuilder;
    @Autowired
    private KafkaProducerSpout kafkaProducerSpout;

    @Autowired
    private RollingWordCountBoltBuilder rollingWordCountBoltBuilder;
    @Resource(name = "rollingWordCountBolt")
    private RollingCountBolt rollingWordCountBolt;

    @Autowired
    private IntermediateRankingsWordCountBoltBuilder intermediateRankingsWordCountBoltBuilder;
    @Resource(name = "intermediateRankingsWordCountBolt")
    private IntermediateRankingsBolt intermediateRankingsWordCountBolt;

    @Autowired
    private TotalRankingsWordCountBoltBuilder totalRankingsWordCountBoltBuilder;
    @Resource(name = "totalRankingsWordCountBolt")
    private TotalRankingsBolt totalRankingsWordCountBolt;


/*    @Autowired
    private KafkaSpout kafkaSpout;
    @Autowired
    private KafkaSpoutBuilder kafkaSpoutBuilder;*/

    @Autowired
    private KafkaSpout kafkaSpout;
    @Autowired
    private KafkaClientSpoutBuilder kafkaSpoutBuilder;

    @Autowired
    private SplitSentenceBolt splitSentenceBolt;
    @Autowired
    private SplitSentenceBoltBuilder splitSentenceBoltBuilder;

    @Autowired
    private WordCountBolt wordCountBolt;
    @Autowired
    private WordCountBoltBuilder wordCountBoltBuilder;

    @Autowired
    private WordCountToMySQLBoltBuilder wordCountToMySQLBoltBuilder;
    @Resource(name = "wordCountToMySQLBolt")
    private JdbcInsertBolt wordCountToMySQLBolt;

    @Autowired
    private WordCountToRedisBoltBuilder wordCountToRedisBoltBuilder;
    @Autowired
    private WordCountToRedisBolt wordCountToRedisBolt;

    @Autowired
    private WordCountTopNToRedisBoltBuilder wordCountTopNToRedisBoltBuilder;
    @Autowired
    private WordCountTopNToRedisBolt wordCountTopNToRedisBolt;

    /**
     * 模拟一个监控系统的实现
     * @return
     */
    @Bean
    public TopologyBuilder buildTopology() {
        TopologyBuilder builder = new TopologyBuilder();
        //产生随机句子写入kafka（用于模拟实际生产环境中生产者往Kafka写消息的动作）
        builder.setSpout(kafkaProducerSpoutBuilder.getId(), kafkaProducerSpout, kafkaProducerSpoutBuilder.getParallelismHint());

        //统计WordCount
        //读取kafka作为数据源，输入句子给下游
        builder.setSpout(kafkaSpoutBuilder.getId(), kafkaSpout, kafkaSpoutBuilder.getParallelismHint());


        //将句子拆分成单词，上游数据源为kafka spout（实际上的业务日志清洗规则）
        builder.setBolt(splitSentenceBoltBuilder.getId(), splitSentenceBolt, splitSentenceBoltBuilder.getParallelismHint())
                .shuffleGrouping(kafkaSpoutBuilder.getId());

        /**
         * 滑动窗口统计的单词计数，上游数据源为分词bolt。数据分组为fieldsGrouping，在这里即同样的单词分给同一个下游bolt处理
         * rollingWordCountBolt发射的数据：遍历Map<Object, Long>，然后collector.emit(new Values(obj, count, actualWindowLengthInSeconds)) 即将每个对象计数分别往下游发送
         */
        builder.setBolt(rollingWordCountBoltBuilder.getId(),rollingWordCountBolt,rollingWordCountBoltBuilder.getParallelismHint())
                .fieldsGrouping(splitSentenceBoltBuilder.getId(),new Fields("word"));

        /**
         * 下游收到滑动窗口的数据并写入Redis。那么此时在Redis的数据便是滑动窗口的实时数据了。前台页面直接定时轮询key即可实现实时监控。
         * Redis的话，个人认为可不使用FieldGrouping，反正是单线程。
         */
        builder.setBolt(wordCountToRedisBoltBuilder.getId(),wordCountToRedisBolt,wordCountToRedisBoltBuilder.getParallelismHint())
                .shuffleGrouping(rollingWordCountBoltBuilder.getId());



        //分词统计数据发送，相同的单词发送到相同的bolt，上游数据源为分词bolt。每天汇总一次数据，
        builder.setBolt(wordCountBoltBuilder.getId(), wordCountBolt, wordCountBoltBuilder.getParallelismHint())
                .fieldsGrouping(splitSentenceBoltBuilder.getId(), new Fields("word"));

        //同样的单词分到一个bolt处理（防止多个bolt的sql connection处理同一个单词），分词数据写入Mysql
        builder.setBolt(wordCountToMySQLBoltBuilder.getId(),wordCountToMySQLBolt,wordCountToMySQLBoltBuilder.getParallelismHint())
                .fieldsGrouping(wordCountBoltBuilder.getId(), new Fields("word"));


        //滑动窗口topN，注意：根据观察（还没看源码）此处TOPN是累计TOPN，而且如果掉出了TOPN，会导致计数清零。
        /**
         * 产生中间数据Rankings，就是统计分配在每个bolt实例里面的单词的Rankings。这里的聚合操作类似于Hadoop的combiner。（Mapper端的reduce）
         * 同一个对象（"obj"域）需要发送到同一个Bolt求Rankings。
         * {@link org.apache.storm.starter.bolt.IntermediateRankingsBolt}接收的Tuple格式为：(object, object_count, additionalField1,additionalField2, ..., additionalFieldN)，并且根据object_count对object进行排序
         * {@link org.apache.storm.starter.tools.Rankings}:自然降序排列
         * {@link org.apache.storm.starter.tools.Rankable}:可自然降序排列接口
         * {@link org.apache.storm.starter.tools.RankableObjectWithFields}:RankableObjectWithFields是Rankable的实现类，根据Fields实现Rank
         */
        builder.setBolt(intermediateRankingsWordCountBoltBuilder.getId(), intermediateRankingsWordCountBolt, intermediateRankingsWordCountBoltBuilder.getParallelismHint())
                .fieldsGrouping(rollingWordCountBoltBuilder.getId(),new Fields("obj"));
        /**
         * 将IntermediateRankingsBolt统计的Rankings全部汇聚于一个bolt实例（TotalRankingsBolt）进行统一的Rankings统计。类似与hadoop的reduce。
         * globalGrouping意为将数据分配给同一个task处理。
         * {@link org.apache.storm.starter.bolt.TotalRankingsBolt} 合并Rankings
         * PS：这里是汇聚每个IntermediateRankingsBolt上的Rankings。所以需要通过globalGrouping保证所有Tuple发送给一个bolt的一个task处理（保证所有数据进入同一个bolt实例）
         * TODO: 那么ParallelismHint对于globalGrouping是否就是没有用了呢？
         */
        builder.setBolt(totalRankingsWordCountBoltBuilder.getId(), totalRankingsWordCountBolt,totalRankingsWordCountBoltBuilder.getParallelismHint())
                .globalGrouping(intermediateRankingsWordCountBoltBuilder.getId());
        /**
         * 滑动窗口topN统计写入Redis
         */
        builder.setBolt(wordCountTopNToRedisBoltBuilder.getId(), wordCountTopNToRedisBolt,wordCountTopNToRedisBoltBuilder.getParallelismHint())
                .globalGrouping(totalRankingsWordCountBoltBuilder.getId());

        return builder;
    }


}
