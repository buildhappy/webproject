package com.greatdreams.simplespringweb.common.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class UserInfoFilter implements Filter {

	private FilterConfig fc;
	private ServletContext ctx;
	
	public void init(FilterConfig filterConfig) throws ServletException {
		this.fc =filterConfig;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest rq = (HttpServletRequest)request;
		String UserAgent = rq.getHeader("User-Agent");
		System.out.println("Path Information:" + rq.getRequestURI());
		System.out.println("User-Agent : " + UserAgent);
		chain.doFilter(request, response);
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

}
