package ch09interface;
/**
 * 工厂设计模式
 * @author Administrator
 *
 */
interface Service{
	void method1();
	void method2();
}
class Implementation1 implements Service{
	Implementation1(){}
	public void method1(){System.out.println("Implementation method1");}
	public void method2(){System.out.println("Implementation method2");}
}

/**
 * 工厂接口的作用是获取Service对象
 * @author Administrator
 *
 */
interface ServiceFactory{
	Service getService();
}
class FactoryImplements implements ServiceFactory{
	public Service getService(){
		return new Implementation1();
	}
}


public class P187Factories {
	public static void serviceConsumer(ServiceFactory factory){
		Service s = factory.getService();
		s.method1();
		s.method2();
	}
	public static void main(String[] args){
		serviceConsumer(new FactoryImplements());
	}
}
