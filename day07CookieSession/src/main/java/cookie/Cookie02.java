package cookie;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 网站首页(与Cookie03.java配合使用)
 * 功能：显示网站所有商品+显示用户曾经浏览过的商品
 * @author buildhappy
 * 
 */

public class Cookie02 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		// 1.显示网站所有商品
		out.print("本网站有如下书籍：<br/>");
		Map<String, Book> map = DB.getMap();
		for (Map.Entry<String, Book> entry : map.entrySet()) {
			Book book = entry.getValue();
			out.print("<a href='/day07CookieSession/Cookie03?id=" + book.getId()
					+ "' target='_blank'>" + book.getName() + "</a><br/>");
					//target='_blank开辟新窗口
		}
		
		
		out.print("您曾经看过如下商品：<br/>");//print与write的区别：在写完数据后会自动换行
		
		// 2.显示用户曾经浏览过的商品    //bookHistory
		Cookie cookie = null;
		Cookie cookies[] = request.getCookies();
		for (int i = 0; cookies != null && i < cookies.length; i++) {
			if (cookies[i].getName().equals("bookHistory")) {
				cookie = cookies[i];
				System.out.println(cookie.getPath());
			}
		}
		
		if (cookie != null) {
			// 找到了bookHistory这个cookie
			String bookHistory = cookie.getValue(); // cookie记录的形式4_6_1
			String ids[] = bookHistory.split("\\_");
			for (String id : ids) {
				Book book = (Book) DB.getMap().get(id);
				out.print(book.getName() + "<br/>");
			}
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}

/**
 * 模拟数据库记录所有的书
 * @author buildhappy
 *
 */
class DB {
	//有检索数据的需求用Map，否则用List
	private static Map<String, Book> map = new HashMap();
	//静态块，在类加载的时候就运行
	static {
		map.put("1", new Book("1", "javaweb开发", "老张"));
		map.put("2", new Book("2", "jdbc开发", "老黎"));
		map.put("3", new Book("3", "struts2开发", "老张"));
		map.put("4", new Book("4", "spring开发", "老黎"));
		map.put("5", new Book("5", "hibernate开发", "老张"));
	}

	public static Map getMap() {
		return map;
	}
}

class Book {
	private String id;
	private String name;
	private String author;

	public Book() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Book(String id, String name, String author) {
		super();
		this.id = id;
		this.name = name;
		this.author = author;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}