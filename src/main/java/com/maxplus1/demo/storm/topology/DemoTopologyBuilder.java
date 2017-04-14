package com.maxplus1.demo.storm.topology;

import com.maxplus1.demo.storm.bolt.SplitSentenceBolt;
import com.maxplus1.demo.storm.bolt.WordCountBolt;
import com.maxplus1.demo.storm.bolt.WordCountToRedisBolt;
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

    @Bean
    public TopologyBuilder buildTopology() {
        TopologyBuilder builder = new TopologyBuilder();
        //产生随机句子写入kafka
        builder.setSpout(kafkaProducerSpoutBuilder.getId(), kafkaProducerSpout, kafkaProducerSpoutBuilder.getParallelismHint());

        //统计WordCount

        //读取kafka作为数据源，输入句子给下游
        builder.setSpout(kafkaSpoutBuilder.getId(), kafkaSpout, kafkaSpoutBuilder.getParallelismHint());


        //将句子拆分成单词，上游数据源为kafka spout
        builder.setBolt(splitSentenceBoltBuilder.getId(), splitSentenceBolt, splitSentenceBoltBuilder.getParallelismHint())
                .shuffleGrouping(kafkaSpoutBuilder.getId());

        //滑动窗口统计的单词计数，上游数据源为分词bolt。数据分组为fieldsGrouping，在这里即同样的单词分给同一个下游bolt处理
        builder.setBolt(rollingWordCountBoltBuilder.getId(),rollingWordCountBolt,rollingWordCountBoltBuilder.getParallelismHint())
                .fieldsGrouping(splitSentenceBoltBuilder.getId(),new Fields("word"));
        //滑动窗口数据写入Redis
        builder.setBolt(wordCountToRedisBoltBuilder.getId(),wordCountToRedisBolt,wordCountToRedisBoltBuilder.getParallelismHint())
                .shuffleGrouping(rollingWordCountBoltBuilder.getId());

        //滑动窗口topN
        /**
         * 产生中间数据Rankings，就是统计分配在每个bolt实例里面的单词的Rankings。这里的聚合操作类似于Hadoop的combiner。（Mapper端的reduce）
         * {@link org.apache.storm.starter.bolt.IntermediateRankingsBolt}接收的Tuple格式为：(object, object_count, additionalField1,additionalField2, ..., additionalFieldN)，并且根据object_count对object进行排序
         * {@link org.apache.storm.starter.tools.SlotBasedCounter}: 基于slot的计数器，Map<T, long[]> objToCounts，long[]维护了每个T的每个slot的计数。 windowLengthInSeconds（时间窗口） emitFrequencyInSeconds（发射频率）
         * {@link org.apache.storm.starter.tools.SlidingWindowCounter}:滑动窗口计数器。每一个时间窗口维护了一个SlotBasedCounter，每隔一个发射频率则相下游发射一个时间窗口的slot。
         * {@link org.apache.storm.starter.tools.Rankings}:自然降序排列
         * {@link org.apache.storm.starter.tools.Rankable}:可自然降序排列接口
         * {@link org.apache.storm.starter.tools.RankableObjectWithFields}:RankableObjectWithFields是Rankable的实现类，根据Fields实现Rank
         */
        builder.setBolt(intermediateRankingsWordCountBoltBuilder.getId(), intermediateRankingsWordCountBolt, intermediateRankingsWordCountBoltBuilder.getParallelismHint())
                .globalGrouping(rollingWordCountBoltBuilder.getId());

        /**
         * 将IntermediateRankingsBolt统计的Rankings全部汇聚于一个bolt实例（TotalRankingsBolt）进行统一的Rankings统计。类似与hadoop的reduce。
         * globalGrouping意为将数据分配给同一个task处理。
         * {@link org.apache.storm.starter.bolt.TotalRankingsBolt} 合并Rankings
         * PS：这里是汇聚每个IntermediateRankingsBolt上的Rankings。所以需要通过globalGrouping保证所有Tuple发送给一个bolt的一个task处理（保证所有数据进入同一个bolt实例）
         */
        builder.setBolt(totalRankingsWordCountBoltBuilder.getId(), totalRankingsWordCountBolt,totalRankingsWordCountBoltBuilder.getParallelismHint())
                .globalGrouping(intermediateRankingsWordCountBoltBuilder.getId());


        //分词统计数据发送，相同的单词发送到相同的bolt，上游数据源为分词bolt
        builder.setBolt(wordCountBoltBuilder.getId(), wordCountBolt, wordCountBoltBuilder.getParallelismHint())
                .fieldsGrouping(splitSentenceBoltBuilder.getId(), new Fields("word"));

        //分词数据写入Mysql
        builder.setBolt(wordCountToMySQLBoltBuilder.getId(),wordCountToMySQLBolt,wordCountToMySQLBoltBuilder.getParallelismHint())
                .shuffleGrouping(wordCountBoltBuilder.getId());

        //所有单词的topN
        builder.setBolt(intermediateRankingsWordCountBoltBuilder.getId()+"_All_Words_topN", intermediateRankingsWordCountBolt, intermediateRankingsWordCountBoltBuilder.getParallelismHint())
                .fieldsGrouping(wordCountBoltBuilder.getId(), new Fields("word"));
        builder.setBolt(totalRankingsWordCountBoltBuilder.getId()+"_All_Words_topN", totalRankingsWordCountBolt,totalRankingsWordCountBoltBuilder.getParallelismHint())
                .globalGrouping(intermediateRankingsWordCountBoltBuilder.getId());

        return builder;
    }


}
