package itcast.jdk5demo6.generic;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class Demo3 {

	
	public void test1(){
		
		//在使用泛形时，两边如果同时用到了泛形，两边就必须一致
		//ArrayList<String> list = new ArrayList<Object>();//错误
		//ArrayList<Object> list = new ArrayList<String>(); //错误

	}
	
	@Test
	public void test2(){
		//在使用泛形时，如果只有一边使用到了泛形，这是可以的
		ArrayList<String> list = new ArrayList();
		ArrayList list1 = new ArrayList<String>();
		
		
		list1.add(1);
		System.out.println(list1.get(0));

		
		doaa(new ArrayList());
		dabb(new ArrayList<String>());
	}
	
	
	public void doaa(List<String> list){
		
	}
	
	//sun
	public void dabb(ArrayList list1){
		
	}
	
	//a .class
	public void test3(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("aaa");
	}
	
	
	
}
