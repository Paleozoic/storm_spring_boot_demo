package com.maxplus1.demo.storm.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiaolong.qiu on 2017/4/5.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storm.datasource.mysql")
public class MySQLProps {

    private String dataSourceClassName;
    private String dataSourceUrl;
    private String dataSourceUser;
    private String dataSourcePassword;
}
