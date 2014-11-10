<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<jsp:useBean id="bean" class="com.cn.BeanWh.RuleOperate"></jsp:useBean>
<jsp:useBean id="format" class="com.cn.BeanWh.FormatXML"></jsp:useBean>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">    
    <title>My JSP 'black_rules_con.jsp' starting page</title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
	<script language="javascript" type="text/javascript">
		setTimeout("javascript:window.top.frames['mainFrame'].location.href='mainFrame/rules_blacklist.jsp'",1000);
	</script>
  </head> 

  <body >
  	<%
    	String [] rules = request.getParameterValues("rules");//获取了选中的属性；
     	String filePath = this.getClass().getResource("/").getPath().replace("WEB-INF/classes", "mainFrame/config.xml");
    	//System.out.println(path);
    	//System.out.println(basePath+"config.xml");
    	//String ss="/home/shuizhihun/file/config.xml";
    	String configPath = "/usr/local/tomcat7/webapps/Nwaf/mainFrame/config.xml"; 
    	String apacheConPath="/usr/local/apache/conf/httpd.conf";
    	
    	if(rules!=null){
	    	bean.deleteXML(configPath);//delete items;
	    	format.formatXML(configPath);   	
	    	bean.InsertXML(rules,configPath);//add selected items
	    	format.formatXML(configPath);
	    	bean.editHttpd(apacheConPath, rules);//edit httpd.conf of apache server;
	    	bean.restartApache();//restart apache server;
    	}
    %>

    <br>
	配置的规则有：<%for(int j=0;rules != null && j<rules.length;j++){%> <%=rules[j]%><%}%>

	<hr />
    <p>即将跳转回前页</p>
  </body>
</html>
