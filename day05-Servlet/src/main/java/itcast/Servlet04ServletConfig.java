package itcast;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 通过servletConfig获取servlet配置的初始化参数
 * @author buildhappy
 * 在web.xml中进行配置
 */
public class Servlet04ServletConfig extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//获取配置对象(从GenericServlet对象继承得到)
		ServletConfig config = this.getServletConfig();
		
		// 获取配置的初始化参数  方式1
		//String value = config.getInitParameter("charset");

		// 获取配置的初始化参数  方式2
		Enumeration e = config.getInitParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			String value = config.getInitParameter(name);
			System.out.println(name + "=" + value);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
