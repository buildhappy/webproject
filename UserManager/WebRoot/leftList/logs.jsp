<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head><title>管理界面</title><LINK href='left.css' type='text/css' rel='stylesheet'></LINK></head>
	<body  marginheight="20">
		<script language="javascript" type="text/javascript">
			setTimeout("javascript:window.top.frames['mainFrame'].location.href='../mainFrame/log_filter.jsp'",0);
		</script>
		<table >
		<tr><td height="40"><a href='../mainFrame/log_filter.jsp' target='mainFrame'>过滤日志</a></td></tr>
		<tr><td height="40"><a href='../mainFrame/log_access.jsp' target='mainFrame'>访问日志</a></td></tr>
		<tr><td height="40"><a href='../mainFrame/log_admin.jsp' target='mainFrame'>管理日志</a></td></tr>
		<tr><td height="40"><a href='../mainFrame/log_reportForm.jsp' target='mainFrame'>报表</a></td></tr>
		</table>
	</body>
</html>
