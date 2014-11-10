<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>jsp:setProperty标签</title>
  </head>
  
  <body>
    
    <jsp:useBean id="person" class="cn.itcast.domain.Person"/>
    
    <!-- 1.手工为bean属性赋值 value-->
    <jsp:setProperty name="person" property="name" value="xxxx"/>
    <%=person.getName() %><br/>
    
    
    <!-- 2.用请求参数给bean的属性赋值 param  http://localhost:8080/day09/2.jsp?name=uuuuuuuuuuuuu -->
    <jsp:setProperty name="person" property="name" param="name"/>
    
    	<!-- http://localhost:8080/day09/2.jsp?name=uuuuuuuu&age=12 -->
    <jsp:setProperty name="person" property="age" param="age"/>  <!-- 支持8种基本数据类型的转换（把客户机提交的字符串，转成相应的8种基本类型，赋到bean的属性上） -->
   
   		<!-- http://localhost:8080/day09/2.jsp?name=uuuuuuuu&age=12&birthday=1980-09-09 -->
   
    <jsp:setProperty name="person" property="birthday" value="<%=new Date() %>"/><!-- 通过手动赋值(当前值) -->
   
    <%=person.getName() %><br/>
    <%=person.getAge() %><br/>
    <%=person.getBirthday() %><br/>
    
    
    <br/>----------------------------------------------------<br/>
    
    <!-- 3.用所有的请求参数为bean赋值，请求参数与类属性名称要一致 -->
    	<!-- http://localhost:8080/day09/2.jsp?name=uuuuuuuu&age=12 -->
    <jsp:setProperty name="person" property="*"/>
    <%=person.getName() %><br/>
    <%=person.getAge() %><br/>
    
    
    <jsp:getProperty name="person" property="name"/><!-- 将结果输出到页面上 -->
    <jsp:getProperty name="person" property="age"/>
    <jsp:getProperty name="person" property="birthday"/>

  </body>
</html>
