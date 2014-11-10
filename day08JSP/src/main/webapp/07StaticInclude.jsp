<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>动态包含(这种情况是运行时包含)</title>
</head>
<body>
	<%
		request.getRequestDispatcher("/public/head.jsp");
	%>
	<%
		response.getWriter().write("afsafsaf");
	%>
	<%
		request.getRequestDispatcher("/public/food.jsp");
	%>
</body>
</html>