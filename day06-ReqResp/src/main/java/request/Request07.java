package request;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 考题，结合form2.html中文编码
 * @author buildhappy
 *
 */
public class Request07 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String username = request.getParameter("name");
		username = new String(username.getBytes("iso8859-1"),"UTF-8");//字节编吗转换
		System.out.println(username);
		
		/*不会出现乱码
		request.setCharacterEncoding("UTF-8");
		String username = request.getParameter("username");
		
		response.setCharacterEncoding("gb2312");
		response.setContentType("text/html;charset=gb2312");
		response.getWriter().write(username);*/
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
