import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
public class MapDemo {

	public static void main(String[] args) {
		Map<String , String> map = new TreeMap<String , String>();
		map.put("01" , "sansan");
		System.out.println(map.put("02" , "sisi"));
		map.put("03", "build");
		System.out.println(map);
		
		//使用Map.Entry()迭代Map
		Collection<Map.Entry<String , String>> coll = map.entrySet();
		
		Iterator<Map.Entry<String , String>> it = coll.iterator();
		
		while(it.hasNext()){
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			System.out.println(key + ":" + value);
		}
	}

}
