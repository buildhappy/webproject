package com.chapter2_02.constructor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
	public static void main(String[] args){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("com/chapter2_02/constructor/beans.xml");
		Person person = (Person) ctx.getBean("person");
		System.out.println(person.getPname());
		System.out.println(person.getPage());
	}
}
