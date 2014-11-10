package itcast;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 线程安全问题
 */
public class Servlet03ThreadSafe extends HttpServlet {

	int num = 0;
	//子类在覆盖父类的方法时，不能抛出比父类更多的异常
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		//i不会出现线程安全
		//num会出现线程安全问题，可以用synchronized(mutex) 解决，但此方法会影响网站效率，不推荐使用。
		synchronized (this){
			//同步代码
		}
		int i = 0;
		i++;
		num++;
		try {
			Thread.sleep(1000 * 5);//睡眠5秒   
		}
		catch (InterruptedException e){
			e.printStackTrace();//后台记录
			response.getOutputStream().write("出异常了".getBytes());
		}
		
		response.getWriter().write(i + "");
		response.getWriter().write(num + "");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request , response);
	}

}
