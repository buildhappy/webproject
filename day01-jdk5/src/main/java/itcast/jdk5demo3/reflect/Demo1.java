package itcast.jdk5demo3.reflect;

public class Demo1 {

	/**
	 * 反射，加载类字节码(类对象)的三种方式
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		//方法一
		Class clazz = Class.forName("itcast.jdk5demo.reflect.Person");//返回类的字节码
		
		//方法二
		Class clazz1 = Person.class;
		
		//方法三
		Class clazz2 = new Person().getClass();
	}

}
