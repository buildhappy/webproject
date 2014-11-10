package com.hsp.controller;

import java.io.IOException;
import java.io.PrintWriter;
import com.hsp.*;
import com.hsp.domain.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//��¼����
public class LoginClServlet extends HttpServlet {
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset = utf-8");
			PrintWriter out = response.getWriter();
			
			//������LoginServlet���͹������ύ��Ϣ
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			//���һ���Ƿ���ܵ�
			System.out.println(username);
			
			if("buildhappy".equals(username) && "123".equals(password)){
							
				//request���Ҫ��response֮ǰ
				//ʹ��session���������ݸ���һ��ҳ��
				request.getSession().setAttribute("loginuname", username);

				//session ���Դ��ݶ���
				
				User user = new User();
				user.setName(username);
				user.setPwd(password);
				request.getSession().setAttribute("userObj", user);
				
				
				//��ת����һ��ҳ�桾servlet�ṩ��������ת:Sendredirctת��    forwardת����
				//sendRedirect��URlӦ������д    webӦ����/servlet url
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
