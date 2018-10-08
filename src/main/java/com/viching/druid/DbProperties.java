package com.viching.druid;

/**
 * 数据库连接参数
 * 
 * @project spring-boot-starter-druid
 * @author Administrator
 * @date 2018年10月9日
 * Copyright (C) 2016-2020 www.viching.com Inc. All rights reserved.
 */
public class DbProperties {
	private String url;
	private String username;
	private String password;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
