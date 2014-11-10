<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!-- 导入标签库的uri，位于standard包下的META-INF文件夹下的c.tld中 -->
<%@page import="cn.itcast.domain.Person"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>使用jstl+el完成集合迭代</title>
  </head>
  
  <body>
   <% 
    	List list = new ArrayList();
    	list.add(new Person("aaa"));
    	list.add(new Person("bbb"));
    	list.add(new Person("ccc"));
    	
    	request.setAttribute("list",list);
    %>
    <c:forEach var="person" items="${list}">
    	${person.name }<br/>
    </c:forEach>
    
    
     <br/>
     <% 
     	Map map = new HashMap();
     	map.put("aa",new Person("aaaaa"));
     	map.put("bb",new Person("bbbbb"));
     	map.put("cc",new Person("ccccc"));
     	map.put("dd",new Person("ddddd"));
     	map.put("111",new Person("eeeee"));
     	request.setAttribute("map",map);
     %>
     
     <c:forEach var="entry" items="${map}"><!-- 实际上是对Set<map.Entry>进行迭代 -->
     	${entry.key } :  ${entry.value.name}  <br/>
     </c:forEach>
    
    
    <!-- 代表用户登陆了 -->
    <c:if test="${user!=null}">   
    	欢迎您：${user.username }
    </c:if>
    
    <c:if test="${user==null}">
    	用户名：<input type="text">
    	密码：<input type="text">
    </c:if>
  </body>
</html>
