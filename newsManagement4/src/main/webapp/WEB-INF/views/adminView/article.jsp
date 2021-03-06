<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<!-- 发表文章页面 -->
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <link rel="stylesheet" type="text/css" href="<?php echo base_url() . 'style/admin/css/public.css' ?>"/>
    <script type="text/javascript" src="<?php echo base_url() ?>org/ueditor/ueditor.all.min.js"></script>
    <script type="text/javascript">
            window.UEDITOR_HOME_URL = "<?php echo base_url() ?>org/ueditor/";
            window.onload = function(){
                    window.UEDITOR_CONFIG.initialFrameWidth = 900;
                    window.UEDITOR_CONFIG.initialFrameHeight = 600;
            UE.getEditor('content');
        }
    </script>
    <script type="text/javascript" src="<?php echo base_url() ?>org/ueditor/ueditor.config.js"></script>
    <title>Document</title>
    <style type="text/css">
            span{
                    color: #f00;
            }
    </style>
</head>
<body>
    <form action="<?php echo site_url('admin/article/send') ?>" method="post" enctype="multipart/form-data"><!--有上传文件必须用enctype-->
    <table class="table">
	<tr >
            <td class="th" colspan="10">发表文章</td>
	</tr>
	<tr>
		<td>标题</td>
		<td><input type="text" name="title" value="<?php echo set_value('title') ?>"/><!--set_value表示提交后即使格式错误，数据仍保存-->
                    <?php echo form_error('title', '<span>', '</span>') ?><!--在同一行显示错误提示-->
		</td>
	</tr>
	<tr>
        <td>类型</td>
        <td>
           <!-- <input type="radio" name="type" value="0"<?php echo set_radio('type', '0', TRUE) ?>/> 普通         true意思为：默认选中-->
          <!--  <input type="radio" name="type" value="1"<?php echo set_radio('type', '1') ?>/> 热门    -->
        </td>
	</tr>
	<tr>
        <td>栏目</td>
        <td>
            <select name="cid" id="">
                <?php foreach($category as $v): ?>
              <!--  <option value="<?php echo $v['cid'] ?>"<?php echo set_select('cid', $v['cid']) ?>><?php echo $v['cname'] ?></option>   -->
                  <!--
                    <option value="1"  php echo set_select('cid , ‘1’ , TRUE) >情感</option> -->
                  <!--
                    <option value="2"  php echo set_select('cid , ‘2’) >生活</option>   -->
                <?php endforeach ?>
            </select>
        </td>
	</tr>
	<tr>
      <td>缩略图</td>
      <td>
        <input type="file" name="thumb"/>
      </td>
	</tr>
	<tr>
      <td>摘要</td>
        <td>
          <textarea name="info" id="" style="width:550px;height:160px;"><?php echo set_value('info') ?></textarea>
                <?php echo form_error('info', '<span>', '</span>') ?>
                <!--在同一行显示提示消息,并保持原来的内容-->
          </td>
	</tr>
	<tr>
      <td>内容</td>
      <td>
        <textarea name="content" id="content" style="width:550px;height:500px;"><?php echo set_value('content') ?></textarea>
        <?php echo form_error('content', '<span>', '</span>') ?>
      </td>
	</tr>
	<tr>
      <td colspan="10"><input type="submit" class="input_button" value="发布"/></td>
	</tr>
	</table>
	</form>
</body>
</html>