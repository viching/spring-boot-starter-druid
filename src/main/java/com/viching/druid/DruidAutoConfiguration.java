package com.viching.druid;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.alibaba.druid.pool.DruidDataSource;
import com.viching.druid.configuration.DataSourceKey;
import com.viching.druid.configuration.DynamicDataSourceAspect;
import com.viching.druid.configuration.DynamicDataSourceContextHolder;
import com.viching.druid.configuration.DynamicRoutingDataSource;
/**
 * 数据库连接池自动化配置
 * 
 * @project spring-boot-starter-druid
 * @author Administrator
 * @date 2018年10月9日
 * Copyright (C) 2016-2020 www.viching.com Inc. All rights reserved.
 */
@Configuration
@EnableConfigurationProperties(DruidProperties.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Import({DynamicDataSourceAspect.class})
public class DruidAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DruidAutoConfiguration.class);

    @Autowired
    private DruidProperties properties;

    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource() {
    	List<DataSource> list = dataSource();
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>(0);
        
        for(int i = 0; i < list.size(); i++){
        	if(i == 0){
        		dataSourceMap.put(DataSourceKey.MASTER.name(), list.get(i));
        	}else{
        		dataSourceMap.put(DataSourceKey.SLAVE.name()+(i-1), list.get(i));
        	}
        }

        // Set master datasource as default
        dynamicRoutingDataSource.setDefaultTargetDataSource(list.get(0));
        // Set master and slave datasource as target datasource
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);

        // To put datasource keys into DataSourceContextHolder to judge if the datasource is exist
        DynamicDataSourceContextHolder.dataSourceKeys.addAll(dataSourceMap.keySet());

        // To put slave datasource keys into DataSourceContextHolder to load balance
        DynamicDataSourceContextHolder.slaveDataSourceKeys.addAll(dataSourceMap.keySet());
        DynamicDataSourceContextHolder.slaveDataSourceKeys.remove(DataSourceKey.MASTER.name());
        return dynamicRoutingDataSource;
    }
    
    @SuppressWarnings("resource")
	private List<DataSource> dataSource() {
    	List<DataSource> list = new ArrayList<DataSource>();
    	DruidDataSource dataSource = null;
    	for(int i=0; i< properties.getList().size(); i++){
    		dataSource = new DruidDataSource();
    		dataSource.setDriverClassName(properties.getDriverClass());
            dataSource.setUrl(properties.getList().get(i).getUrl());
            dataSource.setUsername(properties.getList().get(i).getUsername());
            dataSource.setPassword(properties.getList().get(i).getPassword());
            if (properties.getInitialSize() > 0) {
                dataSource.setInitialSize(properties.getInitialSize());
                logger.info("setInitialSize --->" + properties.getInitialSize());
            }
            if (properties.getMinIdle() > 0) {
                dataSource.setMinIdle(properties.getMinIdle());
                logger.info("setInitialSize --->" + properties.getInitialSize());
            }
            if (properties.getMaxActive() > 0) {
                dataSource.setMaxActive(properties.getMaxActive());
                logger.info("setMaxActive --->" + properties.getMaxActive());
            }
            if (properties.getTestOnBorrow() != null) {
                dataSource.setTestOnBorrow(properties.getTestOnBorrow());
                logger.info("setTestOnBorrow --->" + properties.getTestOnBorrow());
            }
            if (properties.getMaxWait() > 0) {
                dataSource.setMaxWait(properties.getMaxWait());
                logger.info("setMaxWait --->" + properties.getMaxWait());
            }
            if (properties.getTimeBetweenEvictionRunsMillis() > 0) {
                dataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
                logger.info("setTimeBetweenEvictionRunsMillis --->" + properties.getTimeBetweenEvictionRunsMillis());
            }
            if (properties.getMinEvictableIdleTimeMillis() > 0) {
                dataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
                logger.info("setMinEvictableIdleTimeMillis --->" + properties.getMinEvictableIdleTimeMillis());
            }
            if (properties.getValidationQuery() != null) {
                dataSource.setValidationQuery(properties.getValidationQuery());
                logger.info("setValidationQuery --->" + properties.getValidationQuery());
            }
            if (properties.getTestWhileIdle() != null) {
                dataSource.setTestWhileIdle(properties.getTestWhileIdle());
                logger.info("setTestWhileIdle --->" + properties.getTestWhileIdle());
            }
            if (properties.getTestOnReturn() != null) {
                dataSource.setTestOnReturn(properties.getTestOnReturn());
                logger.info("setTestOnReturn --->" + properties.getTestOnReturn());
            }
            if (properties.getPoolPreparedStatements() != null) {
                dataSource.setPoolPreparedStatements(properties.getPoolPreparedStatements());
                logger.info("setPoolPreparedStatements --->" + properties.getPoolPreparedStatements());
            }
            if (properties.getMaxPoolPreparedStatementPerConnectionSize() > 0) {
                dataSource.setMaxPoolPreparedStatementPerConnectionSize(properties.getMaxPoolPreparedStatementPerConnectionSize());
                logger.info("setMaxPoolPreparedStatementPerConnectionSize --->" + properties.getMaxPoolPreparedStatementPerConnectionSize());
            }
            if (properties.getFilters() != null) {
                try {
                    dataSource.setFilters(properties.getFilters());
                    logger.info("setFilters --->" + properties.getFilters());
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("setInitialSize error");
                }
            }
            if (properties.getConnectionProperties() != null) {
                dataSource.setConnectionProperties(properties.getConnectionProperties());
                logger.info("setConnectionProperties --->" + properties.getConnectionProperties());
            }

            try {
                dataSource.init();
                logger.info("dataSource.init()");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            
            list.add(dataSource);
    	}
    	return list;
    }
}
