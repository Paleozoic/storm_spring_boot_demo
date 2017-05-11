package com.maxplus1.demo.storm.bolt.builder;

import com.maxplus1.demo.storm.bolt.WordCountBolt;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiaolong.qiu on 2017/3/29.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storm.bolt.wordCountBolt")
public class WordCountBoltBuilder extends BoltBuilder {

    private int emitFrequencyInSeconds;

    @Bean("wordCountBolt")
    public WordCountBolt buildBolt() {
        super.setId("wordCountBolt");
        WordCountBolt wordCountBolt = new WordCountBolt();
        wordCountBolt.setEmitFrequencyInSeconds(emitFrequencyInSeconds);
        return wordCountBolt;
    }
}
