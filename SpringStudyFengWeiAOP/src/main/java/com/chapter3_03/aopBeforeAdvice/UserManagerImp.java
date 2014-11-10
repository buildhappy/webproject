package com.chapter3_03.aopBeforeAdvice;

/**
 * real subject role(真实主题)
 * @author Administrator
 *
 */
public class UserManagerImp implements UserManager{
	public void deleteUser(String name){
		System.out.println("deleteUser");
	}
	public void insertUser(String name , int age){
		System.out.println("insertUser");
	}
	public void updateUser(String name , int age){
		System.out.println("updateUser");
	}
	public void selectUser(String name){
		System.out.println("selectUser");
	}

}
