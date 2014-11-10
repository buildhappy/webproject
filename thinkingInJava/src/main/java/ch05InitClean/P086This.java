package ch05InitClean;

/**
 * this的使用
 * 
 * @author buildhappy
 */
public class P086This {
	public static void main(String[] args){
		Flower flower = new Flower();
	}
}

class Flower {
	int petalCount = 0;
	String s = "initial value";

	Flower(int petals) {
		petalCount = petals;
		System.out
				.println("constructor int arg only. petalCount=" + petalCount);
	}

	Flower(String ss) {
		s = ss;
		System.out.println("constructor String arg only. s=" + s);
	}

	Flower(int i, String ss) {
		this(i);// 调用构造函数1
		// ! this(ss);//this构造函数只能使用一次
		this.s = ss;
		System.out.println("String && int args");
	}

	Flower() {// 默认的构造函数(不带参数)
		this(47, "hi");
		System.out.println("Default contructor no args");
	}
}