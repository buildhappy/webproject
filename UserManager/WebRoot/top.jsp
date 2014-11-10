<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>
<jsp:useBean id="bean" class="com.cn.BeanWh.BeanWh"></jsp:useBean>
<jsp:useBean id="apache" class="com.cn.ApacheWh.Apache"></jsp:useBean>
<%
	//String s = (String)session.getAttribute("langchange"); 
	//if(s == null){
	//	s = "0";
	//}
	//int i =  Integer.parseInt(s);
%>
<html>
	<head>
		<title>kangle(2.8.3) 信息</title>
		
		<%application.setAttribute("now", apache.getLoadTime()); %>
		<style type="text/css">
			body {background-image:url(ss.gif)  center; flaot:left;}
			img{float:left;}
			table{margin: 2px 5px; }
			div{margin:0px;padding:0px;float:left;}
			form{margin:0px;padding:0px;float:left;}
			a:link{text-decoration:none ; color:white ;}
			a:visited {text-decoration:none ; color:white;}
			a:hover {text-decoration:underline ; color:white;}
			a:active {text-decoration:none ; color:white;}
		</style>
	</head>
	<body bgcolor="white">
	<img src="waf-title.bmp" style="position:relative;left:108px;" width="80%" height=110px>
	<br>			
		<table>          	 
			<tr>
	<!--				<form action='change_lang.jsp' method='post' target=_top>
							<div align="right"  >:
								<select name='lang'>
									<option value='en' >en</option>
									<option value='zh_CN' selected>zh_CN</option>
								</select>
								<input type=submit value=''>
							</div>
						</form>border-collapse="0"
						<a href='leftList/system.jsp' title='日志信息' target='leftFrame'>
	-->
				<div style="position:relative; top:0px; ">
					<form action='leftList/system.jsp' title='总体信息' target='leftFrame'><input type=submit value="系统" style="width:120px;height:40px;position:relative;left:108px;" ></form>
					<form action='leftList/rules.jsp' title='规则信息' target='leftFrame'><input type=submit value="规则" style="width:120px;height:40px;position:relative;left:97px;"></form>
					<form action='leftList/logs.jsp' title='日志信息' target='leftFrame'><input type=submit value="日志" style="width:120px;height:40px;position:relative;left:86px;" ></form>
					<form action='leftList/users.jsp' title='用户信息' target='leftFrame'><input type=submit value="用户" style="width:120px;height:40px;position:relative;left:75px;" ></form>
					<form action='leftList/help.jsp' title='帮助信息' target='leftFrame'><input type=submit value="帮助" style="width:120px;height:40px;position:relative;left:64px;" ></form>
				</div>
			</tr>	
		</table>	
	</body>
</html>