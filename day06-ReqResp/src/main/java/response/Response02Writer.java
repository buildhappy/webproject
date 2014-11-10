package response;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用Writer输出中文数据
 * @author buildhappy
 *
 */
public class Response02Writer extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		test1(response);
	}
	
	public void test2(HttpServletResponse response) throws IOException{
		//这句话调用后，相当于把response.setCharacterEncoding("UTF-8");  也设好了(最好是将两条语句都写出来)
		response.setContentType("text/html;charset=UTF-8");

		String data = "中国";
		PrintWriter writer = response.getWriter();
		writer.write(data);
	}
	
	public void test1(HttpServletResponse response) throws IOException{
		
		//更改response的码表,通知服务器用UTF-8码表去取response中的数据，然后写给客户机
		response.setCharacterEncoding("UTF-8");//在servlet-api2.4以上才可以
		
		//通知浏览器以UTF-8码表打开回送的数据
		//response.setHeader("content-type", "text/html;charset=UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		String data = "中国";
		PrintWriter writer = response.getWriter();
		writer.write(data);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
