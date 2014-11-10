package response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * getOutputStream和getWriter方法冲突的问题
 * @author buildhappy
 *
 */
public class Response07 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//!PrintWriter print = response.getWriter();//writer不能与getOutStream同时存在
		OutputStream out = response.getOutputStream();
		this.getServletContext().getRequestDispatcher("Response08").forward(request, response);
			//request.getRequestDispatcher()是请求转发，前后页面共享一个request
			//response.sendRedirect()是重新定向，前后页面不是一个request。
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
