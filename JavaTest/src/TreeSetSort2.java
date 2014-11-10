import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
public class TreeSetSort2 {
	
	public static void main(String[] args) {
		TreeSet ts = new TreeSet(new MyComparator());
		ts.add(new Person("zhangsan" , 10));
		ts.add(new Person("zhangsan" , 12));
		ts.add(new Person("wanger" , 10));
		Iterator<Object> it = ts.iterator();
		while(it.hasNext()){
			Person st = (Person)it.next();
			System.out.println(st);
		}
	}
}

class Person{
	private String name;
	private int age;
	public Person(String name , int age){
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
	public void setAge(int age) {
		this.age = age;
	}
	
	public String toString(){
		return this.name + "..." + this.age;
	}

	public int compareTo(Object o) {
		if(!(o instanceof Person))
			throw new RuntimeException("Not a Person object");
		Person s = (Person)o;
		if(this.age > s.age) 
			return 1;
		if(this.age == s.age){
			return this.name.compareTo(s.name);
		}
		return -1;
	}
}

class MyComparator implements Comparator{
	public int compare(Object o1 , Object o2){
		Person p1 = (Person)o1;
		Person p2 = (Person)o2;
		
		int num = ((Integer)p1.getAge()).compareTo((Integer)p2.getAge());
		if(num == 0)
			return p1.getName().compareTo(p2.getName());
		
		return num;
	}
}