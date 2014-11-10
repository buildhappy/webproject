<%@page import="org.springframework.context.support.FileSystemXmlApplicationContext"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="com.buildhappy.domain.News"%>
<%@page import="com.buildhappy.service.NewsService"%>
<%@page import="java.util.Set" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.List" %>
<%@page import="java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<!-- 新闻显示的首页 -->
<head>
<title>News Magazine</title>
<%
	request.setCharacterEncoding("utf-8");
	response.setContentType("text/html;charset=utf-8");
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="imagetoolbar" content="no" />
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/style/index/newsMagazine/styles/layout.css" type="text/css" />
<script type="text/javascript" src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/scripts/jquery-1.4.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/scripts/jquery.easing.1.3.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/scripts/jquery.timers.1.2.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/scripts/jquery.galleryview.2.1.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/scripts/jquery.galleryview.setup.js"></script>
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
<!-- ##############################  新闻主体显示部分  ############################################################ -->
<div class="wrapper">
  <div class="container">
  	<!-- 显示滚动图片  -->
    <div class="content">
      <div id="featured_slide">
        <ul id="featurednews">
          <li>
          	<img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/1.jpg" alt="" />
            <div class="panel-overlay">
              <h2>Nullamlacus dui ipsum</h2>
              <p>Temperinte interdum sempus odio urna eget curabitur semper convallis nunc laoreet.<br />
                <a href="#">Continue Reading &raquo;</a>
              </p>
            </div>
          </li>
          <li>
          	<img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/2.jpg" alt="" />
            <div class="panel-overlay">
              <h2>Aliquatjusto quisque nam</h2>
              <p>Temperinte interdum sempus odio urna eget curabitur semper convallis nunc laoreet.<br />
                <a href="#">Continue Reading &raquo;</a>
              </p>
            </div>
          </li>
          <li>
          	<img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/3.jpg" alt="" />
            <div class="panel-overlay">
              <h2>Dapiensociis temper donec</h2>
              <p>Temperinte interdum sempus odio urna eget curabitur semper convallis nunc laoreet.<br />
                <a href="#">Continue Reading &raquo;</a></p>
            </div>
          </li>
          <li><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/4.jpg" alt="" />
            <div class="panel-overlay">
              <h2>Semvelit nonummy odio tempus</h2>
              <p>Justolacus eger at pede felit in dictum sempus elit curabitur ipsum. Ametpellentum.<br />
                <a href="#">Continue Reading &raquo;</a></p>
            </div>
          </li>
          <li>
          	<img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/5.jpg" alt="" />
            <div class="panel-overlay">
              <h2>Pedefamet orci orci quisque</h2>
              <p>Nonnam aenenasce morbi liberos malesuada risus id donec volutpat estibulum curabitae.<br />
                <a href="#">Continue Reading &raquo;</a>
              </p>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <!-- div content end(滚动图片结束) -->
    
    <!-- 右侧新闻概况栏 -->
    <div class="column">
      <ul class="latestnews">
      	<%
      		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:web-app.xml");
 			NewsService newsService= ctx.getBean("newsService" , NewsService.class);
     		List<News> newses;
     		newses = newsService.selectTop3LatestNewsOfAll();
     		Iterator it = newses.iterator();
     		News news;
     		String url;
     		//for(News news : newses){
     		while(it.hasNext()){
     			news = (News)it.next();
     			url = request.getContextPath() + "/index/checkTheNews.jsp?newsId=" + news.getId();
     			out.print("<li><img src='" + request.getContextPath() + "/resources/style/index/newsMagazine/images/6.jpg' alt='' />");
     			out.print("<p>");
     				//print the title 
     			out.print("<strong><a href='"  + url  + "'>" + news.getTitle() + "</a></strong> </br>");
     				//print the content
     			out.print(news.getContent());
     			out.print("</p>");
     			out.print("</li>");
     		}
      	%>
      	<!--  
        <li><img src="/resources/style/index/newsMagazine/images/6.jpg" alt="" />
          <p>
          	<strong><a href="#">Indonectetus facilis leo.</a></strong> 
          	Nullamlacus dui ipsum cons eque loborttis non euis que morbi penas dapibulum orna. Urnaultrices quis curabitur phasellentesque.
          </p>
        </li>
        -->
      </ul>
    </div>
    <br class="clear" />
  </div>
</div>
<!-- ##############################  分类显示各类新闻   ############################################################ -->
<div class="wrapper">
  <!-- ????unknown -->
  <div id="adblock">
    <div class="fl_left"><a href="#"><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/demo/468x60.gif" alt="" /></a></div>
    <div class="fl_right"><a href="#"><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/demo/468x60.gif" alt="" /></a></div>
    <br class="clear" />
  </div>
  
  <!-- 分类显示各种新闻 -->
  <div id="hpage_cats">
    <div class="fl_left">
    <%
  		news = newsService.selectLatestNewsOfTheType("society");
    	url = request.getContextPath() + "/index/checkTheNews.jsp?newsId=" + news.getId();
  	%>
      <h2><a href="#">社会 &raquo;</a></h2>
      <img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/9.jpg" alt="" />
      <p><strong><a href="<%=url%>"><%out.print(news.getTitle()); %></a></strong></p>
      <p><%out.print(news.getContent()); %></p>
    </div>
    <div class="fl_right">
    	<%
  			news = newsService.selectLatestNewsOfTheType("technology");
    		url = request.getContextPath() + "/index/checkTheNews.jsp?newsId=" + news.getId();
  		%>
      <h2><a href="#">科技 &raquo;</a></h2>
      <img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/9.jpg" alt="" />
      <p><strong><a href="<%=url%>"><%out.print(news.getTitle()); %></a></strong></p>
      <p><%out.print(news.getContent()); %></p>
    </div>
    <br class="clear" />
    <div class="fl_left">
      <h2><a href="#">体育  &raquo;</a></h2>
      <img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/11.jpg" alt="" />
      <p><strong><a href="#">Indonectetus facilis leo.</a></strong></p>
      <p>Morbitincidunt maurisque eros molest nunc anteget sed vel lacus mus semper. Anterdumnullam interdum eros dui urna consequam ac nisl nullam ligula vestassa. Condimentumfelis et amet tellent quisquet a leo lacus nec augue accumsan sagittislaorem dolor sum at urna.</p>
    </div>
    <div class="fl_right">
      <h2><a href="#">军事 &raquo;</a></h2>
      <img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/12.jpg" alt="" />
      <p><strong><a href="#">Indonectetus facilis leo.</a></strong></p>
      <p>Morbitincidunt maurisque eros molest nunc anteget sed vel lacus mus semper. Anterdumnullam interdum eros dui urna consequam ac nisl nullam ligula vestassa. Condimentumfelis et amet tellent quisquet a leo lacus nec augue accumsan sagittislaorem dolor sum at urna.</p>
    </div>
    <br class="clear" />
  </div>
</div>
<!-- ####################################################################################################### -->
<div class="wrapper">
  <div class="container">
    <div class="content">
      <div id="hpage_latest">
        <h2>Feugiatrutrum rhoncus semper</h2>
        <ul>
          <li><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/13.jpg" alt="" />
            <p>Nullamlacus dui ipsum conseqlo borttis non euisque morbipen a sdapibulum orna.</p>
            <p>Urnau ltrices quis curabitur pha sellent esque congue magnisve stib ulum quismodo nulla et.</p>
            <p class="readmore"><a href="#">Continue Reading &raquo;</a></p>
          </li>
          <li><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/14.jpg" alt="" />
            <p>Nullamlacus dui ipsum conseqlo borttis non euisque morbipen a sdapibulum orna.</p>
            <p>Urnau ltrices quis curabitur pha sellent esque congue magnisve stib ulum quismodo nulla et.</p>
            <p class="readmore"><a href="#">Continue Reading &raquo;</a></p>
          </li>
          <li class="last"><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/15.jpg" alt="" />
            <p>Nullamlacus dui ipsum conseqlo borttis non euisque morbipen a sdapibulum orna.</p>
            <p>Urnau ltrices quis curabitur pha sellent esque congue magnisve stib ulum quismodo nulla et.</p>
            <p class="readmore"><a href="#">Continue Reading &raquo;</a></p>
          </li>
        </ul>
        <br class="clear" />
      </div>
    </div>
    <div class="column">
      <div class="holder"><a href="#"><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/demo/300x250.gif" alt="" /></a></div>
      <div class="holder"><a href="#"><img src="<%=request.getContextPath() %>/resources/style/index/newsMagazine/images/demo/300x80.gif" alt="" /></a></div>
    </div>
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