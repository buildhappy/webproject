package com.chapter3_02.jdkDynamicProxy;


public class App {
	public static void main(String[] args){
		System.out.println("************** class of UserManager ***************");
		//step1: create the instance of the UserManagerImp
		UserManager userManager = new UserManagerImp();
	
		//step2: create the proxy of the instance(创建目标对象代理)
		JdkProxy proxyInstance = new JdkProxy();
		Object obj = proxyInstance.createProxyObject(userManager);
		UserManager userManagerProxy = (UserManager)obj;
		
			//deleteUser and updateUser are joinPoint(连接点)
		userManagerProxy.deleteUser("Jack");	
		userManagerProxy.updateUser("May" , 12);
		
		System.out.println("************** class of ProductManger ***************");
		ProductManager productManger = new ProductManagerImp();
		
		JdkProxy proxyInstance2 = new JdkProxy();
		Object obj2 = proxyInstance2.createProxyObject(productManger);
		ProductManager productMangerProxy = (ProductManager)obj2;
		productMangerProxy.findProduct("apple");
	}

}
