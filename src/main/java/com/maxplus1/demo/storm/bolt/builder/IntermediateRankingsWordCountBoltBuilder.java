package com.maxplus1.demo.storm.bolt.builder;

import lombok.Getter;
import lombok.Setter;
import org.apache.storm.starter.bolt.IntermediateRankingsBolt;
import org.apache.storm.starter.bolt.TotalRankingsBolt;
import org.apache.storm.topology.IComponent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiaolong.qiu on 2017/4/7.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storm.bolt.intermediateRankingsWordCountBolt")
public class IntermediateRankingsWordCountBoltBuilder extends BoltBuilder {

    private int topN = 10;

    @Bean("intermediateRankingsWordCountBolt")
    public IntermediateRankingsBolt buildBolt() {
        super.setId("intermediateRankingsWordCountBolt");
        return new IntermediateRankingsBolt(topN);
    }
}
