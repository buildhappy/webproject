package cn.itcast.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
public class LiyuchunProxy {
	
	private Person chunchun = new Liyuchun();
	
	
	//Demo----  Person person = LiyuchunProxy.createProxy();   person.sing(”爱你爱你“)  person.dance();
	public Person createProxy(){
		
		return (Person) Proxy.newProxyInstance(LiyuchunProxy.class.getClassLoader(), chunchun.getClass().getInterfaces(),new InvocationHandler(){

			/*
			 * proxy:把代理对象自身传递进来
			 * method:代表当前调用的方法
			 * args:调用方法的参数
			 * 
			 */
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				String methodname = method.getName();
				if(methodname.equals("sing")){
					System.out.println("拿1万刀来！！");
					return method.invoke(chunchun, args);  //谢谢哟！！
				}else if(methodname.equals("dance")){
					System.out.println("拿2万刀来！！");
					return method.invoke(chunchun, args);
				}else{
					System.out.println("春哥不支持这个功能");				
				}
				return null;
			}
			
		});
	}
	
}
