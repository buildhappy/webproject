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
//解决全站乱码
public class CharacterEncodingFilter implements Filter {

	private FilterConfig config;
	private String defaultCharset = "UTF-8";
	
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		//获取要设置的字符集
		String charset = this.config.getInitParameter("charset");
		if(charset==null){
			charset = defaultCharset;
		}
		
		//强转成HttpServletRe...
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		request.setCharacterEncoding(charset);  //post  get
		response.setCharacterEncoding(charset);
		response.setContentType("text/html;charset=" + charset);
		
		chain.doFilter(request, response);//放行请求
	}

	
	public void destroy() {
		// TODO Auto-generated method stub

	}

	
	public void init(FilterConfig filterConfig) throws ServletException {
		this.config = filterConfig;
	}

}
