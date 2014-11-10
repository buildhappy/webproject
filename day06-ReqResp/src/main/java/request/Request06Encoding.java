package request;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 解决get和post提交的乱码没，结合form2.html
 * @author buildhappy
 *
 */
public class Request06Encoding extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	
		
		if(request.getMethod().equals("POST")){
			//request.setCharacterEncoding("UTF-8");  //只对post提交有效，对get无效
			request.setCharacterEncoding("UTF-8"); 
			String username = request.getParameter("username");
			System.out.println(username);
		}else{
			//如为get提交，username中的乱码想正常，只能采用如下方式手工处理
			String username = request.getParameter("username");
			username = new String(username.getBytes("iso8859-1"),"UTF-8");
			System.out.println(username);
		}
		
		/*通过修改服务器的配置，在conf/server.xml中的Connector  useBodyEncodingForURI=true (有问题)
		//request.setCharacterEncoding("UTF-8");  //post  
		//System.out.println(request.getParameter("username"));
		 */
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
