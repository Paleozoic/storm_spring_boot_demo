package com.maxplus1.demo.utils;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * Created by xiaolong.qiu on 2017/4/14.
 */
public class Serializer {
    public final static GenericJackson2JsonRedisSerializer INSTANCE = new GenericJackson2JsonRedisSerializer();
}
