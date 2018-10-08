package com.viching.druid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 连接池监控配置参数
 * 
 * @project spring-boot-starter-druid
 * @author Administrator
 * @date 2018年10月9日
 * Copyright (C) 2016-2020 www.viching.com Inc. All rights reserved.
 */
@Configuration
@ConfigurationProperties(prefix = "spring.druid.monitor")
public class DruidMonitorProperties {

   private String DruidStatView;
   private String DruidWebStatFilter;

   private String allow;
   private String deny;
   private String loginUsername;
   private String loginPassword;

   private String exclusions;
   private String resetEnable;

    public String getDruidStatView() {
        return DruidStatView;
    }

    public void setDruidStatView(String druidStatView) {
        DruidStatView = druidStatView;
    }

    public String getDruidWebStatFilter() {
        return DruidWebStatFilter;
    }

    public void setDruidWebStatFilter(String druidWebStatFilter) {
        DruidWebStatFilter = druidWebStatFilter;
    }

    public String getAllow() {
        return allow;
    }

    public void setAllow(String allow) {
        this.allow = allow;
    }

    public String getDeny() {
        return deny;
    }

    public void setDeny(String deny) {
        this.deny = deny;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getExclusions() {
        return exclusions;
    }

    public void setExclusions(String exclusions) {
        this.exclusions = exclusions;
    }

    public String getResetEnable() {
        return resetEnable;
    }

    public void setResetEnable(String resetEnable) {
        this.resetEnable = resetEnable;
    }
}
