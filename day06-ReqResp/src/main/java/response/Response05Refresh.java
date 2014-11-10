package response;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 控制定时刷新
 * @author buildhappy
 *
 */
public class Response05Refresh extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String data = "中国的人";
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		//response.setHeader("refresh", "3");
		//用meta标签模似上面的头
		response.getWriter().write("<meta http-equiv='refresh' content='3;url=index.jsp'>");
																				//每隔3秒刷新到首页
		response.getWriter().write(data);
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
