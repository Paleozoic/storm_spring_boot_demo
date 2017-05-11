package com.maxplus1.demo.storm.bolt.builder;

import lombok.Getter;
import lombok.Setter;
import org.apache.storm.starter.bolt.RollingCountBolt;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiaolong.qiu on 2017/4/6.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storm.bolt.rollingWordCountBolt")
public class RollingWordCountBoltBuilder extends BoltBuilder{

    private int windowLengthInSeconds = 300;//时间窗口300s
    private int emitFrequencyInSeconds = 60;//没60s发射一次数据

    @Bean("rollingWordCountBolt")
    public RollingCountBolt buildBolt() {
        super.setId("rollingWordCountBolt");
        RollingCountBolt rollingCountBolt = new RollingCountBolt(windowLengthInSeconds,emitFrequencyInSeconds);
        return rollingCountBolt;
    }
}
