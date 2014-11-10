<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>out隐式对象注意的问题</title>
</head>
<body>
	<%
	//结果是先看到wowo,后出现hahha，
	//原因是：out是一个带缓存功能的PrintWriter，要等缓冲区满了以后才会输出
		out.print("hahha");
		response.getWriter().write("wowo");
	%>
</body>
</html>