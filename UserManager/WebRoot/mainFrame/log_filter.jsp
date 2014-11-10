<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:useBean id="bean" class="com.cn.BeanWh.BeanWh"></jsp:useBean>
<html>
<head><title>管理界面</title><LINK href='left.css' type='text/css' rel='stylesheet'></LINK></head>
	<body  marginheight="20">
	<%
	String outpnum = "0";
	if (application.getAttribute("sqlnum")!=null){outpnum = application.getAttribute("sqlnum").toString();}
	%>
		<%//String serverIP = "10.108.114.60"; %>
		<style type="text/css">
			a:link{text-decoration:none ; color:white ;}
			a:visited {text-decoration:none ; color:white;}
			a:hover {text-decoration:underline ; color:white;}
			a:active {text-decoration:none ; color:white;}
		</style>
	<!--	<form name="f1" id="f1" action="index_conf.jsp" method="post">  -->
		<form name="f1" id="f1" action="../control/logdir_conf.jsp" method="post">
			<table >
				<tr><td height="40">日志-过滤日志</td></tr>
				<tr>
					<td>日志文件路径:</td>
					<td><input type="text" name="logdir" id="ldir" value=<%
								String address = bean.logAdd();
								if(address.length() <= 3){
									address = "未配置";
								}
							%><%=address%>></td>
				</tr>
				<tr>
					<td>本次处理日志数:</td>
					<td><input type="text" name="log_num" id="lnum" value=<%=outpnum%>></td>
				</tr>
			</table>
			<table>
				<tr>
					<td colspan="2" align="left"><a href="javascript:if(confirm('确认清除日志？')){ window.location='../control/cleanlog-wh.jsp';}"><input type="button" value="清空日志"></a></td>
					
					<td colspan="2" ><input type="submit" value="修改日志路径"></td>
					
					<td colspan="2" ><a href="../control/log_ana.jsp"><input type="button" value="分析日志"></a></td>
					
					<td colspan="2" ><a href="../control/SQLshow.jsp"><input type="button" value="数据库信息"></a></td>
					<!--  
					<%//String showHref = "http://" +serverIP+ ":8081/"; %>
					<td colspan="2" ><a href=<%//=showHref %>><input type="button" value="日志展现"></a></td>
					-->
					
				</tr>
			</table>
		</form>
	<!--	</form>  -->
	</body>
</html>