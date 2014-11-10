package cookie;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 首页（显示用户上次访问的时间）
 * @author buildhappy
 *
 */
public class Cookie01 extends HttpServlet {

	@SuppressWarnings("deprecation")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		PrintWriter out = response.getWriter();
		out.write("您上次访问时间是：");
		
		//1.获取用户上次访问的时间，并显示
		Cookie cookies[] = request.getCookies();
		for(int i=0; cookies != null && i < cookies.length; i++){
			Cookie cookie = cookies[i];
			if(cookie.getName().equals("lastAccessTime")){
				long time = Long.parseLong(cookie.getValue());//将 string参数解析为有符号的long
				Date date = new Date(time);//将毫秒值转化成日期
				out.write(date.toLocaleString());
			}
		}
		
		//2.把本次的时间以cookie的形式回写给客户机   (lastAccessTime)
		Cookie cookie = new Cookie("lastAccessTime",System.currentTimeMillis()+"");
		cookie.setMaxAge(9);//存活时间  秒
		response.addCookie(cookie);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
