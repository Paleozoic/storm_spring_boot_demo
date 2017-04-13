package com.maxplus1.demo.config.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;

/**
 * Created by xiaolong.qiu on 2017/4/6.
 */
public class RedisConfUtils {

    /**
     * {@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration#getClusterConfiguration}
     * @param redisProperties
     * @return
     */
    public static RedisClusterConfigurationSerializable getRedisClusterConfiguration(RedisProperties redisProperties){
        RedisClusterConfigurationSerializable redisClusterConfiguration = new RedisClusterConfigurationSerializable();

        if (redisProperties.getCluster() == null) {
            return null;
        }
        RedisProperties.Cluster clusterProperties = redisProperties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(
                clusterProperties.getNodes());

        if (clusterProperties.getMaxRedirects() != null) {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        return redisClusterConfiguration;
    }
}
