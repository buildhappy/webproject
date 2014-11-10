package cn.service;

import java.util.ArrayList;
import java.util.List;

import cn.domain.User;



public class BusinessService {

	private static List<User> list = new ArrayList();
	static{
		list.add(new User("aaa","123"));
		list.add(new User("bbb","123"));
		list.add(new User("ccc","123"));
	}
	
	public User login(String username,String password){
		for(User user : list){
			if(user.getUsername().equals(username) && user.getPassword().equals(password)){
				return user;
			}
		}
		return null;
	}
	
	public User findUser(String username){
		for(User user : list){
			if(user.getUsername().equals(username)){
				return user;
			}
		}
		return null;
	}
	
}
