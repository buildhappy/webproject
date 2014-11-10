package itcast;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 利用ServletContest中的函数，获取web.xml文件中配置的web应用的显示名称
 * @author buildhappy
 *
 */
public class Servlet13 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String name = this.getServletContext().getServletContextName();
		System.out.println(name);
		response.getWriter().write(" pageEncoding='UTF-8' <a href='/"+name+"/1.html'>点点</a>");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
