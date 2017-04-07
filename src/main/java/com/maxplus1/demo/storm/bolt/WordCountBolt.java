package com.maxplus1.demo.storm.bolt;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * Created by xiaolong.qiu on 2017/3/28.
 */
@Slf4j
public class WordCountBolt extends BaseBasicBolt {

    /**
     * 每个Bolt用来存放单词计数的Map
     */
    private Map<String, Integer> counts = Maps.newHashMap();

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String word = tuple.getString(0); //分词得到的word，Tuple里面只有1个元素（单词）
        Integer count = counts.get(word);
        counts.put(word, count = count==null?1:count+1);
        log.debug("{}===>>>{}",word,count);
        collector.emit(new Values(word,count));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));//定义输出域为word和count
    }
}
