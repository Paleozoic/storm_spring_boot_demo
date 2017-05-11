package com.maxplus1.demo.storm.bolt.builder;

import com.maxplus1.demo.storm.bolt.WordCountTopNToRedisBolt;
import com.maxplus1.demo.utils.Serializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Created by xiaolong.qiu on 2017/4/6.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storm.bolt.wordCountTopNToRedisBolt")
@DependsOn("redisTemplate")
public class WordCountTopNToRedisBoltBuilder extends BoltBuilder {

    @Autowired
    private RedisProperties redisProperties;

    @Bean("wordCountTopNToRedisBolt")
    public WordCountTopNToRedisBolt buildBolt() {
        super.setId("wordCountTopNToRedisBolt");
        WordCountTopNToRedisBolt wordCountTopNToRedisBolt = new WordCountTopNToRedisBolt();
        wordCountTopNToRedisBolt.setRedisProperties(Serializer.INSTANCE.serialize(redisProperties));
        return wordCountTopNToRedisBolt;
    }


}
