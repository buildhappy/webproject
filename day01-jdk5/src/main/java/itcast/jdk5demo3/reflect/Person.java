package itcast.jdk5demo3.reflect;

import java.util.Collection;
import java.util.List;

public class Person {  //class

	public String name;   //字段
	private String password;
	private List list;
	private static String address;
	
	
	public static String getAddress(){
		return address;
	}
	
	public List getList() {
		return list;
	}

	public Person(){
		System.out.println("person");
	}
	
	public Person(double d,Class clazz){
		System.out.println("dclass");
	}
	
	private Person(int j){
		System.out.println("j");
	}
	
	public void doaa(){
		System.out.println("run!!");
	}
	
	public void dobb(String arg){
		System.out.println(arg);
	}
	
	public void dobb(int arg){
		System.out.println("wowow");
	}
	
	public String docc(String arg,List list){
		
		return "aa";
	}
	
	private void doee(Collection coll){
		System.out.println("dd");
	}
	
	
	public static void dodd(int i){
		System.out.println("cc");
	}
	
	public static void main(String[] args) {
		System.out.println("main!!!");
	}
	

}
