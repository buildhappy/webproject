<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>
<jsp:useBean id="bean" class="com.cn.BeanWh.BeanWh"></jsp:useBean>
<%
	String conf_back_path = "F:\\httpd.conf.bak";
	String conf_path = "F:\\httpd.conf";
%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
request.setCharacterEncoding("GBK");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script language="javascript" type="text/javascript">
		setTimeout("javascript:window.top.frames['mainFrame'].location.href='mainFrame/log_filter.jsp'",1000);
	</script>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
  	<%
    	String logdir = request.getParameter("logdir");   
    %>
    <br>
	配置的日志路径为：<%=logdir%>
	<hr />
    <p>即将跳转回前页</p>
    <%
		bean.insertStringInFile(logdir, "logdir", null);
    %>     
  </body>
</html>

