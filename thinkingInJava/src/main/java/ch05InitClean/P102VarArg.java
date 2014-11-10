package ch05InitClean;
import java.util.*;//自动加载
import static java.lang.System.out;//静态导入，可以直接使用out.print和out.println
/**
 * 可变参数列表
 * @author buildhappy
 *
 */
public class P102VarArg {
	public static void main(String[] args){
		//printArray函数可以接受任意类型的参数
		
		//基本类型对象
		printArray(new Integer(47) , new Float(3.14) , new Double(11.11));
		//基本数据类型
		printArray(47 , 3.14 , 11.11);
		//字符串
		printArray("one" , "two" , "three");
		//自定义类对象，默认打印类名和对象地址
		printArray(new A() , new A());
		//数组,最好转化成Object类型，否则会有警告
		printArray((Object[])new Integer[] {1 , 2 , 3 , 4});
		//参数也可以为空
		printArray();
		
	}
	
	//使用该参数形式的好处：参数个数可以改变，参数类型可以任意
	public static void printArray(Object... args){
		System.out.print(args.getClass());//打印该对象的类
		for(Object arg : args){
			out.print(arg + " ");
		}
		out.println();
	}
}

class A{}
