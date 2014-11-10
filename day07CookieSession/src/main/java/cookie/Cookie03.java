package cookie;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 显示商品详细信息 功能：根据用户带过来的id值，显示相应商品的信息 根据用户原来看过的书，以及现在看的书的id，构建新的cookie值
 * @author buildhappy
 * 
 */
public class Cookie03 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		// 1.根据用户带过来的id值，显示相应商品的信息
		out.print("您想看的书的详细信息为：<br/>");
		String id = request.getParameter("id");
		Book book = (Book) DB.getMap().get(id);
		out.print(book.getId() + "<br/>");
		out.print(book.getName() + "<br/>");
		out.print(book.getAuthor() + "<br/>");

		// 2.记录用户查看的书，即以cookie的形式回写该商品的id号给浏览器(cookie的列表长度不能超过3个)
		// 取出原来的cookie并在其基础上用当前的Id构建新的cookie
		String bookHistory = makeCookie(book.getId(), request);
		Cookie cookie = new Cookie("bookHistory", bookHistory);
		cookie.setMaxAge(10000);
		// cookie.setPath("/day07CookieSession");
		response.addCookie(cookie);// 回写给浏览器
	}

	// 根据用户原来看过的书，以及现在看的书的id，构建新的cookie值
	private String makeCookie(String id, HttpServletRequest request) {
		// 演示所有可能的情况
		// 原来的记录信息 本次浏览的书 最终cookie形式
		// bookHistory=null 3 bookHistory=3
		// bookHistory=2_1_5 3 bookHistory=3_2_1
		// bookHistory=2 3 bookHistory=3_2
		// bookHistory=2_3 3 bookHistory=3_2

		// 1.得到用户曾经看过的书
		String bookHistory = null;
		Cookie cookies[] = request.getCookies();
		for (int i = 0; cookies != null && i < cookies.length; i++) {
			if (cookies[i].getName().equals("bookHistory")) {
				bookHistory = cookies[i].getValue(); // 获取cookie的内容
			}
		}
		// System.out.println(bookHistory);

		if (bookHistory == null) {
			bookHistory = id;
			return bookHistory;
		}

		// bookHistory=1_2_5 代表用户曾经看一些书，接着程序要得到用户曾经看过什么书
		String ids[] = bookHistory.split("_");
		// 为了检测数组中是否包含当前id，我们应该把数据转成集合，并且还要转成链表结构的集合
		LinkedList<String> idList = new LinkedList(Arrays.asList(ids));// 将数组转化成集合

		/*
		if (idList.contains(id)) { // bookHistory=2_3 3 bookHistory=3_2
			idList.remove(id);
			idList.addFirst(id);
		} else { // bookHistory=2_1_5  3  bookHistory=3_2_1
			if (idList.size() >= 3) {
				idList.removeLast();
				idList.addFirst(id);
			} else { // bookHistory=2   3  bookHistory=3_2
				idList.addFirst(id);
			}
		}
		*/

		if (idList.contains(id)) {
			idList.remove(id);
		} else {
			if (idList.size() >= 3) {
				idList.removeLast();
			}
		}
		idList.addFirst(id);

		StringBuffer sb = new StringBuffer();//字符串组装用StringBuffer
		for (String lid : idList) { //组装效果 1_2_3_
			sb.append(lid + "_");
		}

		return sb.deleteCharAt(sb.length() - 1).toString();//最终放在cookie中的值为1_2_3
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
