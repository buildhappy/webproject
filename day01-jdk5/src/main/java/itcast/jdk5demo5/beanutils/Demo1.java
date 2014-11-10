package itcast.jdk5demo5.beanutils;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.junit.Test;

//import sun.io.Converters;

//注意：使用beanutils要引入commons-beanutils.jar包，该包需要log4j.jar包(commons-logging.jar)的支持

//使用beanUtils框架操作 bean的属性（简化内省方式操作类的函数和属性）
//log4j struts1
@SuppressWarnings("deprecation")
public class Demo1 {

	@Test
	public void test1() throws Exception, InvocationTargetException {
		Student s = new Student();
		BeanUtils.setProperty(s, "name", "aaa");
		System.out.println(s.getName());
	}

	@Test
	public void test2() throws Exception, InvocationTargetException {

		String name = "aaa";
		String password = "123";
		String age = "23";
		String birthday = "1980-09-09";

		Student s = new Student();

		/**
		 *  注册转换器，将字符串类型转换日期的类型
		 *  该类的帮助文档位于commons-beanutils-1.9.1中，不在jdk中
		 *  ConverUtils.register(Converter con , Class<?>) 
		 */
		
		ConvertUtils.register(new Converter() {
			public Object convert(Class type, Object value) {
				if (value == null) {
					return null;
				}
				if (!(value instanceof String)) {
					throw new ConversionException("只支持string类型的转换");
				}
				String date = (String) value;
				if (date.trim().equals("")) {
					return null;
				}

				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//格式转换函数
				try {
					return df.parse(date);
				} catch (ParseException e) {
					throw new ConversionException(e); //将参数e传递进来，这样保证异常链不断
				}
			}
		}, Date.class);

		BeanUtils.setProperty(s, "name", name);//将name的值赋值给s对象的name属性
		BeanUtils.setProperty(s, "password", password);
		BeanUtils.setProperty(s, "age", age); // beanutils框架只支持8种基本数据类型的转换
		BeanUtils.setProperty(s, "birthday", birthday);
											  //date类不属于基本数据类型，要注册一个转化器进行类型装换

		System.out.println(s.getName());
		System.out.println(s.getPassword());
		System.out.println(s.getBirthday());

	}
}
