<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>jsp:usebean标签的使用</title>
  </head>
  
  <body>
    <!-- userbean标签的标签体只在userBean标签实例化bean时才执行 
    	工作流程：先从session域中找person对象，如果没有在创建(名称为person)
    -->
    <jsp:useBean id="person" class="cn.itcast.domain.Person" scope="session">
    	bbbb
    </jsp:useBean>
    
   	<%=person.getName() %>
    
  </body>
</html>
