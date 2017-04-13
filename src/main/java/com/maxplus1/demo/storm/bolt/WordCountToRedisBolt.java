package com.maxplus1.demo.storm.bolt;

import com.maxplus1.demo.config.redis.RedisClusterConfigurationSerializable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

/**
 * http://storm.apache.org/releases/1.1.0/storm-redis.html
 * Storm官方在1.x之后提供了AbstractRedisBolt和相关实现类来处理redis的操作
 * Created by xiaolong.qiu on 2017/3/28.
 */
@Slf4j

public class WordCountToRedisBolt extends BaseRichBolt {


    private final static String HASH_KEY = "WORD_COUNT";

    private HashOperations<String,String,Long> hashOperations;
    @Setter
    private RedisClusterConfigurationSerializable redisClusterConfiguration;


    /**
     * prepare用来处理一些无法序列化的对象，或者说是有状态的对象，比如数据库连接，redis连接等。
     * 因为Storm Master分发代码是通过将Bolt序列化的方式分发。而不是通过分发整个应用代码的方式分发。
     * 序列化的好处是兼容其他语言的处理。
     * @param stormConf 由 {@link org.apache.storm.Config#put(Object, Object)} 传入
     * @param context
     * @param collector
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {

//        RedisClusterConfiguration redisClusterConfiguration = (RedisClusterConfiguration) stormConf.get("redisClusterConfiguration");
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration);
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);

        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void execute(Tuple input) {
        hashOperations.put(HASH_KEY,input.getString(0),input.getLong(1));
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
