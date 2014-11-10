<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>脚本片段</title>
</head>
<body>
	<%
		int x = 1;
	%>
	aaaa
	<hr/>
	<%
		out.print(x);
	%>
	<%
		for(int i = 0; i < 2; i++){
	%>
	<%
		out.print(x + "<hr/>");
	}
	%>
</body>
</html>