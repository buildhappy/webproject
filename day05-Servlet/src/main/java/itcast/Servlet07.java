package itcast;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 通过servletContext,获取为web应用配置的初始化参数(web.xml中的初始化配置)
 * @author buildhappy
 *
 */
public class Servlet07 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//String value = this.getServletContext().getInitParameter("xxx");
		String url = this.getServletContext().getInitParameter("url");
		String username = this.getServletContext().getInitParameter("username");
		String password = this.getServletContext().getInitParameter("password");
		
		System.out.println(url);
		System.out.println(username);
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
