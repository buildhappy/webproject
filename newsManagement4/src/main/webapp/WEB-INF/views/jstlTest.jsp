<%@page import="org.springframework.context.support.FileSystemXmlApplicationContext"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="com.buildhappy.domain.News"%>
<%@page import="com.buildhappy.dao.NewsDao"%>
<%@page import="java.util.Set" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.List" %>
<%@page import="java.util.Iterator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%> <!-- el表达式 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>

<body>  
	<c:out value="${RequestScope.getContextPath()}"></c:out>
	${RequestScope.getContextPath()}
<!-- el表示式 
      	<h1>username : ${username} </h1>
      	-->
 <!-- jstl标签 -->    
  	
     <h1>使用foEach遍历collect集合</h1>
      	<c:forEach var="name" items="${names}" begin="0"  end="1" varStatus="s">
      		name: ${name}
      		<c:out value="${name}"></c:out>
      		索引：<c:out value="${s.index}"/><br/>
      		总共迭代次数：<c:out value="${s.count }"/><br/>
      		是否是最后一个：<c:out value="${s.first }"/><br/>
      		<br/>
      	</c:forEach>
      	
     <h1>使用if判断语句</h1>
      	<c:if test="${username}==null" var="res"></c:if><!-- var记录判断结果 -->
      	username为空：<c:out value="${res }"/>
      	
     <h1>set用于将变量存取于JSP范围中或JavaBean属性中</h1>
      	<jsp:useBean id="author" class="com.buildhappy.domain.Author"></jsp:useBean>
      	<h2>设置普通变量的值</h2>
      	<c:set value="张三" var="name1"/>
      	<c:set var="name2" scope="session">王五</c:set>
      	<c:out value="${name1 }"/></br>
      	<c:out value="${name2 }"/>
      	<h2>设置bean对象的值</h2>
      	<c:set target="${author }" property="name">buildhappy</c:set>
      	<c:out value="${author.name }"></c:out>
      	
     <h1>remove用来从指定的jsp范围内移除指定的变量</h1><br/>
      	      	
      	
     <h1>url用于动态生成一个String类型的URL</h1>
      	<c:url value="http://localhost:8080/newsManagement4" var="url" scope="session">
      	</c:url>
      	<a href="${url }" >首页</a>
     <h1>forTokens用于浏览字符串，并根据指定的字符串截取字符串</h1>
         <c:forTokens items="北.京.邮.电.大.学" delims="." var="s1">
         	<c:out value="${s1 }"/>
         </c:forTokens>
     <h1>import把其他静态或动态文件包含到JSP页面</h1>
      	<c:catch var="error1">      	
      		<c:import url="http://www.baidu.com"></c:import>
      	</c:catch>
      	<c:out value="${error1 }"/>
      	
     <h1>redirect该标签用来实现请求的重定向</h1>
      	<c:redirect url="http://localhost:8080/newsManagement4">
      		<c:param name="username">buildhappy</c:param>
      		<c:param name="password">123456</c:param>
      		则运行后，页面跳转为：http://localhost:8080/newsManagement4/?uname=lihui&password=11111
      	</c:redirect>
</body>
</html>