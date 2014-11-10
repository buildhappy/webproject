package itcast.jdk5demo4.introspector;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.junit.Test;

public class Demo1 {

	/**通过内省操作类的函数和属性
	 * @param args
	 * @throws Exception 
	 */
	//属性与字段的关系：如果类中的字段有get或set方法，那么该字段就晋升为属性
	
	//获取bean的所有属性
	@Test
	public void test1() throws Exception{
		
		BeanInfo info = Introspector.getBeanInfo(Student.class);
											//对javabean进行内省，返回其属性和公开方法
		PropertyDescriptor[] pds = info.getPropertyDescriptors();
											//获取bean的所有属性
		for(PropertyDescriptor pd : pds){
			System.out.println(pd.getName());
		}
	}
	
	
	//操作bean的属性:为属性赋值（方式一）
	@Test
	public void test2() throws Exception{
		
		Student s = new Student();
		
		BeanInfo info = Introspector.getBeanInfo(Student.class);
		PropertyDescriptor[] pds = info.getPropertyDescriptors();
		for(PropertyDescriptor pd : pds){
			if(pd.getName().equals("password")){
				Method method = pd.getWriteMethod();//获取写方法，setPassword(String password)
				method.invoke(s, "1243");
			}
		}
		System.out.println(s.getPassword());
	}
	
	//操作bean的属性:为属性赋值（方式二）
	@Test
	public void test3() throws Exception{
		Student s = new Student();
		PropertyDescriptor pd = new PropertyDescriptor("password",Student.class);
		Method set = pd.getWriteMethod();
		set.invoke(s, "1243");
		System.out.println(s.getPassword());
		
		//通过内省获取属性值
		Method get = pd.getReadMethod();  //getPassword()
		String password = (String) get.invoke(s, null);
		System.out.println(password);
	}
}
