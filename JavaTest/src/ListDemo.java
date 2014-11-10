import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
public class ListDemo {
	public static void main(String[] args){
		ArrayList al = new ArrayList();
		al.add("java01");
		al.add("java02");
		al.add("java03");
		al.add("java04");
		System.out.println("size:" + al.size() + "  print:" + al);


//		for(Iterator it = al.iterator(); it.hasNext(); ){
//			Object obj = it.next();
//			if(obj.equals("java01")){
//				//it.remove();//ok
//				al.add("java00");//wrong ConcurrentModificationException
//			}
//			System.out.println(obj);
//		}
		
		for(ListIterator it = al.listIterator(); it.hasNext(); ){
			//System.out.println(al.get(1));
			Object obj = it.next();
			if(obj.equals("java02")){
				//al.remove(1);
				it.add("java10");
			}
			System.out.println(obj);
		}
		
		
	}
}
