package com.buildhappy.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import com.buildhappy.domain.User;

@Repository
public class UserDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
		//根据用户名和密码获取匹配的用户数
	public int getMatchCount(String userName , String password){
		String sql = "select count(*) from t_user where user_name=?and password=?";
		return jdbcTemplate.queryForInt(sql, new Object[]{userName , password});
	}
	
		//根据用户名获取User对象
	public User findUserByUserName(final String userName){
		String sqlStr = "select user_id , user_name from t_user where user_name=?";
		final User user = new User();
		jdbcTemplate.query(sqlStr , new Object[]{userName} , 
						   new RowCallbackHandler(){
							  public void processRow(ResultSet rs)throws SQLException{
								  user.setUserId(rs.getInt("user_id"));
								  user.setUserName(userName);
							  }
						   });
		return user;
	}
	
		//更新用户积分、最后登录的IP以及最后登录时间
	public void updateLoginInfo(User user){
		String sqlStr = "update t_user set last_visit =?,last_ip=? where user_id=?";
		jdbcTemplate.update(sqlStr , new Object[]{user.getLastVisit() , user.getLastIp() , user.getUserId()});
	}
}
