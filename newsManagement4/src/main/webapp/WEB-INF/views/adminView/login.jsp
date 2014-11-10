<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../resources/style/admin/css/login.css" />
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/style/admin/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/style/admin/js/login.js"></script>
<title>login page</title>
</head>
<body>
	<div id="divBox">
		<form action="<%=request.getContextPath()%>/LoginCheck" method="POST"
			id="login">
			<input name="name" type="text" id="userName" /><br />
			<input name="password" type="password" id="psd" /><br />
			<input type="" id="verify" name="captcha" />
			<input type="submit" id="sub" />
			<div class="captcha">
				<img src="<%=request.getContextPath()%>/PictureCheckCode" alt=""/>
			</div>
		</form>
		<div class="four_bj">
			<p class="f_lt"></p>
			<p class="f_rt"></p>
			<p class="f_lb"></p>
			<p class="f_rb"></p>
		</div>
	</div>
</body>
</html>