<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.itcast.domain.Person"%>
<%@page import="cn.itcast.domain.Address"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>el表达式</title>
  </head>
  
  <!-- el表达式用于获取request、session、applection域中的数据,将结果显示到页面 
  	         格式：${标识符}-->
  
  <body>
    <% 
    	String data = "abcd";
    	request.setAttribute("data",data);
    %>
    ${data }   <%-- pageContext.findAttribute("data") page request  session application--%>
    
    <br/>
    <% 
    	Person p = new Person();
    	p.setName("aaaa");
    	
    	request.setAttribute("person",p);
    %>
    ${person.name }   <%-- pageContext.findAttribute("person") page request  session application--%>
    
    
     <br/>
    <% 
    	Person p1 = new Person();
    	Address a = new Address();
    	a.setCity("上海");
    	p1.setAddress(a);
    	request.setAttribute("p1",p1);
    %>
    ${p1.address.city }
    
    <br/>
    
    <% 
    	List list = new ArrayList();
    	list.add(new Person("aaa"));
    	list.add(new Person("bbb"));
    	list.add(new Person("ccc"));
    	
    	request.setAttribute("list",list);
    %>
    ${list[1].name }  
    
     <br/>
     <% 
     	Map map = new HashMap();
     	map.put("aa",new Person("aaaaa"));
     	map.put("bb",new Person("bbbbb"));
     	map.put("cc",new Person("ccccc"));
     	map.put("dd",new Person("ddddd"));
     	map.put("111",new Person("eeeee"));
     	request.setAttribute("map111",map);
     %>
    
    ${map111.bb.name }
    ${map111.dd.name }
    ${map111['111'].name }  <%--用el表达式在取数据时，通常用.号，.号取不出来时，用[] --%>
    <br/>
    
    <!-- 获取当前web应用的名称 -->
           当前应用地址：${pageContext.request.contextPath }
    <br/>
    <a href="${pageContext.request.contextPath }/index.jsp">点点</a>
  </body>
</html>
