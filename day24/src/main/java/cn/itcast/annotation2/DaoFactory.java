package cn.itcast.annotation2;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DaoFactory {

	public static BookDao createBookDao(){
		
		BookDao dao = new BookDao();
		
		//向dao注入一个池
		
		//解析出dao所有属性
		try{
			BeanInfo info = Introspector.getBeanInfo(dao.getClass(),Object.class);
			PropertyDescriptor pds[] = info.getPropertyDescriptors();
			for(int i=0;pds!=null && i<pds.length;i++){
				//得到bean的每一个属性描述器
				PropertyDescriptor pd = pds[i];
				
				Method setMethod = pd.getWriteMethod();  //得到属性相应的set方法
				
				//看set方法上有没有inject 注解
				Inject inject = setMethod.getAnnotation(Inject.class);
				if(inject==null){
					continue;
				}
				
				//方法有注解，则用注解配置的信息，创建一个连接池
				Class propertyType = pd.getPropertyType();
				Object datasource = propertyType.newInstance();
				DataSource ds = (DataSource)createDataSourceByInject(inject,datasource);
				
				setMethod.invoke(dao, ds);
			}
		}catch (Exception e) {
			throw new RuntimeException();
		}
		
		return dao;
	}

	//用注解的信息，为池配置属性
	private static Object createDataSourceByInject(Inject inject,Object ds) {
		
		//获取到注解所有属性相应的方法     driverClass url equals hashcode
		Method methods[] = inject.getClass().getMethods();
		for(Method m : methods){
			String methodName = m.getName();  //equals  url()  password()
			
			PropertyDescriptor pd = null;
			try {
				pd = new PropertyDescriptor(methodName,ds.getClass());  //geturl
				Object value  = m.invoke(inject, null);  //得到注解属性的值
				pd.getWriteMethod().invoke(ds, value);
			} catch (Exception e) {
				continue;
			}
		}
		
		return ds;
	}
	
}
