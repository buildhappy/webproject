package itcast.jdk5demo1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Demo3EnforcedFor {

	/**增强for,只能作用在数组或者实现Iterator接口的集合类上
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		mapExercise();
//		List list = new ArrayList();
//		list.add(1);
//		list.add(2);
//		list.add(3);
//		
//		for(Object obj : list){
//			int num = (Integer)obj;
//			System.out.println(num);
//		}
//		
//		int nums[] = {1,2,3};
//		for(int num : nums){
//			//System.out.println(num);
//		}
//		
//		System.out.println("-------------Map类--------------");
//		
//		Map map = new LinkedHashMap();//LinkedHashMap是Map的子类，内容有序
//		map.put("1", "aa");  //map.entry
//		map.put("2", "bb");
//		map.put("3", "cc");
//		
//		
//		//取数据的方式1   keySet()取出所有的key
//		Set set = map.keySet();
//		Iterator it = set.iterator();
//		System.out.println("方法一");
//		while(it.hasNext()){
//			String key = (String) it.next();
//			String value = (String) map.get(key);
//			System.out.println(key + "=" + value);
//		}
//		
//		//取数据的方式2(非常重要)
//		Set set1 = map.entrySet();  //Map.Entry；将map中的每一个元素放到一个set中去
//		Iterator it1 = set1.iterator();
//		while(it1.hasNext()){
//			Map.Entry entry = (Entry) it1.next();
//			String key = (String) entry.getKey();
//			String value = (String) entry.getValue();
//			System.out.println(key + "=" + value);
//		}
//		
//		//取数据的方式3
//		for(Object obj  : map.entrySet()){
//			Map.Entry entry = (Entry) obj;
//			String key = (String) entry.getKey();
//			String value = (String) entry.getValue();
//			System.out.println(key + "=" + value);
//		}
	}
	
	public static void mapExercise(){
		Map<String , String> map = new LinkedHashMap<String , String>();
		map.put("1", "guozhen");
		map.put("2", "jianfu");
		map.put("3", "amao");
		map.put("4", "xiaoming");
		
//		Set keySet = map.keySet();
//		Iterator it = keySet.iterator();
//		while(it.hasNext()){
//			String key = (String)it.next();
//			System.out.println(key + ":" + map.get(key));			
//		}
		
//		Set entrySet = map.entrySet();
//		Iterator it = entrySet.iterator();
//		while(it.hasNext()){
//			Map.Entry<String, String> entry = (Map.Entry<String, String>)it.next();
//			System.out.println(entry.getKey() + ":" + entry.getValue());
//		}
		
//		Set<Map.Entry<String , String>> entrySet = map.entrySet();
//		for(Object obj : entrySet){
//			Map.Entry<String, String> entry = (Map.Entry<String , String>)obj;
//			System.out.println(entry.getKey() + ":" + entry.getValue());
//		}
		
	}
}
