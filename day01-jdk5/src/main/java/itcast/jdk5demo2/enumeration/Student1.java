package itcast.jdk5demo2.enumeration;

public class Student1 {
	
	private String name;
	private String grade;   //  ABCDE
	
	//alt+shift+s
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		if(!grade.matches("[ABCDE]")){//正则表达式,是否匹配ABCDE
			throw new RuntimeException("grade只能是ABCDE");
		}
		this.grade = grade;
	}
}
