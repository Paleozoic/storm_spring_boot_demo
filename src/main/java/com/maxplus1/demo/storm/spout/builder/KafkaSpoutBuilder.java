package com.maxplus1.demo.storm.spout.builder;

import com.maxplus1.demo.utils.IDUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.IRichSpout;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiaolong.qiu on 2017/3/29.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storm.spout.kafkaSpout")
public class KafkaSpoutBuilder extends SpoutBuilder{

    private String brokerZkStr;
    private String topic;
    private String zkRoot;


    @Bean("kafkaSpout")
    public KafkaSpout buildSpout() {
        SpoutConfig spoutConf = new SpoutConfig(new ZkHosts(brokerZkStr), topic, zkRoot, IDUtils.getUuid());
        spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new KafkaSpout(spoutConf);
    }
}
