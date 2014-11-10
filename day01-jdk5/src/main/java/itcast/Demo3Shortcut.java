package itcast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Demo3Shortcut {

	private static final int NUM = 0;
	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) {

		// alt+/ 内容助理content
		// FileInputStream in = new FileInputStream("");]
		
		// 快速修复：Ctrl + 1
		// FileInputStream in = new FileInputStream("");
		List list = new ArrayList();

		// 导包：Ctrl + shift + O
		Set set = new HashSet();

		// 格式化代码块：ctrl + shift + F
		try {
			FileInputStream in = new FileInputStream("");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ctrl+ 查看源代码
		//向前向后：Alt + 方向键   
		Set set1 = new HashSet();
		
		//添加注释 Ctrl+Shift+/  \
		Set set2 = new HashSet();

		//查看方法说明：F2
		
		//重置透视图(eclipse布局)windows->reset perspective
		
		//更改为大写 Ctrl+Shift+X  Y变小写
		
		//复制行 Ctrl+Alt+向下键(有些不能用)
		System.out.println("hahah");
		System.out.println("hahah");

		
		//查看类的继承关系 ctrl+T
		Set set3 = new HashSet();
		
		//ctrl+shit+t
		//查看源代码ctrl
		Set set4 = new HashSet();
		
		//ctrl+shit+l所有的快捷键

	}

}
