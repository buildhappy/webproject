package junit.test;

import itcast.Person;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//要引入junit.jar包
public class PersonTest {

	@BeforeClass
	public static void beforeClass(){
		System.out.println("beforeClass");//PersonTest类装载后就运行
	}
	
	@Before
	public void before(){
		System.out.println("before");
	}
	

	@Test
	public void testRun(){
		Person p = new Person();
		p.run();
	}
	
	@Test
	public void testEat(){
		Person p = new Person();
		p.eat();
	}
	
	@Test
	public void testDoxx(){
		Person p = new Person();
		//Assert断言，判断是不是期望的数组
		Assert.assertArrayEquals(new int[]{1,2}, p.doxx());          //[1,2]
		//Assert.assertTrue(false);
	}
	
	@After
	public void after(){
		System.out.println("after");
	}

	@AfterClass
	public static void afterClass(){
		System.out.println("afterClass");
	}
	
}
