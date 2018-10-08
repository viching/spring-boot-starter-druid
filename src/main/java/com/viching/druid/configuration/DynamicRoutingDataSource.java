package com.viching.druid.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源配置路由器
 * 
 * @project spring-boot-starter-druid
 * @author Administrator
 * @date 2018年10月9日
 * Copyright (C) 2016-2020 www.viching.com Inc. All rights reserved.
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Set dynamic DataSource to Application Context
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        logger.debug("Current DataSource is [{}]", DynamicDataSourceContextHolder.getDataSourceKey());
        return DynamicDataSourceContextHolder.getDataSourceKey();
    }
}
