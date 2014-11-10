<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
 <%@page import="java.util.Date" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<!-- the index page after logging in -->
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
    <title>新闻管理系统</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/style/admin/css/admin.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/style/admin/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/style/admin/js/admin.js"></script>
    <!-- 默认打开目标 -->
    <base target="iframe"/>
  </head>
  <body>
    <!-- 头部 -->
    <div id="top_box">
      <div id="top">
        <p id="top_font">新闻管理系统首页 （V1.1）</p>
      </div>
      <div class="top_bar">
        <p class="adm">
          <span>管理员：</span>
          <span class="adm_pic">&nbsp&nbsp&nbsp&nbsp</span>
          <span class="adm_people">[<%= session.getAttribute("userName")%>]</span>
        </p>
        <p class="now_time">
          时间:
          <% Date date=new Date();
          	out.print(date.toLocaleString());
          %>
          当前位置:
          <span>首页</span>
        </p>
        <p class="out">
          <span class="out_bg">&nbsp&nbsp&nbsp&nbsp</span>&nbsp
          <a href="<?php echo site_url('admin/login/login_out') ?>" target="_self">退出</a>
        </p>
      </div>
    </div>
    <!-- 左侧菜单 -->
    <div id="left_box">
      <p class="use">功能管理</p>
      <div class="menu_box">
        <h2>新闻管理</h2>
        <div class="text">
          <ul class="con">
            <li class="nav_u">
              <a href="<%= request.getContextPath() %>/adminView/editNews.jsp" class="pos">发表新闻</a>				       
            </li>
          </ul>
          <ul class="con">
            <li class="nav_u">
              <a href="<%= request.getContextPath() %>/adminView/checkNews.jsp" class="pos">查看新闻</a>				        	
            </li>
          </ul>
        </div>
      </div>	
      <div class="menu_box">
        <h2>栏目管理</h2>
        <div class="text">
          <ul class="con">
            <li class="nav_u">
              <a href="<?php echo site_url('admin/category/index') ?>" class="pos">查看栏目</a>				        	
            </li> 
          </ul>
          <ul class="con">
            <li class="nav_u">
              <a href="<?php echo site_url('admin/category/add_cate') ?>" class="pos">添加栏目</a>				        	
            </li> 
          </ul>
        </div>
      </div>	
      <div class="menu_box">
        <h2>常用菜单</h2>
        <div class="text">
          <ul class="con">
            <li class="nav_u">
              <a href="<%=request.getContextPath() %>/index/newsMagazine.jsp" class="pos" target="_blank">前台首页</a>			        	
            </li> 
          </ul>
          <ul class="con">
            <li class="nav_u">
              <a href="<%=request.getContextPath() %>/adminView/copy.jsp" class="pos">系统信息</a>				        	
            </li> 
          </ul>
          <ul class="con">
            <li class="nav_u">
              <a href="<?php echo site_url('admin/admin/change') ?>" class="pos">密码修改</a>				        	
            </li> 
          </ul>
        </div>
      </div>			
    </div>
    <!-- 右侧 -->
    <div id="right">
      <iframe  frameboder="0" border="0" scrolling="yes" name="iframe" src="<
               ?php echo site_url().'/admin/admin/copy' ?>"></iframe><!--传递给controllers/admin/admin.php/copy方法-->
    </div>
    <!-- 底部 -->
    <div id="foot_box">
      <div class="foot">
        <p>@Copyright © 2013-2013 buildhappy.com All Rights Reserved. 京ICP备0000000号</p>
      </div>
    </div>
  </body>
</html>
<!--[if IE 6]>
    <script type="text/javascript" src="<?php echo base_url().'style/admin/' ?>js/iepng.js"></script>
    <script type="text/javascript">
        DD_belatedPNG.fix('.adm_pic, #left_box .pos, .span_server, .span_people', 'background');
    </script>
<![endif]-->
