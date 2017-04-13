package com.maxplus1.demo.config.redis;

import org.springframework.data.redis.connection.RedisClusterConfiguration;

import java.io.Serializable;

/**
 * Created by xiaolong.qiu on 2017/4/13.
 */
public class RedisClusterConfigurationSerializable extends RedisClusterConfiguration implements Serializable {
}
