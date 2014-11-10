package itcast.jdk5demo1;

import java.util.Arrays;
import java.util.List;

public class Demo5 {

	/**可变参数,定义格式为f(int ... args)
	 * @param args
	 */
	public static void main(String[] args) {
		//sum(1,2,3,4,5,6,7,8,6,7);
		//int arr[] = {2,8,9};
		//sum(arr);//可以直接传数组
		
		List list = Arrays.asList("aa","bb","cc");//将该3个对象放入list中。Arrays是array的帮助类，里面的方法是static
		System.out.println(list);
		
		String[] str = {"xx","yy","zz"};
		list = Arrays.asList(str);//可以直接传对象数组
		System.out.println(list);
		
		//注意以下代码int数组和Integer数组的不同
		int arr[] = {1,2,3};
		list = Arrays.asList(arr);//不能传非对象数组(基本数据类型)
		System.out.println(list);//打印时，显示乱码
	}
	
	//可变参数，在用时，当成数组即可
	public static void sum(int ... nums){
		int sum = 0;
		for(int num : nums){
			sum += num;
		}
		System.out.println(sum);
	}
	
	//xx(1,2,3,4,5,6)
	public static void xx(int k,int... nums){
	}
}
