<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'view.jsp' starting page</title>
  </head>
  
  <body>
  	
  	<font color="red" size="100px">
  		${data }
  		<br/>
  		哈哈
  		<%
  		out.println(pageContext.getAttribute("data"));
  		%>
  	</font>
  </body>
</html>
