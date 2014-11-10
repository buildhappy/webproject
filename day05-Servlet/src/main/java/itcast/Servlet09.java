package itcast;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 通过servletContext 实现请求转发(getNamedDispatcher函数)
 * servlet9收到请求后，转发给servlet10/view.jsp 处理  (mvc设计模式)
 * @author buildhappy
 *
 */
public class Servlet09 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//servlet收到请求产生数据，然后转交给jsp显示
		String data = "aaaaaa";
		this.getServletContext().setAttribute("data", data);//将数据传递给view.jsp文件
		RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/view.jsp");
		rd.forward(request, response);
		//response.getWriter().write("<font size='100px' color='red'>" + data + "</font>");
		
		///day05-Servlet/view.jsp
		//得到转发到servlet10的转发对象
		//RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/Servlet10");
		//rd.forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
