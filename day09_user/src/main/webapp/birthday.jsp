<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="${pageContext.request.contextPath }/css/jquery-ui.css">
<script src="${pageContext.request.contextPath }/js/jquery-1.11.1.js"></script>
<script src="${pageContext.request.contextPath }/js/jquery-ui.js"></script>
<title>演示jquery的日期下拉框怎样用</title>
<script>
	$(function() {
		$( "#datepicker" ).datepicker();//$("#datepicker")表示引用id=datepicker的对象
		$( "#format" ).change(function() {
			$( "#datepicker" ).datepicker( "option", "dateFormat", $( this ).val() );
		});
	});
</script>
</head>
<body>
	<h1>jquery的日期下拉框引用方法</h1>
	<br/>
	<p>Date: <input type="text" id="datepicker"></p>
	<p>Format options:<br>
		<select id="format">
		<option value="mm/dd/yy">Default - mm/dd/yy</option>
		<option value="yy-mm-dd">ISO 8601 - yy-mm-dd</option>
		<option value="d M, y">Short - d M, y</option>
		<option value="d MM, y">Medium - d MM, y</option>
		<option value="DD, d MM, yy">Full - DD, d MM, yy</option>
		<option value="&apos;day&apos; d &apos;of&apos; MM &apos;in the year&apos; yy">With text - 'day' d 'of' MM 'in the year' yy</option>
		</select>
	</p>
	
	<br/>
</body>
</html>