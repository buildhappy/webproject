<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  	<head>
  		<title>登陆界面</title>
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath }/css/login.css"/>
	</head>
	
	<body>
		<div id="container">
			<div id="image">
				<div id="form">
					<form action="${pageContext.request.contextPath }/servlet/LoginServlet" method="post">
						<div class="input">
							用户：<input class="inputtext" type="text" name="username" />
						</div>
						<div class="input">
							密码：<input class="inputtext" type="password" name="password" />
						</div>
						<div id="btn">
							<input class="btn" type="button" value="注册" onclick="window.location.href='register.html'" />
							<input class="btn" type="submit" value="登陆" />
						</div>
					</form>
				</div>
			</div>
		</div>
	</body>
</html>
