package com.buildhappy.controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.iharder.Base64;

import org.springframework.context.support.GenericXmlApplicationContext;

import com.buildhappy.domain.News;
import com.buildhappy.service.NewsService;

/**
 * 接受jsp文件发送的新闻数据，并存储到数据库中
 */
public class SaveNews extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public SaveNews() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String newsContent = request.getParameter("editorValue");//.getParameter("newsContent");
		System.out.println("get" + newsContent);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String author = null;
		String type = null;
		String newsTitle = null;
		String newsContent = null;
		Date date=new Date(System.currentTimeMillis());
		
		//get data from editNews.jsp
		author = request.getSession().getAttribute("author").toString();
		type = request.getParameter("type");
		newsTitle = request.getParameter("title");
		//newsContent = Base64.encodeBytes(request.getParameter("editorValue").getBytes());//获取内容
		newsContent = request.getParameter("editorValue");//获取内容
		//create News object
		News news = new News(author, 3 , type , newsTitle , newsContent , date);
		
		//load configuration file of web-app.xml 
		GenericXmlApplicationContext gxc = new GenericXmlApplicationContext();
		gxc.load("classpath:web-app.xml");
		gxc.refresh();
		
		NewsService newsService = gxc.getBean("newsService" , NewsService.class);
		newsService.insertNews(news);
		PrintWriter writer = response.getWriter();
		writer.print("ok ");//print information to user
	}
}