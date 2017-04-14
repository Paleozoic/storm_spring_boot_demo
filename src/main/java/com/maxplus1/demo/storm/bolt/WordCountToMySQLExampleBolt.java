package com.maxplus1.demo.storm.bolt;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.jdbc.bolt.AbstractJdbcBolt;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.JdbcClient;
import org.apache.storm.jdbc.common.Util;
import org.apache.storm.jdbc.mapper.JdbcMapper;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.ITuple;
import org.apache.storm.tuple.Tuple;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO：待改造
 * 参考自：（一般情况下可以直接使用SimpleJdbcMapper）
 * {@link org.apache.storm.jdbc.bolt.JdbcInsertBolt}
 * http://storm.apache.org/releases/1.1.0/storm-jdbc.html
 * Created by xiaolong.qiu on 2017/3/28.
 */
@Slf4j
@Deprecated
public class WordCountToMySQLExampleBolt extends AbstractJdbcBolt {

    private String tableName;
    private String insertQuery;
    private JdbcMapper jdbcMapper;

    private List<Column> schemaColumns;

    public WordCountToMySQLExampleBolt(ConnectionProvider connectionProvider) {
        super(connectionProvider);
        //获取列元数据
        int queryTimeoutSecs = 30;
        connectionProvider.prepare();
        JdbcClient client = new JdbcClient(connectionProvider, queryTimeoutSecs);
        this.schemaColumns = client.getColumnSchema(tableName);

        /**
         * 参考自：（一般情况下可以直接使用SimpleJdbcMapper）
         * {@link org.apache.storm.jdbc.mapper.SimpleJdbcMapper}
         */
        //根据列元数据定义（列名和类型）来定义jdbcMapper
        JdbcMapper jdbcMapper = new JdbcMapper() {
            @Override
            public List<Column> getColumns(ITuple tuple) {
                List<Column> columns = new ArrayList<Column>();
                for(Column column : schemaColumns) {
                    String columnName = column.getColumnName();
                    Integer columnSqlType = column.getSqlType();

                    if(Util.getJavaType(columnSqlType).equals(String.class)) {
                        String value = tuple.getStringByField(columnName);
                        columns.add(new Column(columnName, value, columnSqlType));
                    } else if(Util.getJavaType(columnSqlType).equals(Short.class)) {
                        Short value = tuple.getShortByField(columnName);
                        columns.add(new Column(columnName, value, columnSqlType));
                    } else if(Util.getJavaType(columnSqlType).equals(Integer.class)) {
                        Integer value = tuple.getIntegerByField(columnName);
                        columns.add(new Column(columnName, value, columnSqlType));
                    } else if(Util.getJavaType(columnSqlType).equals(Long.class)) {
                        Long value = tuple.getLongByField(columnName);
                        columns.add(new Column(columnName, value, columnSqlType));
                    } else if(Util.getJavaType(columnSqlType).equals(Double.class)) {
                        Double value = tuple.getDoubleByField(columnName);
                        columns.add(new Column(columnName, value, columnSqlType));
                    } else if(Util.getJavaType(columnSqlType).equals(Float.class)) {
                        Float value = tuple.getFloatByField(columnName);
                        columns.add(new Column(columnName, value, columnSqlType));
                    } else if(Util.getJavaType(columnSqlType).equals(Boolean.class)) {
                        Boolean value = tuple.getBooleanByField(columnName);
                        columns.add(new Column(columnName, value, columnSqlType));
                    } else if(Util.getJavaType(columnSqlType).equals(byte[].class)) {
                        byte[] value = tuple.getBinaryByField(columnName);
                        columns.add(new Column(columnName, value, columnSqlType));
                    } else if(Util.getJavaType(columnSqlType).equals(Date.class)) {
                        Long value = tuple.getLongByField(columnName);
                        columns.add(new Column(columnName, new Date(value), columnSqlType));
                    } else if(Util.getJavaType(columnSqlType).equals(Time.class)) {
                        Long value = tuple.getLongByField(columnName);
                        columns.add(new Column(columnName, new Time(value), columnSqlType));
                    } else if(Util.getJavaType(columnSqlType).equals(Timestamp.class)) {
                        Long value = tuple.getLongByField(columnName);
                        columns.add(new Column(columnName, new Timestamp(value), columnSqlType));
                    } else {
                        throw new RuntimeException("Unsupported java type in tuple " + Util.getJavaType(columnSqlType));
                    }
                }
                return columns;
            }
        };
        this.jdbcMapper = jdbcMapper;
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            List e = this.jdbcMapper.getColumns(tuple);
            ArrayList columnLists = new ArrayList();
            columnLists.add(e);
            if(!StringUtils.isBlank(this.tableName)) {
                this.jdbcClient.insert(this.tableName, columnLists);
            } else {
                this.jdbcClient.executeInsertQuery(this.insertQuery, columnLists);
            }

            this.collector.ack(tuple);
        } catch (Exception e) {
            this.collector.reportError(e);
            this.collector.fail(tuple);
        }
    }

    @Override
    protected void process(Tuple tuple) {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
