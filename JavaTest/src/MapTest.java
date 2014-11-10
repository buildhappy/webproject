/*
学生Student类
学生属性：姓名，年龄。
注意：姓名和年龄相同的视为同一个学生。
保证学生的唯一性。
(要保证唯一就要想到Hash表，想到Hash表就要想到hashCode和equals方法)

步骤：
1，描述学生。
2，定义map容器。将学生作为键，地址作为值。存入。
3，获取map集合中的元素。
*/

import java.util.*;
public class MapTest {

	public static void main(String[] args) {
		TreeMap<Student2 , String> map = new TreeMap<Student2 , String>(new MyCompare());
//		map.put("01" , new Student2("wang" , 22));
//		map.put("02" , new Student2("sansan", 12));
//		map.put("03" , new Student2("sfsf", 42));
		map.put(new Student2("wang" , 22) , "01");
		map.put(new Student2("sansan", 12) , "02");
		map.put(new Student2("sfsf", 42) , "03");
		
		Collection<Map.Entry<Student2 , String>> coll = map.entrySet();
		Iterator<Map.Entry<Student2 , String>> it = coll.iterator();
		
		while(it.hasNext()){
			Map.Entry<Student2 , String> entry = it.next();
			Student2 key = entry.getKey();
			String st = entry.getValue();
			System.out.println("key:" + key);
			System.out.println(st);
		}
		
	}
}

class Student2 implements Comparable<Student2>{
	private String name;
	private int age;
	public Student2(String name , int age){
		this.name = name;
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAga(String aga) {
		this.age = age;
	}
	
	
	@Override
	public int hashCode(){
		return name.hashCode() + age * 34;
	}
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Student))
			throw new ClassCastException("class not mathch");
		Student2 st = (Student2)obj;
		return name.equals(st.getName()) && (age == st.getAge());
	}

	@Override
	public int compareTo(Student2 s2) {
		int num = name.compareTo(s2.getName());
		if(num == 0){
			return ((Integer)age).compareTo((Integer)s2.getAge());
		}
		return num;
	}
	
	@Override
	public String toString(){
		return name + "--" + age;
	}
}

//创建自己的比较类
class MyCompare implements Comparator<Student2>{
	@Override
	public int compare(Student2 s1 , Student2 s2){
		int num = s1.getName().compareTo(s2.getName());
		if(num == 0){
			return ((Integer)s1.getAge()).compareTo((Integer)s2.getAge());
		}
		return num;
	}
}