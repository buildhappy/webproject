package com.chapter3_01.staticProxy;
/**
 * proxy subject role(代理 主题角色)
 * @author Administrator
 *
 */
public class UserManagerProxy implements UserManager{
	private UserManager userManager;
	public void deleteUser(String name){
		checkUser();//security check at the beginning
		userManager.deleteUser(name);
	}
	public void insertUser(String name , int age){
		checkUser();
		userManager.insertUser(name, age);
	}
	public void updateUser(String name , int age){
		checkUser();
		userManager.updateUser(name, age);
	}
	public void selectUser(String name){
		checkUser();
		userManager.selectUser(name);
	}
	
	public void checkUser(){
		System.out.println("check user");
	}
}
