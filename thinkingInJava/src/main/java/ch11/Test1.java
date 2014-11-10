package ch11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;


public class Test1 {
	/**
	 * Java编程思想，第11章持有对象
	 */
	
	//P220
	@Test
	public void AddingGroups(){
		//使用Arrays.asList向Collection中添加数据
		Collection<Integer> co = new ArrayList<Integer>(Arrays.asList(1 , 2 , 3));
		Iterator<Integer> it = co.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
		
		//使用addAll()向Collection中添加数据
		Collection<Integer> co2 = new ArrayList<Integer>();
		co2.addAll(co);
		it = co2.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
		
	}
}
