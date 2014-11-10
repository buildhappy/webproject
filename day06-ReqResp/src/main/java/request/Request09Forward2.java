package request;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 转发过程要注意的问题
 * @author buildhappy
 *
 */


//如果在调用forward方法之前，在Servlet程序中写入的部分内容已经被真正地传送到了客户端，
//forward方法将抛出IllegalStateException异常。 

//如果在调用forward方法之前向Servlet引擎的缓冲区(response)中写入了内容，
//只要写入到缓冲区中的内容还没有被真正输出到客户端，forward方法就可以被正常执行，
//原来写入到输出缓冲区中的内容将被清空，但是，已写入到HttpServletResponse对象中的响应头字段信息保持有效。 

/*
 转发的细节
 1.转发只有一次请求，并且只有一个request和response,也就是说，转发涉及到的多个资源共享同一个request response
 2.转发浏览器的地址栏无变化 
 */

public class Request09Forward2 extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.write("ahhaa");
		//! out.close();//在此关闭流文件时，将会报错

		request.getRequestDispatcher("/form2.html").forward(request, response);

		out.write("wowowow");//也不会显示数据，即forward的前后都不要写数据
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
