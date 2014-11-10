package itcast.jdk5demo4.introspector;

/**
 * Student类用来封装学生对象，将其视为一种javabean
 * @author buildhappy
 *
 */
public class Student {
	
	private String name;
	private String password;
	private int age;
	
	
	public boolean isGender() {  //getGender
		return false;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {  //属性的读方法
		return password;
	}
	public void setPassword(String password) {  //属性的写方法
		this.password = password;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	public String getYu(){//该类有Yu属性
		return null;
	}
	
}
