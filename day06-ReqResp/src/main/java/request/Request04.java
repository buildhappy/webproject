package request;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 防盗链
 * @author buildhappy
 *
 */
public class Request04 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//
		String referer = request.getHeader("referer");//referer告诉服务器我是从哪个页面链接过来的
		System.out.println(referer);
		if(referer==null || !referer.startsWith("http://localhost")){
			response.sendRedirect("index.jsp");//重定向，不共用response
								//request.getRequestDispatcher()是请求转发，前后页面共享一个request
			return;
		}
		String data = "slkjdlsjfljadljslfjljlsjaqaa";
		System.out.println(data);
		//response.getWriter().write(data);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
