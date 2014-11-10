<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="java.net.InetAddress" %>
<%@page import="java.util.Date" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
        <title></title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/style/admin/css/admin.css" />
    </head>
    <!--显示各种配置和用户信息。。。默认的欢迎界面-->
    <body>
        <table class="table">
            <tr>
                <td colspan='2' class="th"><span class="span_people">&nbsp</span>欢迎光临管理后台</td>
            </tr>
            <tr>
                <td>用户名</td>
                <td><%= session.getAttribute("userName") %></td>
            </tr>
            <tr>
                <td>登录时间</td>
                <td>
                <% Date date = new Date();
                   out.print(date.toLocaleString());
                %>
                </td>
            </tr>
            <tr>
                <td>客户端IP</td>
                <td><%= request.getRemoteAddr() %></td>
            </tr>
            <tr>
                <td colspan='2' class="th"><span class="span_server" style="float:left">&nbsp</span>服务器信息</td>
            </tr>
            <tr>
                <td>服务器环境</td>
                <td><%= request.getServerName() %></td>
            </tr>
            <tr>
                <td>JAVA版本</td>
                <td>java version 1.7.0_45</td>
            </tr>
            <tr>
                <td>服务器IP</td>
                <td><%= InetAddress.getLocalHost().toString() %></td>
            </tr>
            <tr>
                <td>数据库信息</td>
                <td>mysql</td>
            </tr>
        </table>
    </body>
</html>