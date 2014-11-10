package ch05InitClean;

import java.util.Arrays;

/**
 * 数组的初始化
 * @author buildhappy
 *
 */
public class P101ArrayInit {
	public static void main(String[] args){
		//数组的初始化方法三：这样可以在函数调用处进行数组的初始化
		Other.main(new String[]{"federal" , "debain"});
		ArrayInit.main();
	}
}

class Other{
	public static void main(String[] args){
		for(String arg:args){
			System.out.println(arg);
		}
	}
}

class ArrayInit {
	public static void main(){
		System.out.println("In ArrayInit");
		//数组的初始化方法一：
		int[] a = new int[3];
		for(int i = 0; i < a.length; i++){
			a[i] = i + 1;
		}
		
		System.out.println(Arrays.toString(a));
		//数组的初始化方法二：
		Integer[] b = {
			new Integer(1),
			new Integer(2),
			3,//JDK5后的自动装箱
		};
		//数组的初始化方法三：
		Integer[] c = new Integer[]{
			new Integer(1),
			new Integer(2),
			3//自动装箱
		};
		
		//方法一和二，
	}
}
