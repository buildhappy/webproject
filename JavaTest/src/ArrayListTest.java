/*
 * 去除ArrayList中的重复元素
 */

import java.util.ArrayList;
import java.util.Iterator;
public class ArrayListTest {

	public static void main(String[] args) {
		ArrayList al = new ArrayList();
		al.add("java01");
		al.add("java02");
		al.add("java01");
		System.out.println("Original List:" + al);
		System.out.println("Pured List:" + pureArrayList(al));
		
	}
	
	
	public static ArrayList pureArrayList(ArrayList al){
		ArrayList newList = new ArrayList();
		for(Iterator it = al.iterator(); it.hasNext(); ){
			Object obj = it.next();
			if(!newList.contains(obj)){
				newList.add(obj);
			}
		}
		return newList;
	}

}
