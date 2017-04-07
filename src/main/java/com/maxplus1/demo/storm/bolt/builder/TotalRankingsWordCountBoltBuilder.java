package com.maxplus1.demo.storm.bolt.builder;

import lombok.Getter;
import lombok.Setter;
import org.apache.storm.starter.bolt.TotalRankingsBolt;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiaolong.qiu on 2017/4/7.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storm.bolt.totalRankingsWordCountBolt")
public class TotalRankingsWordCountBoltBuilder extends BoltBuilder {

    private int topN = 10;

    @Bean("totalRankingsWordCountBolt")
    public TotalRankingsBolt buildBolt() {
        super.setId("totalRankingsWordCountBolt");
        return new TotalRankingsBolt(topN);
    }
}
