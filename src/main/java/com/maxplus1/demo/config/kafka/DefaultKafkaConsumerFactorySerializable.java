package com.maxplus1.demo.config.kafka;


import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by xiaolong.qiu on 2017/4/13.
 */
public class DefaultKafkaConsumerFactorySerializable<K, V> extends DefaultKafkaConsumerFactory<K, V> implements Serializable {


    public DefaultKafkaConsumerFactorySerializable(Map<String, Object> configs) {
        super(configs);
    }

    public DefaultKafkaConsumerFactorySerializable(Map<String, Object> configs, Deserializer<K> keyDeserializer, Deserializer<V> valueDeserializer) {
        super(configs, keyDeserializer, valueDeserializer);
    }
}
