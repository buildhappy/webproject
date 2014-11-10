package itcast;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 1.在servlet中如何获取servletContext
 * 2.如何servletContext带数据给其它web资源(与servlet6的交互)
 * @author buildhappy
 *
 */

public class Servlet05ServletContext extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//两种获取servletContext对象的方式
		ServletContext context = this.getServletConfig().getServletContext();
		ServletContext context1 = this.getServletContext();
		
		//Servlet5带数据给Servlet6(用context对象实现数据共享)
		context.setAttribute("data", "aaaaaaaaaa");
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
