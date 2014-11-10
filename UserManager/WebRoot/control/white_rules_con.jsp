<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<jsp:useBean id="bean" class="com.cn.BeanWh.RuleOperate"></jsp:useBean>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>正在处理</title>
	<script language="javascript" type="text/javascript">
		setTimeout("javascript:window.top.frames['mainFrame'].location.href='../mainFrame/rules_whitelist.jsp'",1000);
	</script>
</head>
<body>

<h1>提交任务正在处理....</h1>   
<%
   String [] rules = request.getParameterValues("hidden1");//获取了选中的属性；
   session.setAttribute("arr",rules);
   session.setMaxInactiveInterval(900);//设置session的时间，单位为s；    
   if(rules==null){	  
      System.out.println("rules is null");
   }else{	  
	  //for(int i=0;i<rules.length;i++)
	  //System.out.println(rules[i]);
	  bean.whilteGenerator(rules);
	  bean.restartApache();
  } 
%>

</body>
</html>