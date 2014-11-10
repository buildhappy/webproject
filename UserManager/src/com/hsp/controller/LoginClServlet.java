package com.hsp.controller;

import java.io.IOException;
import java.io.PrintWriter;
import com.hsp.*;
import com.hsp.domain.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//登录管理
public class LoginClServlet extends HttpServlet {
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset = utf-8");
			PrintWriter out = response.getWriter();
			
			//接受由LoginServlet发送过来的提交信息
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			//检查一下是否接受到
			System.out.println(username);
			
			if("buildhappy".equals(username) && "123".equals(password)){
							
				//request语句要在response之前
				//使用session来传递数据给下一个页面
				request.getSession().setAttribute("loginuname", username);

				//session 可以传递对象
				
				User user = new User();
				user.setName(username);
				user.setPwd(password);
				request.getSession().setAttribute("userObj", user);
				
				
				//跳转到下一个页面【servlet提供了两种跳转:Sendredirct转向    forward转发】
				//sendRedirect的URl应该这样写    web应用名/servlet url
				response.sendRedirect("/UserManager/MainFrame?uname=" + username + "&pwd=" + password);
				
			}
			else{
				response.sendRedirect("/UserManager/LoginServlet");
			}
			
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		this.doGet(request, response);
	}

}
