package request;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 获取表单提交的数据(form.html)
 * @author buildhappy
 *
 */
public class Request05Form extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//用户名
		String username = request.getParameter("username");
		if(username!=null && !username.trim().equals("")){
			System.out.println("username="+  username);
		}
		
		//密码
		String password = request.getParameter("password");
		System.out.println("password="  + password);
		
		//性别
		String gender = request.getParameter("gender");  //radio
		if(gender!=null){
			System.out.println("gender=" + gender);
		}
		
		//城市
		String city = request.getParameter("city");
		System.out.println("city=" + city);
		
		//爱好
		String likes[] = request.getParameterValues("like");
		for(int i=0;likes!=null && i<likes.length;i++){
			System.out.println("like=" + likes[i]);
		}
		
		//简历
		String resume = request.getParameter("resume");
		System.out.println("resume=" + resume);
		
		//大头照(以后讲)
		
		//获取隐藏数据项
		String value = request.getParameter("aaa");
		System.out.println("aaa=" + value);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
