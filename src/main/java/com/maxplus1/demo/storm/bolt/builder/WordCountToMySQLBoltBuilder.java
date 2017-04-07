package com.maxplus1.demo.storm.bolt.builder;

import com.google.common.collect.Maps;
import com.maxplus1.demo.storm.props.MySQLProps;
import lombok.Getter;
import lombok.Setter;
import org.apache.storm.jdbc.bolt.JdbcInsertBolt;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.mapper.JdbcMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by xiaolong.qiu on 2017/4/6.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storm.bolt.wordCountToMySQLBolt")
public class WordCountToMySQLBoltBuilder extends BoltBuilder {

    @Autowired
    private MySQLProps mySQLProps;

    private String tableName;
    private String insertQuery;
    private int queryTimeoutSecs = 30;

    @Bean("wordCountToMySQLBolt")
    public JdbcInsertBolt buildBolt() {

        super.setId("wordCountToMySQLBolt");

        Map hikariConfigMap = Maps.newHashMap();
        hikariConfigMap.put("dataSourceClassName", mySQLProps.getDataSourceClassName());
        hikariConfigMap.put("dataSource.url", mySQLProps.getDataSourceUrl());
        hikariConfigMap.put("dataSource.user", mySQLProps.getDataSourceUser());
        hikariConfigMap.put("dataSource.password", mySQLProps.getDataSourcePassword());
        /*ConnectionProvider connectionProvider = new HikariCPConnectionProvider(hikariConfigMap);

        JdbcMapper simpleJdbcMapper = new SimpleJdbcMapper(tableName, connectionProvider);

        JdbcInsertBolt insertBolt1 = new JdbcInsertBolt(connectionProvider, simpleJdbcMapper)
                .withTableName(tableName)
                .withQueryTimeoutSecs(queryTimeoutSecs);
        JdbcInsertBolt insertBolt2 = new JdbcInsertBolt(connectionProvider, simpleJdbcMapper)
                .withInsertQuery(insertQuery)
                .withQueryTimeoutSecs(queryTimeoutSecs);*/
        return null;
    }
}
