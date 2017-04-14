package com.maxplus1.demo.storm.spout.builder;

import lombok.Getter;
import lombok.Setter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * KafkaClient可以同时订阅多个topic，是新版的Java版本kafka API实现（推荐）
 * 偏移量写回kafka
 * Created by xiaolong.qiu on 2017/3/29.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storm.spout.kafkaClientSpout")
public class KafkaClientSpoutBuilder extends SpoutBuilder {

    private String bootstrapServers;
    private String[] topics;
    private String groupId;


    @Bean("kafkaClientSpout")
    public KafkaSpout buildSpout() {
        super.setId("kafkaClientSpout");
        KafkaSpoutConfig<String, String> kafkaSpoutConfig = getKafkaSpoutConfig();
        return new KafkaSpout(kafkaSpoutConfig);
    }

    private KafkaSpoutConfig<String, String> getKafkaSpoutConfig() {
        KafkaSpoutConfig.Builder<String, String> builder = KafkaSpoutConfig.builder(bootstrapServers, topics);
        builder.setGroupId(groupId);
        return builder.build();
    }

}
