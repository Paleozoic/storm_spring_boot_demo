package com.maxplus1.demo.config.redis;

import com.maxplus1.demo.utils.Serializer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by xiaolong.qiu on 2017/4/6.
 */
public class RedisConfUtils {

    /**
     * {@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration#getClusterConfiguration}
     * @param redisProperties
     * @return
     */
    public static RedisClusterConfiguration  getClusterConfiguration(RedisProperties redisProperties) {
        if (redisProperties.getCluster() == null) {
            return null;
        }
        RedisProperties.Cluster clusterProperties = redisProperties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(
                clusterProperties.getNodes());

        if (clusterProperties.getMaxRedirects() != null) {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        return config;
    }

    public static RedisTemplate buildRedisTemplate(byte[] redisProperties){
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(
                RedisConfUtils.getClusterConfiguration(
                        (RedisProperties) Serializer.INSTANCE.deserialize(redisProperties)));
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        jedisConnectionFactory.afterPropertiesSet();

        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
