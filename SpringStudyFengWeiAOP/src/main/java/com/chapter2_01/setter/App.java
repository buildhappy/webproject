package com.chapter2_01.setter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
	public static void main(String[] args){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("com/chapter2_01/setter/beans.xml");
		PersonService personService = (PersonService) ctx.getBean("personService");
		personService.savePerson();
	}
}
