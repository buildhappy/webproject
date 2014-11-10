<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	<title>用户注册</title>
	<script type="text/javascript" src="${pageContext.request.contextPath }/js/ShowCalendar.js"></script>
	<style>
		body{
			background-color:#32a8cf;margin:0px;font-size:14px;color:#FFFFFF;
		}
		#main{
			margin-left:50px;margin-top:30px;
		}
		#notice{
			width:70%;line-height:25px;
		}
		#formtable{
			font-size:15px;
		}
		.td1{
			width:100px;
		}
		#formsubmit{
			width:300px;text-align:center;margin-top:15px;margin-left:50px;
		}
		.userinput {
			width:210px; height:25px; background-color:#FFFFFF; border:solid 1px #7dbad7; 
			font-size:16px;
		}	
		.btn{
			background-image:url(images/b2222.gif);
			background-position: center;
			font:bold 12px;
			width:80px;
			cursor:hand;
			height:25px;
			border-width:0;  /*按纽必须有此样式，才会出掉按纽背景*/
		}
	</style>
</head>

<body>
	<div id="header">
		<img src="images/head.gif">	
	</div>
		
	<div id="main">	
		<div id="notice">
			<h2>注册须知：</h2> 
			1:在本站注册的会员，必须遵守《互联网电子公告服务管理规定》，不得在本站发表诽谤他人，侵犯他人隐私，侵犯他人知识产权，传播病毒，政治言论，商业机密等信息。<br/>
			2:在所有在本站发表的文章，本站都具有最终编辑权，并且保留用于印刷或向第三方发表的权利，如果你的资料不齐全，我们将有权不作任何通知使用你在本站发布的作品。<br/> 
		</div>
		<br/><br/>
		
		<form action="${pageContext.request.contextPath }/servlet/RegisterServlet" method="post">
		<table id="formtable">
			<tr>
				<td class="td1">登陆帐号：</td>
				<td>
					<input class="userinput" type="text" name="username" value="${form.username }">
					<span class="message">${form.errors.username }</span>
				</td>
			</tr>
			
			<tr>
				<td></td>
				<td></td>
			</tr>
			
			<tr>
				<td class="td1">重复密码：</td>
				<td>
					<input class="userinput" type="password" name="password" value="${form.password }">
					<span class="message">${form.errors.password }</span>
				</td>
			</tr>
			
			<tr>
				<td class="td1">确认密码：</td>
				<td>
					<input class="userinput" type="password" name="password2"  value="${form.password2 }">
					<span class="message">${form.errors.password2 }</span>	
				</td>
			</tr>
			
			<tr>
				<td class="td1">电子邮箱：</td>
				<td>
					<input class="userinput" type="text" name="email"  value="${form.email }">
					<span class="message">${form.errors.email }</span>
				</td>
			</tr>
			
			<tr>
				<td class="td1">生日：</td>
				<td>
					<input class="userinput" type="text" name="birthday" id="birthday" title="点击选择" onClick="showCalendar(this.id)" value="${form.birthday }">
					<span class="message">${form.errors.birthday }</span>
				</td>
			</tr>
			
			<tr>
				<td class="td1">您的呢称：</td>
				<td>
					<input class="userinput" type="text" name="nickname" value="${form.nickname }">
					<span class="message">${form.errors.nickname }</span>	
				</td>
			</tr>
			
			<tr>
				<td class="td1">图片认证：</td>
				<td>
					<input class="userinput" type="text" name="client_checkcode">
					<img src="${pageContext.request.contextPath }/servlet/ImageServlet"  onclick="this.src=this.src + '?' + new Date().getTime()" height="23px" width="120px" alt="看不清" style="cursor: hand;">
				</td>
			</tr>
			
			<tr>
				<td class="td1"></td>
				<td>
					<span class="message">${form.errors.client_checkcode }</span>	
				</td>
			</tr>
			
		</table>
		<div id="formsubmit">
			<span><input class="btn" type="reset" name="reset" value="重 置"></span>
			&nbsp;&nbsp;
			<span><input class="btn" type="submit" name="submit" value="注 册"></span>
		</div>	
		</form>
	</div>	
	<div id="footer">
		
	</div>
</body>
</html>
