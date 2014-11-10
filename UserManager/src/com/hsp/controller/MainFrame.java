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
		
	    //ȡresponse.sendRedirect�������ݹ����Ĳ���
		String username = request.getParameter("uname");
		String pwd = request.getParameter("pwd");
		
		//Session�����������Ĳ���
		String username2 = (String) request.getSession().getAttribute("loginuname");
		
		//Session�����������Ĳ���������
		User user = (User) request.getSession().getAttribute("userObj");
		
		
		//��ʾ��ʾ��Ϣ
		out.println("<h1>������</h1>"+username + "pwd=" + pwd + "<br/>");
		out.println(username2 + "<br/>");
		out.println(user.getName() + "pwd=" + user.getPwd() +"<br/>");
		out.println("<a href = '/UserManager/LoginServlet'>�������µ�¼</a>");
		
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);

	}

}
