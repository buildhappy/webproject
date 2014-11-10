package shoppingproject.shopping;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 首页，显示网站所有商品
 * @author buildhappy
 *
 */
public class IndexServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		request.getSession();
		
		//1.显示网站所有商品
		out.print("本网站有如下书籍：<br/>");
		Map<String , Book> map = DB.getMap();
		for(Map.Entry<String , Book> entry : map.entrySet()){
			Book book = entry.getValue();
			String url = "/day07CookieSession/BuyServlet?id=" + book.getId();
			url = response.encodeURL(url);  //得到重写后的url，参照BuyServlet.java
			out.print(book.getName() + "<a href='"+url+"'>购买</a><br/>");
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}

class DB{
	private static Map<String,Book> map = new HashMap();
	static{
		map.put("1", new Book("1","javaweb开发","老张"));
		map.put("2", new Book("2","jdbc开发","老黎"));
		map.put("3", new Book("3","struts2开发","老张"));
		map.put("4", new Book("4","spring开发","老黎"));
		map.put("5", new Book("5","hibernate开发","老张"));
	}
	
	public static Map getMap(){
		return map;
	}
	
}

class Book implements Serializable{//实现序列化接口
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
