package cn.web.filter.example;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//控制浏览器缓存的过滤器
public class CacheFilter implements Filter {

	private FilterConfig config;
	
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		//1.获取到用户想访问的资源
		String uri = request.getRequestURI();
		
		//2.获取该资源的缓存时间
		int expires = 0;
		if(uri.endsWith(".jpg")){
			expires = Integer.parseInt(this.config.getInitParameter("jpg"));
		}else if(uri.endsWith(".css")){
			expires = Integer.parseInt(this.config.getInitParameter("css"));
		}else{
			expires = Integer.parseInt(this.config.getInitParameter("js"));
		}
		
		response.setDateHeader("expires", System.currentTimeMillis()+expires*60*1000);
		chain.doFilter(request, response);
	}
	
	


	public void init(FilterConfig filterConfig) throws ServletException {
		this.config = filterConfig;
	}

	
	public void destroy() {
			
	
		}
	}
