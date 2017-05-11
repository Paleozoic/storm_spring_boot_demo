package com.maxplus1.demo.storm.bolt;

import com.google.common.collect.Maps;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.storm.Config;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.TupleUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaolong.qiu on 2017/3/28.
 */
@Slf4j
public class WordCountBolt extends BaseBasicBolt {

    /**
     * 每个Bolt用来存放单词计数的Map
     */
    private Map<String, Long> counts = Maps.newHashMap();

    @Setter
    private int emitFrequencyInSeconds;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("targetDate", "word", "count", "count_0"));//定义输出域为word和count和targetDate
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        if (TupleUtils.isTick(tuple)) {
            long targetDate = System.currentTimeMillis();
            //落地，发送给下游写入Mysql
            counts.forEach((k, v) -> {
                basicOutputCollector.emit(new Values(targetDate, k, v, v));
            });
            //清空缓存
            counts.clear();
        } else {
            String word = tuple.getString(0); //分词得到的word，Tuple里面只有1个元素（单词）
            Long count = counts.get(word);
            counts.put(word, count = count == null ? 1 : count + 1);
            log.debug("{}===>>>{}", word, count);
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<String, Object>();
        /**
         * 这里配置TickTuple的发送频率
         */
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequencyInSeconds);
        return conf;
    }
}
