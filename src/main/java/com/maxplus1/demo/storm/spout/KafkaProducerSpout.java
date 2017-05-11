package com.maxplus1.demo.storm.spout;

import lombok.Setter;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.utils.Utils;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by xiaolong.qiu on 2017/4/6.
 */
public class KafkaProducerSpout extends BaseRichSpout {

    //模拟生产数据使用
    private Random random = new Random();
    private static final String[] sentences = new String[]{"床前明月光", "疑是地上霜", "举头望明月", "低头思故乡"};
    private static final AtomicIntegerArray arr = new AtomicIntegerArray(4);

    static {
        //定义计数器，规定每个句子的发射次数
        arr.set(0, 5000);
        arr.set(1, 4000);
        arr.set(2, 3000);
        arr.set(3, 1000);
    }

    private KafkaTemplate<String, String> kafkaTemplate;
    @Setter
    private Map<String, Object> kafkaPropertiesMap;
    @Setter
    private String topic;

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<String, String>(kafkaPropertiesMap));
    }

    @Override
    public void nextTuple() {
        // 睡眠一段时间后再产生一个数据
        Utils.sleep(100);
        // 随机选择一个句子
        int index = random.nextInt(sentences.length);
        int count = arr.decrementAndGet(index);
        if (count >= 0) {
            String sentence = sentences[index];
            // 将句子写入kafka
            kafkaTemplate.send(topic, sentence);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
