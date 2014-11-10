package itcast.jdk5demo1;

import static java.lang.System.out;
import static java.lang.Math.*;

public class Demo1StaticImport {

	/**静态导入
	 * @param args
	 */
	public static void main(String[] args) {
		out.print("haha");//将out转化为本地函数,使用的时候不用再加类名
		abs(12);
	}

}
