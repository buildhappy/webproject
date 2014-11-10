package itcast.jdk5demo3.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
/**
 * 解剖类的方法（用getMethod()、getDeclaredMethod()方法）
 * @author buildhappy
 *
 */
public class Demo2 {

	//解剖出：public void doaa()
	@Test
	public void test1() throws Exception{
		
		Person p = new Person();//解剖之前先加载类
		
		Class clazz = Class.forName("itcast.jdk5demo.reflect.Person");
		Method method = clazz.getMethod("doaa", null);  //getMethod仅得到公有方法
		method.invoke(p, null);//第一个参数表示调用该对象的方法，第二个参数传递原函数的参数
	}
	
	//解剖出：public void dobb(String arg)
	@Test
	public void test2() throws Exception{
		Person p = new Person();
		Class clazz = Class.forName("itcast.jdk5demo.reflect.Person");
		Method method = clazz.getMethod("dobb", String.class);
		method.invoke(p, "aaa");
	}
	
	//解剖出：public String docc(String arg,List list)
	@Test
	public void test3() throws Exception{
		Person p = new Person();
		Class clazz = Class.forName("itcast.jdk5demo.reflect.Person");//加载类的字节码
		
		Method method = clazz.getMethod("docc",String.class,List.class);
		String result = (String) method.invoke(p, "xxx",new ArrayList());
		System.out.println(result);
	}
	
	
	//解剖出：private void doee(Collection coll)
	@Test
	public void test4() throws Exception{
		Person p = new Person();
		Class clazz = Class.forName("itcast.jdk5demo.reflect.Person");
		
		//Method method = clazz.getMethod("doee",String.class,List.class); //public
		Method method = clazz.getDeclaredMethod("doee", Collection.class);
		method.setAccessible(true);  //暴力反射(打开私有方法的访问权限)
		method.invoke(p, new ArrayList());
	}
	
	//解剖出：public static void dodd(int i){
	@Test
	public void test5() throws Exception{
		
		Class clazz = Class.forName("itcast.jdk5demo.reflect.Person");
		Method method = clazz.getMethod("dodd", int.class);
		method.invoke(null, 1);//静态方法不需要传对象可用null代替，也可以传对象
	}
	
	//解剖出：public static void main(String[] args) 
	@Test
	public void test6() throws Exception{
		Class clazz = Class.forName("itcast.jdk5demo.reflect.Person");
		Method method = clazz.getMethod("main", String[].class);
		method.invoke(null, (Object)new String[]{"aa","bb"});//版本问题
		
		//jdk1.4 以前  Method.invoke(Object obj,Object args[])
			//method.invoke(null,new String[]{"aa" , "bb"});//不可以。这样传参数时，在jdk1.5之后会将其拆成两个String变量，出错
			//method.invoke(null, new Object[]{new String[]{"aa" , "bb"}});//该方法可以，拆完后又将其放到一个对象数组中。
		//jdk1.5 以后  Method.invoke(Object obj,Object ... args)
	}
}
