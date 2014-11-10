package ch07ReusableClass;
/**
 * 继承与初始化
 * @author buildhappy
 *
 */
public class P146Static {
	public static void main(String[] args){
		Beetle beetle = new Beetle();
	}
}
//运行时，先初始化父类的static再子类static，再是父类的构造函数，再子类的static
class Insect{
	private int i = 9;
	protected int j;
	Insect(){
		System.out.println("i,j=" + i + j);
		j = 39;
	}
	static int x1 = initPrint("Insect.x1 initialized");
	static int initPrint(String s){
		System.out.println(s);
		return 1;
	}
}

class Beetle extends Insect{
	private int k = initPrint("Beetle.k initialized");
	public Beetle(){
		System.out.println("k=" + k);
		System.out.println("j=" + j);
	}
	
	private static int x2 = initPrint("Beetle.x2 initialized");
}
