package cn.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class FilterDemo1 implements Filter {

	/*
	 * filter在开发中的常见应用：
	 * 1.filter可以目标资源执行之前，进行权限检查，检查用户有无权限，如有权限则放行，如没有，则拒绝访问
	 * 2.filter可以放行之前，对request和response进行预处理，从而实现一些全局性的设置。
	 * 3.filter在放行之后，可以捕获到目标资源的输出，从而对输出作出类似于压缩这样的设置
	 */
	

	private FilterConfig config;
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		String value = this.config.getInitParameter("xxx");
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		System.out.println("servletDemo1之前");
		
		
		chain.doFilter(request, response);  //放行
		
		
		System.out.println("servletDemo1之后!!");
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("filter被创建了！！");
		this.config = filterConfig;
	}
	
	public void destroy() {
		System.out.println("filter被销毁了！！");

	}

}
