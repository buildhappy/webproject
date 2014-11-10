<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>用户登录界面</title>
		<link rel='stylesheet'href='/project-evaluation/resources/easyui/1.4/themes/metro/easyui.css' />
		<link rel='stylesheet' 	href='/project-evaluation/resources/easyui/1.4/themes/icon.css' />
	    <link rel='stylesheet' 	href='/project-evaluation/resources/easyui/1.4/themes/color.css' />
        <link type='text/css' rel='stylesheet' href='/project-evaluation/resources/custom/0.0.1/css/site_style.css'/>
        		
		<script src="/project-evaluation/resources/easyui/1.4/jquery.min.js"></script>
		<script src="/project-evaluation/resources/easyui/1.4/jquery.easyui.min.js"></script>
		<script src='/project-evaluation/static/easyui/1.4/locale/easyui-lang-zh_CN.js'></script>
	</head>
	<body>
		<div id='main' class='easyui-layout' style='width:100%;height:300px;'>
		<div region='north'  split='true' collapsible='false' style='width:300px;height:100px;padding:10px;'>
				<h2>用户登录界面</h2>
		</div>
		<div region='west' title='navigation' split='true' style='width:300px;height:100px'>
			<div id='nav' class='easyui-accordion' fit='true'>
				<div title='Base' iconCls='icon-save' style='overflow:auto;padding:10px;'>
					 <a href="{% url 'easyui:views_resizable' %}">resizable</a>
					 <br/>
				</div>
				<div title='Layout' iconCls='icon-man' style='overflow:auto;padding:10px;'>
					 <a href="{% url 'easyui:views_panel' %}">panel</a>
					 <br/>
					 <a href="{% url 'easyui:views_tabs' %}">tabs</a>
					 <br/>
					 <a href="{% url 'easyui:views_accordion' %}">accordion</a>
					 <br/>
					 <a href="{% url 'easyui:views_layout'  %}">layout</a>
				</div>
				<div title='Menu and Button' iconCls='icon-man' style='overflow:auto;padding:10px;'>
					 <a href="{% url 'easyui:views_linkbutton' %}">linkbutton</a>
					 <br/>
				</div>
				<div title='Form' iconCls='icon-man' style='overflow:auto;padding:10px;'>
					 <a class='easyui-linkbutton' href="{% url 'easyui:views_form' %}">form</a>
				</div>
				<div title='Window' iconCls='icon-help' style='overflow:auto;padding:10px;'>
					 <a href="{% url 'easyui:views_window' %}">window</a>
					 <br/>
					 <a href="{% url 'easyui:views_dialog' %}">dialog</a>
					 <br/>
					 <a href="{% url 'easyui:views_messager' %}">messager</a>
				</div>
				<div title='DataGrid and Tree' iconCls='icon-help' style='overflow:auto;padding:10px;'>
					 <h3>jQuery UI</h3>
				</div>
			</div>
		</div>
		<div region='center' title='center' split='true' style='width:300px;height:150px'>
			<form action="/project-evaluation/j_spring_security_check" method='post'>
				<div class="error-msg">
					<% 
						String error = request.getParameter("error");
						if ( error != null)
							out.println(error);
					%>
				</div>
				<br/>
				用户名: <input class='easyui-textbox' type='text' name='j_username'/> <br/> <br/>
				密&nbsp;&nbsp;码 : <input class='easyui-textbox' type='password' name='j_password'/> <br/><br/>
			
				<input  class="easyui-linkbutton" type='submit' value="登录"/>
			</form>
		</div>
		<div region='south' split='true' style='padding:10px;margin:0; height:50px;'>
			<div>
				copyright&copy;Greatdreams
			</div>
		</div>
	</div>	
	
	</body>
	
	<script>
		var browser_height = $(window).height();
		var browser_width = $(window).width();
		
		$('#main').css('width',browser_width).css('height',browser_height);
	</script>
</html>