import java.util.Iterator;
import java.util.TreeSet;
public class TreeSetSort1 {

	public static void main(String[] args) {
		TreeSet ts = new TreeSet();
		ts.add(new Student("zhangsan" , 10));
		ts.add(new Student("zhangsan" , 12));
		ts.add(new Student("wanger" , 10));
		Iterator<Object> it = ts.iterator();
		while(it.hasNext()){
			Student st = (Student)it.next();
			System.out.println(st);
		}
	}
}

class Student implements Comparable{
	private String name;
	private int age;
	public Student(String name , int age){
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
	@Override
	public int compareTo(Object o) {
		if(!(o instanceof Student))
			throw new RuntimeException("Not a Studnet object");
		Student s = (Student)o;
		if(this.age > s.age) 
			return 1;
		if(this.age == s.age){
			return this.name.compareTo(s.name);
		}
		return -1;
	}
}