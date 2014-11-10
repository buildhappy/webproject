package itcast.jdk5demo1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Demo2 {

	/** 装箱折箱，基本的数据类型与对象的相互赋值
	 * @param args
	 */
	public static void main(String[] args) {

		int i = 3;
		Integer num = i;   //jdk1.4以前不可以   JDK5可以装箱
		int j = num;   //折箱
		
		
		//应用场景
		List list = new ArrayList();
		list.add(1);  //装箱  此处通过装箱添加对象，自动将1转化为Integer对象(jdk1.4,List中只能添加对象)
		list.add(2);
		
		Iterator it = list.iterator();
		while(it.hasNext()){
			int m = (Integer)it.next();  //折箱
			System.out.println(m);
		}
	}

}
