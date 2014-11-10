package com.hsp.view;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset = utf-8");
		PrintWriter out = response.getWriter();
		
		//返回一个界面(要求html技术) 
		out.println("<img src = 'imgs/11.png'/ ><hr/>");
		out.println("<h1>用户登录</h1>");
		//action 应该怎样写 /web应用名/servlet的url
		out.println("<form action = '/UserManager/LoginClServlet' method = 'post'>");
		out.println("用户名:<input type = 'text' name = 'username'/><br/>");
		out.println("密　码:<input type = 'password' name = 'password'/><br/>");
		out.println("<input type = 'submit' value = '登录'/><br/>");
		out.println("</form>");

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

}
