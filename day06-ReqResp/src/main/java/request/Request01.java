package request;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * request对象的常用方法介绍(获取客户机信息)
 * @author buildhappy
 *
 */
public class Request01 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//得到客户机想请求的资源
		System.out.println(request.getRequestURL());
		System.out.println(request.getRequestURI());
		
		//getQueryString 方法返回请求行中的参数部分。  http://localhost:8080/day06/RequestDemo1?name=aaaa(get请求)
		System.out.println(request.getQueryString());
		
		//getRemoteAddr方法返回发出请求的客户机的IP地址
		System.out.println(request.getRemoteAddr());
		
		System.out.println("-----------");
		System.out.println(request.getRemoteHost());//192.168.1.141  www.itcast.cn
		
		System.out.println(request.getRemotePort());
		System.out.println("-----------");
		
		System.out.println(request.getMethod());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
