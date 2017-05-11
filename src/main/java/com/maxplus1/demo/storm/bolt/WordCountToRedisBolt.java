package com.maxplus1.demo.storm.bolt;

import com.maxplus1.demo.config.redis.RedisConfUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.springframework.data.redis.core.HashOperations;

import java.util.Map;

/**
 * http://storm.apache.org/releases/1.1.0/storm-redis.html
 * Storm官方在1.x之后提供了AbstractRedisBolt和相关实现类来处理redis的操作
 * Created by xiaolong.qiu on 2017/3/28.
 */
@Slf4j
public class WordCountToRedisBolt extends BaseBasicBolt {


    private final static String WORD_COUNT_REAL_TIME_KEY = "WORD_COUNT_REAL_TIME";

    private HashOperations<String, String, Long> hashOperations;
    @Setter
    private byte[] redisProperties;


    /**
     * prepare用来处理一些无法序列化的对象，或者说是有状态的对象，比如数据库连接，redis连接等。
     * 因为Storm Master分发代码是通过将Bolt序列化的方式分发。而不是通过分发整个应用代码的方式分发。
     * 序列化的好处是兼容其他语言的处理。
     *
     * @param stormConf 由 {@link org.apache.storm.Config#put(Object, Object)} 传入
     * @param context
     */
    public void prepare(Map stormConf, TopologyContext context) {
        this.hashOperations = RedisConfUtils.buildRedisTemplate(redisProperties).opsForHash();
    }


    @Override
    public void execute(Tuple input, BasicOutputCollector basicOutputCollector) {
        hashOperations.put(WORD_COUNT_REAL_TIME_KEY, input.getStringByField("obj"), input.getLongByField("count"));
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

}
