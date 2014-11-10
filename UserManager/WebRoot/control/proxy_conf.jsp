<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<jsp:useBean id="bean" class="com.cn.BeanWh.BeanWh"></jsp:useBean>
<%
	String conf_back_path = "F:\\httpd.conf.bak";
	String conf_path = "F:\\httpd.conf";
%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'proxy_conf.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script language="javascript" type="text/javascript">
		setTimeout("javascript:window.top.frames['mainFrame'].location.href='mainFrame/sys_configure.jsp'",1000);
	</script>
	
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
    <% 
  		String address = request.getParameter("address"); 
    %>
    	配置的代理路径为：<%=address%>
	<hr />
    <p>1秒后自动跳转</p>
    <%
    	//bean.setAddress(address);
     	//bean.cpConfig(conf_back_path,conf_path);
     	//bean.conConfig();
     	bean.insertStringInFile(address, "proxy", null);
    %>  <br>
  </body>
</html>
