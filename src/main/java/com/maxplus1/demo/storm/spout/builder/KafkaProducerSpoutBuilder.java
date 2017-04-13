package com.maxplus1.demo.storm.spout.builder;

import com.maxplus1.demo.config.kafka.DefaultKafkaProducerFactorySerializable;
import com.maxplus1.demo.storm.spout.KafkaProducerSpout;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

/**
 * Created by xiaolong.qiu on 2017/3/29.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storm.spout.kafkaProducerSpout")
@DependsOn("kafkaProducerFactory")
public class KafkaProducerSpoutBuilder extends SpoutBuilder{

    private String topic;
    @Autowired
    private DefaultKafkaProducerFactorySerializable defaultKafkaProducerFactory;

    @Bean("kafkaProducerSpout")
    public KafkaProducerSpout buildSpout() {
        super.setId("kafkaProducerSpout");
        KafkaProducerSpout kafkaProducerSpout = new KafkaProducerSpout();
        kafkaProducerSpout.setDefaultKafkaProducerFactory(defaultKafkaProducerFactory);
        kafkaProducerSpout.setTopic(topic);
        return kafkaProducerSpout;
    }
}
