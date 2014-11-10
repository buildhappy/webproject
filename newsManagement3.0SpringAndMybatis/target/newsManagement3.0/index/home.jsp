<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>新闻管理系统</title>
<link href="<%=request.getContextPath()%>/resources/style/index/css/index.css" rel="stylesheet" /><!--引入css模板-->
<link href="<%=request.getContextPath()%>/resources/style/index/css/common.css" rel="stylesheet" />
<link href="<%=request.getContextPath()%>/resources/style/index/css/common.css" rel="stylesheet" />
</head>	
<body>
    <div id="top"></div>
    <div id="header">
    	<!-- 显示图标 -->
      <div class='logo'>
          <a href=""><img src="<%=request.getContextPath()%>/resources/style/index/images/logo.jpg" alt=""></a>
      </div>
     	<!-- 显示导航栏 -->
        <div class='navigation'>
            <a href="<%=request.getContextPath()%>">首页</a><!--.'index.html'-->

        
    <!-- 显示新闻主体部分 -->
    <div id="main">
    <div class='content'>
        <div  class='list'>
            <div class='title'>
				<h2>最新文章..</h2>
            </div>
            <ul>
                <?php foreach($art as $v): ?>
                <li>
                <div class='post-image'>
                    <span>
                        <a href="<?php echo site_url('index/home/article/'.$v['aid']) ?>"><img width="" src="<?php echo base_url().'uploads/'. $v['thumb']?>" /></a><!--缩略图-->
                    </span>	
                </div>	
                <div class='post-content'><!--标题-->
                    <a href="<?php echo site_url('index/home/article/'.$v['aid']) ?>"><h3><?php echo $v['title'] ?></h3></a>
                    <p><?php echo $v['info'] ?></p><!--摘要-->
                </div>
                </li>
                <?php endforeach ?>
            </ul>
	</div>
	<div class='list'>
            <div class='title'>
                <h2>热门文章..</h2>
            </div>
            <ul>
                <?php foreach($hot as $v): ?>
                    <li>
                    <div class='post-image'>
                    <span>
						<a href="<?php echo site_url('index/home/article/'.$v['aid']) ?>">
						<img width="" src="<?php echo base_url().'uploads/'. $v['thumb']?>" /></a>
                    </span>	
                    </div>	
                    <div class='post-content'>
                        <a href="<?php echo site_url('index/home/article/'.$v['aid']) ?>"><h3><?php echo $v['title'] ?></h3></a>
						<p><?php echo $v['info'] ?></p>
                    </div>
                    </li>
				<?php endforeach ?>
            </ul>
	</div>
    </div>
    <!-- 右侧新闻列表 -->
    <div class='sidebar'>
		<div class='item'>
			<h2>文章标题</h2>
				<ul class='flink'>
					<?php foreach ($title as $v): ?>
					<li>
                      <a href="<?php echo site_url('index/home/article/'.$v['aid']) ?>">
                        <?php echo $v['title'] ?>
                      </a>
                    </li>
					<?php endforeach ?>
				</ul>
		</div>			
	</div>
    </div>
 	<div id="footer">
		<div class='bgbar'></div>
		<div class='bottom'>
			<div class='pos'>
				<div class='copyright'>
					© Copyright 2011-2014 buildhappy
				</div>
			</div>	
		</div>
	</div>
</body>
</html>


	