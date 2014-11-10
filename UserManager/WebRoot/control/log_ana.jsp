<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="java.io.*" %>
<jsp:useBean id="logana" class="com.cn.LogAna"></jsp:useBean>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'log_analyze.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
    <%
    	//调用logana.logInter()函数，完成日志分析，同时回传分析日志的数目（String型）
    	String outpnum = logana.logInter();
    	String serverIP = "10.108.114.60";
    %>
    <%
    	//设置应用变量（即在整个应用中生效的参数），用来传递分析日志数目
    	application.setAttribute("sqlnum", outpnum); 
    %>
    <%
    	//调用logOut()函数，完成数据库数据分析，生成日志输出页面
    	String s2 = logana.logOut();
    %>
<!--  <br> 完成分析；-->
    <br> 共分析数据：<%=outpnum%>条
    <br>
    <br><%if(s2!=null){%>
    	已从数据分析并生成网页
    <%}%>
    <hr />
<!-- 
	这种自动跳转可以传递变量参数，但参数传递一次即失效
    <meta http-equiv="refresh" content="3;log_module.jsp?outpnum=<%=outpnum%>"> 
-->
    	<% //这是另一种自动跳转方式，但无法带入变量参数 %> 
    	<%String url_jump = "1;http://" +serverIP+ ":8081/"; %>
    	<%response.setHeader("refresh",url_jump) ;%>
    <br>即将跳转回前页
  </body>
</html>
