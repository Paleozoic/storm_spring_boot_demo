package com.maxplus1.demo.config.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.maxplus1.demo.dao.test2db"}, sqlSessionFactoryRef = "test2dbSqlSessionFactory")
public class Test2dbConfig {

    private final static Logger log = LoggerFactory.getLogger(Test2dbConfig.class);

    @Bean(name = "test2db")
    @ConfigurationProperties(prefix = "spring.datasource.test2db")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "test2dbTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("test2db") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "test2dbSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("test2db") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTypeAliasesPackage("com.maxplus1.demo.entity");
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mapper/test2db/*.xml"));
        return factoryBean.getObject();
    }
}
