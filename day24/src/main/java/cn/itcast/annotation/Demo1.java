package cn.itcast.annotation;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked") 
public class Demo1 {

	
	
	@Override
	public boolean equals(@SuppressWarnings("unchecked")  Object obj) {
		return super.equals(obj);
	}
	
	
	@SuppressWarnings("unchecked")   //Demo1.java---->Demo1.class
	public void a(){
		List list = new ArrayList();
		System.out.println(list);
	}
	
	
	@Deprecated
	public void bbbb(){
		
	}
	
	@MyAnnotation1   //继承性
	public void cccc(){
		
	}
	
	@MyAnnotation3(value="aaaa",name="123")
	public void dddd(){
		
	}
}

class D extends Demo1{
	
	public void cccc(){
		
	}
}

/*
  <MyAnnotation1>
  	<name>flx</flx>
  	<paasword>123</password>
  	
  	<child>
  		<name>fsq<name>
  		<age>9<age>
  	</child>
  </MyAnnotation1>
 
 */ 
