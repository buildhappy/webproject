package itcast;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 通过servletContext获取文件的mime类型
 * @author buildhappy
 *
 */
public class Servlet08 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String filename = "1.jpg";
		
		ServletContext context = this.getServletContext();
		System.out.println(context.getMimeType(filename));//获取mime信息
		
		response.setHeader("content-type", "image/jpeg");//image/jpeg为文件的mime
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
//什么是MIME类型-在把输出结果传送到浏览器上的时候，浏览器必须启动是他的应用程序来处理这个输出文档。
//这可以通过多种类型MIME（多功能网际邮件扩充协议）来完成。
//在HTTP中，MIME类型被定义在Content-Type header中。
