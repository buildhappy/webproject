package junit.test;
/**
 * 测试service层的BusinessServiceImp类
 */
import java.util.Date;

import org.junit.Test;

import cn.itcast.domain.User;
import cn.itcast.exception.UserExistException;
import cn.itcast.service.impl.BusinessServiceImpl;

public class ServiceTest {
	
	@Test
	public void testRegister(){
		
		User user = new User();
		user.setUsername("gggg");
		user.setBirthday(new Date());
		user.setEmail("bb@sina.com");
		user.setId("3424234234");
		user.setNickname("李子");
		user.setPassword("123");
		
		BusinessServiceImpl service = new BusinessServiceImpl();
		try {
			service.register(user);
			System.out.println("注册成功！！");
		} catch (UserExistException e) {
			System.out.println("用户已存在");
		}
	}
	
	@Test
	public void testLogin(){
		BusinessServiceImpl service = new BusinessServiceImpl();
		User user = service.login("gggg", "123");
		System.out.println(user);
	}
}
