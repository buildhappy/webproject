package cn.itcast.annotation2;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.sql.DataSource;

public class DaoFactory2 {
	
	public static BookDao createBookDao(){
		
		BookDao dao = new BookDao();
		
		Field fields[] = dao.getClass().getDeclaredFields();
		for(int i=0;fields!=null && i<fields.length;i++){
			Field f = fields[i];
			f.setAccessible(true);
			Inject inject = f.getAnnotation(Inject.class);
			if(inject==null){
				continue;
			}
			
			//代表当前获取到的字段上有Inject这个注解，则用注解信息，创建一个池赋到字段上
			try{
				//创建字段需要的连接池
				DataSource ds = (DataSource) f.getType().newInstance();  
				
				//用注解的信息，配置上面创建的池
				inject2Datasource(inject,ds);
				
				f.set(dao, ds);
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return dao;
	}

	//用注解的信息，配置池
	private static void inject2Datasource(Inject inject, DataSource ds) {
		Method methods[] = inject.getClass().getMethods();
		for(Method method : methods){
			String name = method.getName();                     //得到注解的每一方法，例如（jdbcUrl(),user(),password(),toString(),hashcode()）
			
			//获取ds上与方法名相应的属性
			try{
				PropertyDescriptor pd = new PropertyDescriptor(name,ds.getClass());
				Object value = method.invoke(inject, null);   //得到注解属性的值
				
				//把值赋到ds的属性上
				pd.getWriteMethod().invoke(ds, value);
			}catch (Exception e) {
				continue;
			}
			
		}
		
		
	}
	
}
