package ch07ReusableClass;

/**
 * 派生类中方法重载
 * 返回值和修饰符不能实现方法重载
 * 重载与重写不同
 * @author buildhappy
 *
 */
public class P136 {
	public static void main(String[] args){
		Bart bart = new Bart();
		bart.doH('a');
	}
}

class Homer {
	//doH方法重载
	public char doH(char i){
		System.out.println("doH(char)");
		return 'd';
	}
	
	int doH(int i){
		System.out.println("doH(int)");
		return 1;
	}
	
	//加static不能实现重载
	/*public char doH(char i){
		System.out.println("doH(char)");
		return 'd';
	}*/
	
}

class Bart extends Homer{
	//实现父类方法的重载，可以加上@Override关键字
	@Override
	public char doH(char i){
		System.out.println("doH(char)");
		return 'd';
	}
	//此时是方法重写，而非覆盖，因此加上@Override会报错
	//@Override
	public void doH(){
		System.out.println();
		return;
	}
	
}
