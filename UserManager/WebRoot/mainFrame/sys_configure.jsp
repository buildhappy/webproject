<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:useBean id="bean" class="com.cn.BeanWh.BeanWh"></jsp:useBean>
<html>
	<head>
		<title>waf信息</title>
		<LINK href=kangle.css type='text/css' rel=stylesheet>
	</head>
	<body bgcolor=>
		<form name="f1" id="f1" action="../control/proxy_conf.jsp" method="post">
			<table width='98%'>
				<tr>
					<td>
						<h3>代理配置</h3>
						<table>
							<tr>
								<td>代理IP/URL:</td>
								<td><input type="text" name="address" id="address" value=<%
									String address = bean.infoAdd();
									if(address.length() <= 3){
										address = "未配置";
									}
								%><%=address%>></td>
							</tr>
						</table>
					</td>
				</tr>
	
				<tr>
					<td colspan="2" align="center"><input type="submit" value="提交"></td>
				</tr>
			</table>
		</form>
	</body>
</html>