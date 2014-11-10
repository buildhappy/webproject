package com.buildhappy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.buildhappy.dao.LoginLogDao;
import com.buildhappy.dao.UserDao;
import com.buildhappy.domain.LoginLog;
import com.buildhappy.domain.User;

@Service
public class UserService {
	@Autowired
	private UserDao userDao;
	@Autowired
	private LoginLogDao loginLogDao;
	
	public boolean hasMatchUser(String userName , String password){
		int matchCount = userDao.getMatchCount(userName, password);
		return matchCount > 0;
	}
	
	public User findUserByName(String userName){
		return userDao.findUserByUserName(userName);
	}
	
	public void loginSuccess(User user){
		LoginLog loginLog = new LoginLog();
		loginLog.setUserId(user.getUserId());
		loginLog.setIp(user.getLastIp());
		loginLog.setLoginDate(user.getLastVisit());
		loginLogDao.inserLoginLog(loginLog);
	}
}
