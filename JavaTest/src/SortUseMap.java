import java.util.TreeMap;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
public class SortUseMap {

	public static void main(String[] args) {
		String s = "aabcbcartyaffdad";
		System.out.println(CountChar(s));
	}
	public static String CountChar(String s){
		char[] charArray = s.toCharArray();
		TreeMap<Character , Integer> tm = new TreeMap<Character , Integer>();
		for(int i = 0; i < charArray.length; i++){
			Integer value = tm.get(charArray[i]);
			if(value == null){
				value = 1;
			}else{
				value++;
			}
			tm.put(charArray[i] , value);
//			if(tm.containsKey(charArray[i])){
//				int value = tm.get(charArray[i]);
//				tm.put(charArray[i] , ++value);
//			}else{
//				tm.put(charArray[i], 1);
//			}
		}
		StringBuilder result = new StringBuilder();
		//System.out.println(tm);
		Collection<Map.Entry<Character , Integer>> coll = tm.entrySet();
		Iterator<Map.Entry<Character , Integer>> it = coll.iterator();
		while(it.hasNext()){
			Map.Entry<Character , Integer> entry = it.next();
			//result = result + entry.getKey() + "(" + entry.getValue() + ")";
			result.append(entry.getKey() + "(" + entry.getValue() + ")");
		}
		return result.toString();
	}
}
