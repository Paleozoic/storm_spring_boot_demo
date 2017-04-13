package com.maxplus1.demo.storm.spout.builder;

import com.maxplus1.demo.storm.spout.KafkaProducerSpout;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Resource;
import java.util.Map;

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
    @Resource(name="kafkaPropertiesMap")
    private Map<String,Object> kafkaPropertiesMap;

    @Bean("kafkaProducerSpout")
    public KafkaProducerSpout buildSpout() {
        super.setId("kafkaProducerSpout");
        KafkaProducerSpout kafkaProducerSpout = new KafkaProducerSpout();
        kafkaProducerSpout.setKafkaPropertiesMap(kafkaPropertiesMap);
        kafkaProducerSpout.setTopic(topic);
        return kafkaProducerSpout;
    }
}
