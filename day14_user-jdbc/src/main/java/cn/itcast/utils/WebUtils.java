package cn.itcast.utils;

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
	                                                           // RegisterForm.class
	public static <T> T request2Bean(HttpServletRequest request,Class<T> beanCalss){
		
		try{
			//1.创建要封装数据的bean
			T bean = beanCalss.newInstance();
			
			//2.把request中的数据整到bean中
			Enumeration  e = request.getParameterNames();
			while(e.hasMoreElements()){
				String name = (String) e.nextElement();   //username password email birthday
				String value = request.getParameter(name); //aaaa    123      aa@
				BeanUtils.setProperty(bean, name, value);
			}
			return bean;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	private String username;
	private String password;
	private String password2;
	private String email;
	private String birthday;
	private String nickname;
	private Map errors = new HashMap();
	
	private String id;
	private String username;
	private String password;
	private String email;
	private Date birthday;
	private String nickname;
		 */
	
	public static void copyBean(Object src,Object dest){
		//注册日期转换器
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
			BeanUtils.copyProperties(dest, src);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	//产生全球唯一的id
	public static String generateID(){
		return UUID.randomUUID().toString();
	}
	
}
