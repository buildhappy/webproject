package itcast.jdk5demo6.generic;

/**
 * 自定义泛形(方法上声明泛形)
 * @author buildhappy
 *
 */
public class Demo4 {

	public <T> void test1(T t){//定义泛型，用<T>
		
	}
	
	public <T> T test2(T t){//返回值也是泛型数据
		
		return null;
	}
	
	public <T,E> void test3(T t,E e){
		
		
	}
}
