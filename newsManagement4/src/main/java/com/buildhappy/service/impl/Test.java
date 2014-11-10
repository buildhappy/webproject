package com.buildhappy.service.impl;

import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Test
 */
public class Test extends HttpServlet {
    public Test() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Writer writer = response.getWriter();
		writer.write(request.getContextPath());
		InetAddress ip ;
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		String s = dateFormat.format(new Date());
		System.out.println(s);
		System.out.println(request.getParameter("id"));
		Date date=new Date(System.currentTimeMillis());
		System.out.println(date.toLocaleString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
