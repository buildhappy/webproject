package com.buildhappy.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.buildhappy.service.NewsService;

/**
 * Servlet implementation class DeleteNews
 */
public class DeleteNews extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public DeleteNews() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String newsId = request.getParameter("newsId");
		System.out.println("deleteNews");
		int id = Integer.parseInt(newsId);
		ApplicationContext cxt = new FileSystemXmlApplicationContext("classpath:web-app.xml");
		NewsService newsService = cxt.getBean("newsService" , NewsService.class);
		newsService.deleteById(id);
		ServletOutputStream writer = response.getOutputStream();
		writer.print("ok");
		
		//response.sendRedirect("");
	}

}
