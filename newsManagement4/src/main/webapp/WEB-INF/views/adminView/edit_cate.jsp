<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <link rel="stylesheet" type="text/css" href="<?php echo base_url() . '/style/admin/css/public.css' ?>"/>
    <title>Document</title>
    <style type="text/css">
      span{
        color: #f00;
      }
    </style>
  </head>
  <body>
    <form action="<?php echo site_url('admin/category/edit') ?>" method="post">
      <table class="table">
        <tr>
          <td class="th" colspan="10">编辑栏目</td>
        </tr>
        <tr>
          <td>栏目名称</td>
          <td>
              <!--获取从category.php的edit_cate函数传递的数据，将栏目名称显示在输入框内-->
            <input type="text" name="cname" value="<?php echo $category[0]['cname'] ?>"/>
            <?php echo form_error('cname', '<span>', '</span>') ?><!--验证内容格式-->
          </td>
        </tr>
        <tr>
          <input type="hidden" name="cid" value="<?php echo $category[0]['cid'] ?>"/>  <!--隐藏表单,传递cid，便于修改-->
          <td colspan="10"><input type="submit" value="编辑" class="input_button"/></td>
        </tr>
      </table>
    </form>
  </body>
</html>