package hadoop.mapreduce;

public class JavaTest extends A{
	public int a = 100;
	public JavaTest(){
		super();
		System.out.println(a);
		a = 200;
	}
	static {
		System.out.println("in static");
	}
	public static void main(String[] args){
		new JavaTest();
	}
}

class A{
	public A(){
		System.out.println(((JavaTest) this).a);
	}
}
