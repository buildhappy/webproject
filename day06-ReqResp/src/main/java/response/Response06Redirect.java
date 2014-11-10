package response;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 实现请求重定向
细节：
1.请求重定向时，浏览器向服务器发送了2次请求，request和response对象分别有两个
2.请求重定向时，浏览器的地址栏会发生变化
3.请求重定向要少用，它只适合用在一些特定的场景下，例如登陆（因为用户登陆成功后，我们系统希望用户知道他到首页上去了）
*/
public class Response06Redirect extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*response.setStatus(302);
		response.setHeader("location", "index.jsp");*/
		response.sendRedirect("index.jsp");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
