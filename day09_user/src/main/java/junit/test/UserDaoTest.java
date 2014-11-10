package junit.test;

import java.util.Date;

import org.junit.Test;

import cn.itcast.dao.UserDao;
import cn.itcast.dao.impl.UserDaoXmlImpl;
import cn.itcast.domain.User;

public class UserDaoTest {
	
	@Test
	public void testAdd(){
		User user = new User();
		user.setBirthday(new Date());
		user.setEmail("bb@sina.com");
		user.setId("3424234234");
		user.setNickname("李子");
		user.setPassword("123");
		user.setUsername("bbbb");
		
		UserDao dao = new UserDaoXmlImpl();
		dao.add(user);
	}
	
	@Test
	public void testFind(){
		UserDao dao = new UserDaoXmlImpl();
		dao.find("aaa","123");
	}
	
	@Test
	public void testFindByUsername(){
		UserDao dao = new UserDaoXmlImpl();
		System.out.println(dao.find("aasdfsfda"));
	}
}
