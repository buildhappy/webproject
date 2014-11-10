package com.chapter3_03.aopBeforeAdvice;

/**
 * abstract subject role(抽象主题角色)
 * @author Administrator
 *
 */
public interface UserManager {
	public void deleteUser(String name);
	public void insertUser(String name , int age);
	public void updateUser(String name , int age);
	public void selectUser(String name);
}
