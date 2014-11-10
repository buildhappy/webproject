<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>首页</title>
  </head>
  
  <body style="text-align: center;">
  	
  	<h1>xxxx网站</h1>
  	<br/>
  	<%
  		out.println("hah ");
  	%>
  	<div style="text-align: right;">	
  	<c:if test="${user!=null}">
  		欢迎您：${user.nickname }  
  		<a href="${pageContext.request.contextPath }/servlet/LogoutServlet">注销</a>
  	</c:if>
  	
  	<c:if test="${user==null}">
	    <a href="${pageContext.request.contextPath }/servlet/RegisterUIServlet">注册</a>
	    <a href="${pageContext.request.contextPath }/servlet/LoginUIServlet">登陆</a>
    </c:if>
    </div>
    <hr>
  </body>
</html>