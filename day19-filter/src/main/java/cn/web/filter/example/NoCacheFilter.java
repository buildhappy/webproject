package cn.web.filter.example;
/**
 * 当就jsp的页面一直变化时，可以设置响应头,使浏览器不缓存jsp文件
 */
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoCacheFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		response.setDateHeader("Expires",-1);
		response.setHeader("Cache-Control","no-cache");
		response.setHeader("Pragma","no-cache");
		
		chain.doFilter(request, response);

	}
	
	public void destroy() {
		// TODO Auto-generated method stub
	}

	

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		//服务器一启动时就加载Filter
	}

}
