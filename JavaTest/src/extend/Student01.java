package extend;

public class Student01 extends Person01 {
	public Student01(int in){
		super(in);
		System.out.println("Student constrator");
	}
	public Student01(){
		System.out.println("Student no args constrator");
	}
}
