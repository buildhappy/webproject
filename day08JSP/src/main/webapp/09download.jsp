<%@ page language="java" import="java.io.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = application.getRealPath("/downloads/1.jpg");
	out.println(path);
	String fileName = path.substring(path.lastIndexOf("\\") + 1);
	response.setHeader("content-disposition", "attachment;filename=" + fileName);
	//通知浏览器以下载的方式打开
	FileInputStream in = new FileInputStream(path);
	int len = 0;
	byte buffer[] = new byte[1024];
	while ((len = in.read(buffer)) > 0) {
		response.getOutputStream().write(buffer, 0, len);
		//注意同时调用字节流(getOutputStream)和字符流(getWriter)，会出错，二者不能同时存在，要删除jsp中的空余部分。
	}
	in.close();
%>