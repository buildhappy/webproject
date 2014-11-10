
package com.buildhappy.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/news")//负责处理查看新闻类的请求
public class IndexController {
	@RequestMapping(value="/index")//匹配请求：localhost:8080/newsManagement3.1Beta/index/
	public String indexPage(){
		return "home";
	}
	
	@RequestMapping(value="/index.html")//匹配请求：localhost:8080/newsManagement3.1Beta/index/
	public void indexPage2(HttpServletRequest request,HttpServletResponse response){
		String username = request.getParameter("username");
		System.out.println("username : " + username);
		try {
			PrintWriter out = response.getWriter();
			out.println("user name :" + username);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return "index";//视图名传递给视图解释器
	}
	
	@RequestMapping("index")
	public String indexPage3(){
		return "index";
	}
	
	@RequestMapping(value="login.html")//匹配请求：localhost:8080/newsManagement3.1Beta/index/
	public void login(HttpServletRequest request,HttpServletResponse response){
		String username = request.getParameter("name");
		System.out.println("username : " + username + "<br/>");
		try {
			PrintWriter out = response.getWriter();
			out.println("user name :" + username);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//return "index";//视图名传递给视图解释器
	}
	
	
}

