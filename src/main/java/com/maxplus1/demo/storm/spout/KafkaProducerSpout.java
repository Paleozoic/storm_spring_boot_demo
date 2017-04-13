package com.maxplus1.demo.storm.spout;

import com.maxplus1.demo.config.kafka.DefaultKafkaProducerFactorySerializable;
import lombok.Setter;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.utils.Utils;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;
import java.util.Random;

/**
 * Created by xiaolong.qiu on 2017/4/6.
 */
public class KafkaProducerSpout extends BaseRichSpout {

    private Random random;
    private KafkaTemplate<String,String> kafkaTemplate;
    @Setter
    private DefaultKafkaProducerFactorySerializable defaultKafkaProducerFactory;
    @Setter
    private String topic;

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.random = new Random();
        this.kafkaTemplate = new KafkaTemplate<>(defaultKafkaProducerFactory);
    }

    @Override
    public void nextTuple() {
        // 睡眠一段时间后再产生一个数据
        Utils.sleep(100);

        // 句子数组
        String[] sentences = new String[]{ "the cow jumped over the moon", "an apple a day keeps the doctor away",
                "four score and seven years ago", "snow white and the seven dwarfs", "i am at two with nature" };

        // 随机选择一个句子
        String sentence = sentences[random.nextInt(sentences.length)];

        // 将句子写入kafka
        ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(topic, sentence);

        //call back
//        send.addCallback();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
