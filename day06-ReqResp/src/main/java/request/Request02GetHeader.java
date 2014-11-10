package request;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 获取请求头相关的方法
 * @author buildhappy
 *
 */
public class Request02GetHeader extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println(request.getHeader("UA-CPU"));//应该返回x86
		
		System.out.println("-----");
		/*
		Enumeration e = request.getHeaderNames();
		while(e.hasMoreElements()){
			String name = (String) e.nextElement();
			String value = request.getHeader(name);
			System.out.println(name + "=" + value);
		}
		*/
		for(Enumeration e = request.getHeaderNames(); e.hasMoreElements(); ){
			String name = (String) e.nextElement();
			String value = request.getHeader(name);
			System.out.println(name + "=" + value);
		}
		
		
		System.out.println("-----");
		Enumeration e = request.getHeaders("ua-cpu");
		while(e.hasMoreElements()){
			System.out.println(e.nextElement());
		}
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
