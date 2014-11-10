package shoppingproject.shopping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * 购买 
 * @author buildhappy
 *
 */
public class BuyServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//1.得到用户想买的书
		String id = request.getParameter("id");
		Book book = (Book) DB.getMap().get(id);
		
		//2.把用户买的书加在session中用于管理所有书的集合中了
		HttpSession session = request.getSession();
		Cookie cookie = new Cookie("JSESSIONID",session.getId());
		cookie.setMaxAge(30*60);
		cookie.setPath("/day07CookieSession"); //在访问day07这个应用下的所有资源时，都会产生
		response.addCookie(cookie);//将cookie发给浏览器
		
		List list = (List) session.getAttribute("list");
		if(list==null){
			list = new ArrayList();
			session.setAttribute("list", list);
		}
		list.add(book);
		
		//3.跳转到购物车显示列表
			//利用forward会出现问题，用户没刷新一次，购买的商品将会增加一个
		//request.getRequestDispatcher("/ListCartServlet").forward(request, response);
			//当用户进制cookie时，利用URL重写实现共享session(所有的URL都得重写)，encodeRedirectURL函数将会返回一个带有session的id号的URL地址
		String url  = response.encodeRedirectURL("/day07CookieSession/ListCartServlet");    //实现 url重写
		response.sendRedirect(url);
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
