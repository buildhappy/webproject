package cn.itcast.generic;
/**
 * 基本的Dao类，所以的Dao类都继承该类
 * 使用了泛型增加了程序的通用性
 */
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import org.hibernate.Session;
import cn.itcast.domain.Category;

//T extends Serializable & Cloneable   限定处理的类型
public class BaseDao<T extends Serializable & Cloneable> {  

	private Session session;
	private Class clazz;
	
//	public BaseDao(Class clazz){
//		this.clazz = clazz;
//	}
	
	public BaseDao(){
		//注意，此次的this指向子类的对象，因为子类在创建对象时会调用父类的无参构造函数
		ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();//BaseDao<Category>
			//this.getClass()通过子类对象获得子类, .getGenericSuperclass()获得父类
			//ParameterizedType参数化的类型
		clazz = (Class) pt.getActualTypeArguments()[0];//获得泛型T
		//System.out.println(this.getClass());
		//System.out.println(clazz);
	}
	
	public void add(T t){
		session.save(t);//hibernate自动将数据保存到相应的表中(比如,传Book对象就将其放入book表中)
	}
	
	public T find(String id){
		return (T) session.get(clazz,id);
	}
	
	public void update(T t){
		session.update(t);
	}
	
	public void delete(String id){
		T t = (T) session.get(clazz,id);
		session.delete(t);
	}
	
}
