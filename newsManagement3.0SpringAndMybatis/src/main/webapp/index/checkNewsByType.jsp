<%@page import="org.springframework.context.support.FileSystemXmlApplicationContext"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="com.buildhappy.domain.News"%>
<%@page import="com.buildhappy.service.NewsService"%>
<%@page import="java.util.Set" %>
<%@page import="java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
 <%@page import="net.iharder.Base64" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<!-- 点击导航栏的不同标签显示对应类型的新闻 -->
<head>
<title>查看新闻</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="imagetoolbar" content="no" />
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/style/index/newsMagazine/styles/layout.css" type="text/css" />
<%
	request.setCharacterEncoding("utf-8");
	response.setContentType("text/html;charset=utf-8");
%>
</head>
<body id="top">
<div class="wrapper col0">
  <div id="topline">
    <p>Tel: 010-8866886 | Mail: buildhappy@bupt.edu.cn</p>
    <ul>
      <li><a href="#">注册</a></li>
      <li><a href="#">登录</a></li>
      <li><a href="#">退出</a></li>
      <li class="last"><a href="#">Suspendisse</a></li>
    </ul>
    <br class="clear" />
  </div>
</div>
<!-- ################################################################################### -->
<div class="wrapper">
  <div id="header">
    <div class="fl_left">
      <h1><a href="#"><strong>N</strong>ews <strong>M</strong>agazine</a></h1>
      <p>buildhappy create</p>
    </div>
    <div class="fl_right"><a href="#"><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/demo/468x60.gif" alt="" /></a></div>
    <br class="clear" />
  </div>
</div>
<!-- ##############################  导航栏   ############################################################### -->
<div class="wrapper col2">
  <div id="topbar">
    <div id="topnav">
      <ul>
        <li class="active"><a href="<%=request.getContextPath() %>/index/newsMagazine.jsp">首页</a></li>
        <li><a href="./checkNewsByType.jsp?type=society">社会</a></li>
        <li><a href="">科技</a></li>
        <li><a href="#">军事</a>
        <li><a href="#">体育</a>
         <li><a href="#">更多..</a>
          <ul>
            <li><a href="#">Link 1</a></li>
            <li><a href="#">Link 2</a></li>
            <li><a href="#">Link 3</a></li>
          </ul>
        </li>
        <!--  <li class="last"><a href="#">A Long Link Text</a></li>-->
      </ul>
    </div>
    <div id="search">
      <form action="#" method="post">
        <fieldset>
          <legend>Site Search</legend>
          <input type="text" value="Search Our Website&hellip;"  onfocus="this.value=(this.value=='Search Our Website&hellip;')? '' : this.value ;" />
          <input type="submit" name="go" id="go" value="Search" />
        </fieldset>
      </form>
    </div>
    <br class="clear" />
  </div>
</div>

<!-- ############################## 显示当前位置   ############################################################ -->
<div class="wrapper">
  <div id="breadcrumb">
    <ul>
      <li class="first">You Are Here</li>
      <li>&#187;</li>
      <li><a href="#">Home</a></li>
      <li>&#187;</li>
      <li><a href="#">Grand Parent</a></li>
      <li>&#187;</li>
      <li><a href="#">Parent</a></li>
      <li>&#187;</li>
      <li class="current"><a href="#"><%=request.getParameter("type") %></a></li>
    </ul>
  </div>
</div>
<!-- ############################  显示新闻内容  ############################################################### -->
<div class="wrapper">
  <div class="container">
  	<%
  		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:web-app.xml");
 		NewsService newsService= ctx.getBean("newsService" , NewsService.class);
     	Set<News> newses;
     	//newses = newsService.selectNewsByType(request.getParameter("type"));
     	newses = newsService.selectAllNews();
     	for(News news:newses){
     		String url = request.getContextPath() + "/index/checkTheNews.jsp?newsId=" + news.getId();
     		out.print("<table>");
     		out.print("<tr><td><li><h1><a href=" + url + ">" + news.getTitle() + "</h1></td></tr><br/>");
     		out.print("<tr><td>" + news.getContent() + "</td></tr>");
     		out.print("<tr align='right'><td>" + news.getCreateTime().toLocaleString() + "</td></tr>");
     		out.print("</table>");
     	}
	%>
	</table>
	</div>
<!-- ####################################################################################################### --><div class="wrapper">
  <div id="adblock">
    <div class="fl_left"><a href="#"><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/demo/468x60.gif" alt="" /></a></div>
    <div class="fl_right"><a href="#"><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/demo/468x60.gif" alt="" /></a></div>
    <br class="clear" />
  </div>
</div>

<!-- ############################### 广告(宣传)区     ########################################################### -->
<div class="wrapper">
  <div id="footer">
    <div class="footbox">
      <h2>Lacus interdum</h2>
      <ul>
        <li><a href="#">Lorem ipsum dolor</a></li>
        <li><a href="#">Suspendisse in neque</a></li>
        <li><a href="#">Praesent et eros</a></li>
        <li><a href="#">Lorem ipsum dolor</a></li>
        <li><a href="#">Suspendisse in neque</a></li>
        <li class="last"><a href="#">Praesent et eros</a></li>
      </ul>
    </div>
    <div class="footbox">
      <h2>Lacus interdum</h2>
      <ul>
        <li><a href="#">Lorem ipsum dolor</a></li>
        <li><a href="#">Suspendisse in neque</a></li>
        <li><a href="#">Praesent et eros</a></li>
        <li><a href="#">Lorem ipsum dolor</a></li>
        <li><a href="#">Suspendisse in neque</a></li>
        <li class="last"><a href="#">Praesent et eros</a></li>
      </ul>
    </div>
    <div class="footbox">
      <h2>Lacus interdum</h2>
      <ul>
        <li><a href="#">Lorem ipsum dolor</a></li>
        <li><a href="#">Suspendisse in neque</a></li>
        <li><a href="#">Praesent et eros</a></li>
        <li><a href="#">Lorem ipsum dolor</a></li>
        <li><a href="#">Suspendisse in neque</a></li>
        <li class="last"><a href="#">Praesent et eros</a></li>
      </ul>
    </div>
    <div class="footbox">
      <h2>Lacus interdum</h2>
      <ul>
        <li><a href="#">Lorem ipsum dolor</a></li>
        <li><a href="#">Suspendisse in neque</a></li>
        <li><a href="#">Praesent et eros</a></li>
        <li><a href="#">Lorem ipsum dolor</a></li>
        <li><a href="#">Suspendisse in neque</a></li>
        <li class="last"><a href="#">Praesent et eros</a></li>
      </ul>
    </div>
    <div class="footbox last">
      <h2>Lacus interdum</h2>
      <ul>
        <li><a href="#">Lorem ipsum dolor</a></li>
        <li><a href="#">Suspendisse in neque</a></li>
        <li><a href="#">Praesent et eros</a></li>
        <li><a href="#">Lorem ipsum dolor</a></li>
        <li><a href="#">Suspendisse in neque</a></li>
        <li class="last"><a href="#">Praesent et eros</a></li>
      </ul>
    </div>
    <br class="clear" />
  </div>
</div>
<!-- ############################### 页末友情链接处    ############################################################## -->
<div class="wrapper">
  <div id="socialise">
    <ul>
      <li><a href="#"><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/facebook.gif" alt="" /><span>Facebook</span></a></li>
      <li><a href="#"><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/rss.gif" alt="" /><span>FeedBurner</span></a></li>
      <li class="last"><a href="#"><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/twitter.gif" alt="" /><span>Twitter</span></a></li>
    </ul>
    <div id="newsletter">
      <h2>NewsLetter Sign Up !</h2>
      <p>Please enter your Email and Name to join.</p>
      <form action="#" method="post">
        <fieldset>
          <legend>Digital Newsletter</legend>
          <div class="fl_left">
            <input type="text" value="Enter name here&hellip;"  onfocus="this.value=(this.value=='Enter name here&hellip;')? '' : this.value ;" />
            <input type="text" value="Enter email address&hellip;"  onfocus="this.value=(this.value=='Enter email address&hellip;')? '' : this.value ;" />
          </div>
          <div class="fl_right">
            <input type="submit" name="newsletter_go" id="newsletter_go" value="&raquo;" />
          </div>
        </fieldset>
      </form>
      <p>To unsubsribe please <a href="#">click here &raquo;</a>.</p>
    </div>
    <br class="clear" />
  </div>
</div>
<!-- ####################################################################################################### -->
<div class="wrapper col8">
  <div id="copyright">
    <p class="fl_left">Copyright &copy; 2011 - All Rights Reserved - <a href="#">Domain Name</a></p>
    <p class="fl_right">Template from <a href="http://www.cssmoban.com/" title="模板之家">网站模板</a></p>
    <br class="clear" />
  </div>
</div>
</body>
</html>