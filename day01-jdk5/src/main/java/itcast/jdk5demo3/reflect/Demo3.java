package itcast.jdk5demo3.reflect;

import java.lang.reflect.Constructor;

import org.junit.Test;

/**
 * 解剖构造方法:反射的目的是创建对象（getConstructor()、getDeclaredConstructor()方法）
 * @author buildhappy
 *
 */
public class Demo3 {

	//public Person()
	@Test
	public void test1() throws Exception{
		
		Class clazz = Class.forName("itcast.jdk5demo.reflect.Person");
		Constructor c = clazz.getConstructor(null);
		Person p = (Person) c.newInstance(null);
		System.out.println(p.name);
		
	}
	
	//public Person(double d,Class clazz)
	@Test
	public void test2() throws Exception{
		Class clazz = Class.forName("itcast.jdk5demo.reflect.Person");
		Constructor c = clazz.getConstructor(double.class,Class.class);
		Person p = (Person) c.newInstance(1,String.class);
		System.out.println(p.name);
	}
	
	//private Person(int j)
	@Test
	public void test3() throws Exception{
		Class clazz = Class.forName("itcast.jdk5demo.reflect.Person");
		Constructor c = clazz.getDeclaredConstructor(int.class);
		c.setAccessible(true);
		Person p = (Person) c.newInstance(1);
		System.out.println(p.name);
	}
	
	@Test
	public void test4() throws Exception{
		Class clazz = Class.forName("itcast.jdk5demo.reflect.Person");
		Person p = (Person) clazz.newInstance();  //无参构造方法
		System.out.println(p.name);
	}
}
