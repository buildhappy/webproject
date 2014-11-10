package cn.itcast.utils;
/**
 * 为web层提供的工具类
 */
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

public class WebUtils {
	//将form表单提交的数据封装到javabean中                                                           // RegisterForm.class
	public static <T> T request2Bean(HttpServletRequest request,Class<T> beanClass){
		
		try{
			//1.利用反射技术，创建要封装数据的bean，利用泛型避免了强转
			//Object bean = beanClass.newInstance();
			T bean = beanClass.newInstance();
			
			//2.把request中的数据整到bean中
			Enumeration  e = request.getParameterNames();
			while(e.hasMoreElements()){
				String name = (String) e.nextElement();   //username password email birthday
				String value = request.getParameter(name); //aaaa    123      aa@
				BeanUtils.setProperty(bean, name, value);//利用BeanUtils工具给bean赋值
			}
			return bean;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/*src(RegisterForm生成)bean的属性
	private String username;
	private String password;
	private String password2;
	private String email;
	private String birthday;
	private String nickname;
	private Map errors = new HashMap();
	
	dest(数据库中的User)bean的属性
	private String id;
	private String username;
	private String password;
	private String email;
	private Date birthday;
	private String nickname;
	注意二者的birthday的类型不一致需要注册一个日期转换器
	 */
	//将原bean转换到目标bean
	public static void copyBean(Object src,Object dest){
		/**
		 *  注册转换器，将字符串类型转换日期的类型
		 *  该类的帮助文档位于commons-beanutils-1.9.1中，不在jdk中
		 *  ConverUtils.register(Converter con , Class<?>) 
		 */
		ConvertUtils.register(new Converter(){
			public Object convert(Class type, Object value) {
				if(value==null){
					return null;
				}
				String str = (String) value;
				if(str.trim().equals("")){
					return null;
				}
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				try {
					return df.parse(str);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		}, Date.class);
		try {
			BeanUtils.copyProperties(dest, src);//BeanUtils工具会将两个bean同名的属性进行赋值
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	//产生全球唯一的id
	public static String generateID(){
		return UUID.randomUUID().toString();
	}
}
