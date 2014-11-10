package com.chapter3_02.jdkDynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkProxy implements InvocationHandler{
	//target object
	private Object targetObject;
	
	/**
	 * 创建代理对象
	 * @param targerObject  目标对象
	 * @return
	 */
	public Object createProxyObject(Object targerObject) {
		this.targetObject=targerObject;
		
		return Proxy.newProxyInstance(targerObject.getClass().getClassLoader(),
				                      targerObject.getClass().getInterfaces(), this);
	}

	/**
	 * @param proxy  :代理对象
	 * @param method :代理对象中方法的封装类
	 * @param args   :方法的参数 
	 * @return
	 * @throws Throwable
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		//securityCheck();
		System.out.println("targetObject:" + proxy.getClass());
		System.out.println("method:" + method.getName());
		System.out.printf("method args:");
		String methodName = method.getName();
		
		//only check the method whose name start with update,pointcut(切入点)
		if(methodName.startsWith("update")){
			securityCheck();//
		}
		for(int i = 0; i< args.length; i++){
			System.out.printf(args[i].toString() + "  ");
		}
		System.out.println();
		Object returnObj = null;
		returnObj = method.invoke(targetObject, args);
		return returnObj;
	}

		//securityCheck is advice(通知)
	private void securityCheck() {
		System.out.println("securityCheck");
		
	}
}
