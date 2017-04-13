package com.maxplus1.demo.config.kafka;


import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by xiaolong.qiu on 2017/4/13.
 */
public class DefaultKafkaProducerFactorySerializable<K, V> extends DefaultKafkaProducerFactory<K, V> implements Serializable {
    public DefaultKafkaProducerFactorySerializable(Map configs) {
        super(configs);
    }

    public DefaultKafkaProducerFactorySerializable(Map configs, Serializer keySerializer, Serializer valueSerializer) {
        super(configs, keySerializer, valueSerializer);
    }
}
