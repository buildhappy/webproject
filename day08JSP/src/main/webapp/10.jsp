<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>数据传递</title>
</head>
<body>
<!-- 自定义标签 -->
<%
	pageContext.setAttribute("data" , "aaa");//pageContext的作用域--该jsp页面
	request.setAttribute("data" , "aaa");//作用域包括转发到的页面
	session.setAttribute("data" , "aaa");//整个会话范围
	application.setAttribute("data" , "aaa");//与所有的用户共享
%>
<%
	//用pageContext向session中存数据，使用pageContext为操作不同的域提供统一的接口
	pageContext.setAttribute("data" , "abaa" , pageContext.SESSION_SCOPE);
%>
<%=session.getAttribute("data")%>
<%=session.getAttribute("data" , pageContext.SESSON_SCOPE)%><!-- 指定从session域中取数据 -->

</body>
</html>