<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>景区登录网站</title>
</head>
<body>
	<c:if test="${!empty error }">
		<font color="red"><c:out value="${error}"/></font>
	</c:if>
	<!--  <form action='<c:url value="/admin/loginCheck.html"/>' method="post">-->
<form action="<%=request.getContextPath() %>/admin/loginCheck.html" method="get">
   		 用户名：
    <input type="text" name="userName">
    <br>
   		 密&nbsp&nbsp&nbsp&nbsp码：
    <input type="password" name="password">
    <br>
    <input type="submit" value="登录"/>
    <input type="reset" value="重置"/>
</form>
</body>
</html>