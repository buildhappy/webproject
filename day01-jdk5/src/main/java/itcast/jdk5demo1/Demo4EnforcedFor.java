package itcast.jdk5demo1;

import java.util.ArrayList;
import java.util.List;

public class Demo4EnforcedFor {

	/**增强for的注意事项(增强for只适合取数据，不能用于修改数据；如果想修改，用传统方式)
	 * @param args
	 */
	public static void main(String[] args) {

		int nums[] = {1,2,3};
		
		for(int num : nums){
			num = 10;
		}
		System.out.println(nums[0]);
		
		//修改数据
		for(int i=0;i<nums.length;i++){
			nums[i] = 10;
		}
		System.out.println(nums[0]);
		
		
		
		List list = new ArrayList();
		list.add(1);
		list.add(2);
		list.add(3);
		
		for(Object obj : list){
			obj = 10;
		}
	}
}
