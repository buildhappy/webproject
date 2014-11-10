<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head><title>管理界面</title><LINK href='left.css' type='text/css' rel='stylesheet'></LINK></head>
	<body  marginheight="20">
		<script language="javascript" type="text/javascript">
			setTimeout("javascript:window.top.frames['mainFrame'].location.href='../mainFrame/user_list.jsp'",0);
		</script>
		<table >
			<tr><td height="40"><a href='../mainFrame/user_list.jsp' target='mainFrame'>列表</a></td></tr>
			<tr><td height="40"><a href='../mainFrame/user_add.jsp' target='mainFrame'>添加</a></td></tr>
			<tr><td height="40"><a href='../mainFrame/user_del.jsp' target='mainFrame'>删除</a></td></tr>
			<tr><td height="40"><a href='../mainFrame/user_edi.jsp' target='mainFrame'>编辑</a></td></tr>
		</table>
	</body>
</html>
