package com.buildhappy.service;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import com.buildhappy.domain.User;
import com.buildhappy.service.UserService;

@ContextConfiguration(locations=("/applicationContext.xml"))//启动spring容器
public class UserServiceTest extends AbstractTestNGSpringContextTests{
	@Autowired
	private UserService userService;
	
	@Test
	public void hasMatchUser(){
		boolean b1 = userService.hasMatchUser("admin", "123");
		boolean b2 = userService.hasMatchUser("admin", "1411");
		assertTrue(b1);
		assertTrue(b2);
	}
	
	@Test
	public void findUserByName(){
		User user = userService.findUserByName("admin");
		asserEquals(user.getUserName() , "admin");
	}
}
