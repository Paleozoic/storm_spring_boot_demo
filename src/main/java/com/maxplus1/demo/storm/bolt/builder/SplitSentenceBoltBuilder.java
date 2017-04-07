package com.maxplus1.demo.storm.bolt.builder;

import com.maxplus1.demo.storm.bolt.SplitSentenceBolt;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiaolong.qiu on 2017/3/29.
 */
@Configuration
@ConfigurationProperties(prefix = "storm.bolt.splitSentenceBolt")
public class SplitSentenceBoltBuilder extends BoltBuilder{
    @Bean("splitSentenceBolt")
    public SplitSentenceBolt buildBolt(){
        super.setId("splitSentenceBolt");
        return new SplitSentenceBolt();
    }
}
