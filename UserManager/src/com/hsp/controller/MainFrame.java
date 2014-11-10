package com.hsp.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hsp.domain.User;

public class MainFrame extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset = utf-8");
		PrintWriter out = response.getWriter();
		
	    //取response.sendRedirect方法传递过来的参数
		String username = request.getParameter("uname");
		String pwd = request.getParameter("pwd");
		
		//Session方法传过来的参数
		String username2 = (String) request.getSession().getAttribute("loginuname");
		
		//Session方法传过来的参数（对象）
		User user = (User) request.getSession().getAttribute("userObj");
		
		
		//显示提示信息
		out.println("<h1>主界面</h1>"+username + "pwd=" + pwd + "<br/>");
		out.println(username2 + "<br/>");
		out.println(user.getName() + "pwd=" + user.getPwd() +"<br/>");
		out.println("<a href = '/UserManager/LoginServlet'>返回重新登录</a>");
		
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);

	}

}
