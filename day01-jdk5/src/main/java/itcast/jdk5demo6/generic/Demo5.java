package itcast.jdk5demo6.generic;

/**
 * 自定义泛形(类上声明泛形)  类上面声明的泛形，只对类的非静态成员有效，静态成员无效
 * @author buildhappy
 * @param <T>
 */
public class Demo5<T> {
	
	public  void test1(T t){
		
	}
	
	public T test2(T t){
		
		return null;
	}
	
	public <E> void test3(T t,E e){
		
	}
	
	public static <T> void test4(T t){
		
	}
}
