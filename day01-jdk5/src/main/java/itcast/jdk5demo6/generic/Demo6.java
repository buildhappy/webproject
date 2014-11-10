package itcast.jdk5demo6.generic;

import java.util.Arrays;

import org.junit.Test;

/**
 * 两个练习题
 * @author buildhappy
 *
 */
public class Demo6 {
	
	@Test
	public void testSwap(){
		String arr[] = {"aaa","bbb","ccc"};
		swap(arr,0,2);
		System.out.println(arr[0]);
	}
	
	/**
	 * 编写一个方法，实现数组元素的交换。将pos1和pos2位置上的数据交换
	 * @param arr
	 * @param pos1
	 * @param pos2
	 */
	public <T> void swap(T arr[],int pos1, int pos2){
		T temp = arr[pos1];
		arr[pos1] = arr[pos2];
		arr[pos2] = temp;
	}
	
	@Test
	public void testReverse(){
		String arr[] = {"aaa"};
		reverse(arr);
		System.out.println(Arrays.asList(arr));
	}
	
	/**
	 * 编写一个泛形方法，接收一个任意数组，并颠倒数组中的所有元素。
	 * @param arr
	 */
	public <T> void reverse(T arr[]){
		
		int start = 0;
		int end = arr.length-1;
		
		while(true){
			if(start >= end){
				break;
			}
			T temp = arr[start];
			arr[start] = arr[end];
			arr[end] = temp;
			start++;
			end--;
		}
	}
	
	/*
	                 4     10
	    3   7       3 5   9  11
	   2 4 6 8     2   6 8    12
	  1   5   9   1     7      13
	 */

}
