package itcast.jdk5demo2.enumeration;

public class Student2 {
	private String name;
	private Grade grade;  //AKIDI
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Grade getGrade() {
		return grade;
	}
	public void setGrade(Grade grade) {
		this.grade = grade;
	}
}

class Grade{
	private Grade(){}//构造函数私有后，无法在类外新建对象
	public static Grade A = new Grade();
	public static Grade B = new Grade();
	public static Grade C = new Grade();
	public static Grade D = new Grade();
	public static Grade E = new Grade();
}
