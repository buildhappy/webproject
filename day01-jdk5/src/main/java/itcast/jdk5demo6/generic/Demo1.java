package itcast.jdk5demo6.generic;

import java.util.ArrayList;
import java.util.List;

public class Demo1 {

	/**
	 * 泛型：允许程序员在编写集合代码时，就限制集合的处理类型，从而把原来程序运行时可能发生的问题，
	 * 转变为编译时的问题，提高程序可读性和稳定性
	 * @param args
	 */
	public static void main(String[] args) {

		List list = new ArrayList();
		list.add("aaa");
		Integer i = (Integer) list.get(0);//此处在运行时出错，但编译能通过
		
		List<String> list1 = new ArrayList<String>();//指定集合的处理类型，即泛型
		list1.add("aaa");
		String str =  list1.get(0);
	}

}
