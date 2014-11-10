package itcast;

import java.util.Arrays;


public class Demo2debug {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//alt+/
		int arr[] = {1,2,7,4,5};
		int arr2[] = {2 , 3 , 4};
		//int arr3[];
		int pos = Arrays.binarySearch(arr, 4);//二分法查询，返回四在数组中的位置(使用前必须排序，否则出错)
		//Arrays类包含用来操作数组（比如排序和搜索）的各种方法。
		//此类还包含一个允许将数组作为列表来查看的静态工厂
		System.out.println(pos);
		//System.out.println(Arrays.hashCode(arr));
		Arrays.sort(arr);
		for(int a : arr){
			System.out.println(a);
		}
		System.out.println(Arrays.equals(arr, arr2));
		String s = Arrays.toString(arr);//将数组转化成字符串
		System.out.println(s);
	}

}
