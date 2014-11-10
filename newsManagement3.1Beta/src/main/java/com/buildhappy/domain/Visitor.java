package com.buildhappy.domain;
/**
 * 新闻访问者的实体类
 * @author Administrator
 *
 */
import java.util.Date;

import org.springframework.stereotype.Repository;

@Repository
public class Visitor {
	private String name;
	private String password;
	private Date visitTime;
//TODO	private LoginLog log;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getVisitTime() {
		return visitTime;
	}
	public void setVisitTime(Date visitTime) {
		this.visitTime = visitTime;
	}
	
	
	
}
