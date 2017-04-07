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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.maxplus1.demo.dao.test1db"}, sqlSessionFactoryRef = "test1dbSqlSessionFactory")
public class Test1dbConfig {

    private final static Logger log = LoggerFactory.getLogger(Test1dbConfig.class);

    @Primary
    @Bean(name = "test1db")
    @ConfigurationProperties(prefix = "spring.datasource.test1db")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "test1dbTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("test1db") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Primary
    @Bean(name = "test1dbSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("test1db") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTypeAliasesPackage("com.maxplus1.demo.entity");
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mapper/test1db/*.xml"));
        return factoryBean.getObject();
    }
}
