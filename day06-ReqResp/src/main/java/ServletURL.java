import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/*
 只要是写地址，建议大家最好以"/"开头，/到底代表什么呢？
 	1.看这个地址是给服务器用的，还是给浏览器用的。
 	2.如果给浏览器用的，/代表网站  （webapps）
 	3.如果给服务器用的，/代表当前web应用
  
 */
public class ServletURL extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//1.
		this.getServletContext().getRealPath("/download/日本纽.jpg");
		
		//2.
		this.getServletContext().getRequestDispatcher("/form.html");
		
		//3.
		response.sendRedirect("/day06/index.jsp");  //location
		
		//4.
		response.getWriter().write("<meta http-equiv='refresh' content='3;url=/day06-ReqResp/index.jsp'>");
		
		//5.
		/*
		<form action="/day06/servlet/RequestDemo8">
		</form>
		*/
		
		//6. <a href="/day06/....">点点</a>  <img src="/day06/....">   
		
		// c:\\        /
		
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}