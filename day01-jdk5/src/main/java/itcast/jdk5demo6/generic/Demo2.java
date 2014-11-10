package itcast.jdk5demo6.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class Demo2 {
	
	/*******************list********************/
	@Test
	public void test1(){
		List<String> list = new ArrayList<String>();//泛型必须是对象类型，不能是基本数据类型
		list.add("aaa");
		list.add("bbb");
		list.add("ccc");
		
		//传统方式
		Iterator<String> it = list.iterator();
		while(it.hasNext()){
			String value = it.next();//返回迭代器中的数据
			System.out.println(value);
		}
		//1.5的方式
		for(String str : list){
			System.out.println(str);
		}
	}
	
	/*******************set********************/
	@Test
	public void test2(){
		Set<Integer> set = new HashSet<Integer>();
		set.add(1);//自动将1封装成对象
		set.add(2);
		
		for(Integer i : set){
			System.out.println(i);
		}
	}
	
	/*******************map********************/
	//传统方式1
	@Test
	public void test3(){
		Map<Integer,String> map = new HashMap<Integer,String>();
		map.put(1, "aaa");
		map.put(2, "bbb");
		map.put(3, "ccc");
		
		Set<Integer> set = map.keySet();//返回map所有的key
		Iterator<Integer> it = set.iterator();
		while(it.hasNext()){
			int key = it.next();
			String value = map.get(key);
			System.out.println(key + "=" + value);
		}
	}
	
	//传统方式2
	@Test
	public void test4(){
		Map<Integer,String> map = new HashMap<Integer,String>();
		map.put(1, "aaa");
		map.put(2, "bbb");
		map.put(3, "ccc");
		
		Set<Map.Entry<Integer, String>> set = map.entrySet();//set中放的是一个个的map.Entry对象
		Iterator<Map.Entry<Integer, String>> it = set.iterator();
		while(it.hasNext()){
			Map.Entry<Integer, String> entry = it.next();
			int key = entry.getKey();
			String value = entry.getValue();
			System.out.println(key + "=" + value);
		}
	}
	
	//JDK1.5的方式
	@Test
	public void test5(){
		Map<Integer,String> map = new HashMap<Integer,String>();
		map.put(1, "aaa");
		map.put(2, "bbb");
		map.put(3, "ccc");
		
		for(Map.Entry<Integer, String> entry : map.entrySet()){
			int key = entry.getKey();
			String value = entry.getValue();
			System.out.println(key + "=" + value);
		}
	}
	
}
