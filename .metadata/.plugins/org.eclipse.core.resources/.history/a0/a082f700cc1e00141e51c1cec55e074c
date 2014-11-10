<%@page import="org.springframework.context.support.FileSystemXmlApplicationContext"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="com.buildhappy.domain.News"%>
<%@page import="com.buildhappy.service.NewsService"%>
<%@page import="java.util.Set" %>
<%@page import="java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <!----------查看文章 admin/article.php/index函数-->
  <head>
    <!--点击“查看文章”显示模板-->
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/style/admin/css/public.css"/>
    <title>Document</title>
  </head>
  <body>
    <table class="table">
      <tr>
        <td class="th" colspan="10">查看新闻</td>
      </tr>
      <tr>
        <td>NewsID</td>
        <td>作者</td>
        <td>栏目</td>
        <td>标题</td>
        <td>发表时间</td>
        <td>操作</td>
      </tr>
      <%
      	ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:web-app.xml");
     	NewsService newsService= ctx.getBean("newsService" , NewsService.class);
     	Set<News> newses;
     	newses = newsService.selectNewsByAuthor("buildhappy");//session.getAttribute("userName").toString()
     	
     	for(News news:newses){
     		out.print("<tr>");
     		out.print("<td>" + news.getId() + "</td>");
     		out.print("<td>" + news.getAuthor() + "</td>");
     		out.print("<td>" + news.getType() + "</td>");
     		out.print("<td>" + news.getTitle() + "</td>");
     		out.print("<td>" + news.getCreateTime().toLocaleString() + "</td>");
     		out.print("[<a href=''>编辑</a>] </td>");
     		out.print("<td> [<a href=" + request.getContextPath() + "/DeleteNews?newsId="+ news.getId() + ">删除</a>] ");
     		out.print("<td>" + request.getContextPath() + "/DeleteNews?newsId="+ news.getId());
     		out.print("</tr>");
     	}
     	
      %>
    </table>
    <div class="page">  <!--存放分页,接受admin/article.php/index的-->
      <?php echo $links ?>
    </div>
  </body>
</html>