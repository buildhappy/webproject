//package cn.itcast;
import java.io.*;
import javax.servlet.*;
public class FirstServlet extends GenericServlet{

	//重写init方法
	public void init(ServletConfig config) throws ServletException{
		System.out.println("servlet对象创建");
	}
	public void service(ServletRequest req , ServletResponse res)
										throws ServletException , java.io.IOException{
		OutputStream out = res.getOutputStream();//获取servlet的输出流,向浏览器输出二进制数据
		out.write("hello world".getBytes());
	}
}