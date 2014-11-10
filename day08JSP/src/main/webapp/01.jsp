<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<!-- 输出当前时间 -->
	<font color="red">
		当前时间：
	</font>
	<%
		Date date = new Date();
		String time = date.toLocaleString();
	%>
	<%=date.toLocaleString() %><!-- 脚本表达式，用于向浏览器输出数据 -->
</body>
</html>