package cn.web.filter.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import cn.web.filter.example.GzipFilter.MyServletOutputStream;

public class WebCacheFilter implements Filter {
	
	private Map<String,byte[]> map = new HashMap();
	
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		//1.得到用户想访问的资源（uri）
		String uri = request.getRequestURI();
		
		//2.看map集合中是否保存了该资源的数据
		byte b[] = map.get(uri);
		
		//3.如果保存了，则直接取数据打给浏览器
		if(b!=null){
			response.getOutputStream().write(b);
			return;
		}
		
		//4.如果没有保存数据，则放行让目标资源执行，这时还需写一个response的包装类，捕获目标资源的输出
		MyResponse my = new MyResponse(response);
		chain.doFilter(request, my);
		byte data[] = my.getBuffer();
		
		//5.以资源uri为关键字，打资源的数据保存map集合中，以备于下次访问
		map.put(uri, data);
		
		//6.输出数据给浏览器
		response.getOutputStream().write(data);
		
		

	}
	
	class MyResponse extends HttpServletResponseWrapper{
		private ByteArrayOutputStream bout = new ByteArrayOutputStream();
		private PrintWriter pw;
		
		private HttpServletResponse response;
		public MyResponse(HttpServletResponse response) {
			super(response);
			this.response = response;
		}
		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return new MyServletOutputStream(bout);    //myresponse.getOutputStream().write("hahah");
		}
		
		@Override
		public PrintWriter getWriter() throws IOException {
			pw = new PrintWriter(new OutputStreamWriter(bout,response.getCharacterEncoding()));
			return pw;  //MyResponse.getWriter().write("中国");
		}
		public byte[] getBuffer(){
			if(pw!=null){
				pw.close();
			}
			return bout.toByteArray();
		}
	}
	
	class MyServletOutputStream extends ServletOutputStream{

		private ByteArrayOutputStream bout;
		public MyServletOutputStream(ByteArrayOutputStream bout){
			this.bout = bout;
		}
		@Override
		public void write(int b) throws IOException {
			bout.write(b);
		}
		
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}
	
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
