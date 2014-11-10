package itcast;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 设置浏览器的缓存
 * @author buildhappy
 *
 */
public class Servlet14 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		long expriestime = System.currentTimeMillis() + 1*24*60*60*1000;
		response.setDateHeader("expires", expriestime);
		String data = "adsdfsdfsdfsdfdsf";
		response.getWriter().write(data);
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
