<%@page import="org.springframework.context.support.FileSystemXmlApplicationContext"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="com.buildhappy.domain.News"%>
<%@page import="com.buildhappy.dao.NewsDao"%>
<%@page import="java.util.Set" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.List" %>
<%@page import="java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
      	<%
      		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:web-app.xml");
 			NewsDao newsService= ctx.getBean("newsDao" , NewsDao.class);
     		List<News> newses;
     		newses = newsService.selectTop3LatestNewsOfAll();
     		Iterator it = newses.iterator();
     		News news;
     		String url;
      		news = newsService.selectLatestNewsOfTheType("society");
        	url = request.getContextPath() + "/index/checkTheNews.jsp?newsId=" + news.getId();
        	out.print(news.getTitle().substring(0,10));
      	%>
</body>
</html>