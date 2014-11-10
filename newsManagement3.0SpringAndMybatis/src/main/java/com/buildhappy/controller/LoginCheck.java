package com.buildhappy.controller;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.support.GenericXmlApplicationContext;

import com.buildhappy.service.AuthorService;

/**
 * 
 */
public class LoginCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public LoginCheck() {
        super();
        
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		//response.setContentType("html/text;charset=utf-8");
		Writer writer = response.getWriter();
		
		//get login information from authorLogin.jsp
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		HttpSession session = request.getSession();
		session.setAttribute("userName", name);
		
		//get user infor from database
		GenericXmlApplicationContext gxc = new GenericXmlApplicationContext();
		gxc.load("classpath:web-app.xml");
		gxc.refresh();
		AuthorService authorService = gxc.getBean("authorService", AuthorService.class);
		//writer.write(request.getContextPath() + "\n");
		if(authorService.selectPasswordByName(name).equals(password)){
			//writer.write("success");
			response.sendRedirect(request.getContextPath() + "/adminView/index.jsp");
		}else{
			writer.write("password false");
		}
	}

}
