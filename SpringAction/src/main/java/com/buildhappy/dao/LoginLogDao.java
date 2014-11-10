package com.buildhappy.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.buildhappy.domain.LoginLog;

@Repository
public class LoginLogDao {
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	  //记录用户的登录日志
	public void inserLoginLog(LoginLog loginLog){
		String slqStr = "insert into t_login_log (user_id , ip , login_datetime) "
						+ "values(? , ? , ?)";
		Object[] args = {loginLog.getUserId() , loginLog.getIp() , loginLog.getLoginDate()};
		jdbcTemplate.update(slqStr , args);
	}
}
