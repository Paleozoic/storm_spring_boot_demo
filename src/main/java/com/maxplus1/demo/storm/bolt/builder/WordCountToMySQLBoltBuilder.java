package com.maxplus1.demo.storm.bolt.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.maxplus1.demo.storm.props.MySQLProps;
import lombok.Getter;
import lombok.Setter;
import org.apache.storm.jdbc.bolt.JdbcInsertBolt;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.mapper.JdbcMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Types;
import java.util.List;
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
        ConnectionProvider connectionProvider = new HikariCPConnectionProvider(hikariConfigMap);

        List<Column> columnSchema = Lists.newArrayList(
                new Column("targetDate", Types.DATE),
                new Column("word", java.sql.Types.VARCHAR),
                new Column("count", Types.BIGINT),
                new Column("count_0", Types.BIGINT)
        );
//        JdbcMapper simpleJdbcMapper = new SimpleJdbcMapper(tableName, connectionProvider);
        JdbcMapper simpleJdbcMapper = new SimpleJdbcMapper(columnSchema);
        JdbcInsertBolt insertBolt = new JdbcInsertBolt(connectionProvider, simpleJdbcMapper)
                .withInsertQuery(insertQuery)
                .withQueryTimeoutSecs(queryTimeoutSecs);
        return insertBolt;
    }
}
