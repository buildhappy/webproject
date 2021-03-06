/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat/7.0.30
 * Generated at: 2014-08-09 00:54:50 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp.adminView;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Date;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
        throws java.io.IOException, javax.servlet.ServletException {

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html; charset=utf-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write(" \r\n");
      out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n");
      out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">\r\n");
      out.write("<!-- the index page after logging in -->\r\n");
      out.write("  <head>\r\n");
      out.write("    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\" />\r\n");
      out.write("    <title>新闻管理系统</title>\r\n");
      out.write("    <link rel=\"stylesheet\" href=\"");
      out.print(request.getContextPath());
      out.write("/resources/style/admin/css/admin.css\" />\r\n");
      out.write("    <script type=\"text/javascript\" src=\"");
      out.print(request.getContextPath());
      out.write("/resources/style/admin/js/jquery-1.7.2.min.js\"></script>\r\n");
      out.write("    <script type=\"text/javascript\" src=\"");
      out.print(request.getContextPath());
      out.write("/resources/style/admin/js/admin.js\"></script>\r\n");
      out.write("    <!-- 默认打开目标 -->\r\n");
      out.write("    <base target=\"iframe\"/>\r\n");
      out.write("  </head>\r\n");
      out.write("  <body>\r\n");
      out.write("    <!-- 头部 -->\r\n");
      out.write("    <div id=\"top_box\">\r\n");
      out.write("      <div id=\"top\">\r\n");
      out.write("        <p id=\"top_font\">新闻管理系统首页 （V1.1）</p>\r\n");
      out.write("      </div>\r\n");
      out.write("      <div class=\"top_bar\">\r\n");
      out.write("        <p class=\"adm\">\r\n");
      out.write("          <span>管理员：</span>\r\n");
      out.write("          <span class=\"adm_pic\">&nbsp&nbsp&nbsp&nbsp</span>\r\n");
      out.write("          <span class=\"adm_people\">[");
      out.print( session.getAttribute("userName"));
      out.write("]</span>\r\n");
      out.write("        </p>\r\n");
      out.write("        <p class=\"now_time\">\r\n");
      out.write("          时间:\r\n");
      out.write("          ");
 Date date=new Date();
          	out.print(date.toLocaleString());
          
      out.write("\r\n");
      out.write("          当前位置:\r\n");
      out.write("          <span>首页</span>\r\n");
      out.write("        </p>\r\n");
      out.write("        <p class=\"out\">\r\n");
      out.write("          <span class=\"out_bg\">&nbsp&nbsp&nbsp&nbsp</span>&nbsp\r\n");
      out.write("          <a href=\"<?php echo site_url('admin/login/login_out') ?>\" target=\"_self\">退出</a>\r\n");
      out.write("        </p>\r\n");
      out.write("      </div>\r\n");
      out.write("    </div>\r\n");
      out.write("    <!-- 左侧菜单 -->\r\n");
      out.write("    <div id=\"left_box\">\r\n");
      out.write("      <p class=\"use\">功能管理</p>\r\n");
      out.write("      <div class=\"menu_box\">\r\n");
      out.write("        <h2>新闻管理</h2>\r\n");
      out.write("        <div class=\"text\">\r\n");
      out.write("          <ul class=\"con\">\r\n");
      out.write("            <li class=\"nav_u\">\r\n");
      out.write("              <a href=\"");
      out.print( request.getContextPath() );
      out.write("/adminView/editNews.jsp\" class=\"pos\">发表新闻</a>\t\t\t\t       \r\n");
      out.write("            </li>\r\n");
      out.write("          </ul>\r\n");
      out.write("          <ul class=\"con\">\r\n");
      out.write("            <li class=\"nav_u\">\r\n");
      out.write("              <a href=\"");
      out.print( request.getContextPath() );
      out.write("/adminView/checkNews.jsp\" class=\"pos\">查看新闻</a>\t\t\t\t        \t\r\n");
      out.write("            </li>\r\n");
      out.write("          </ul>\r\n");
      out.write("        </div>\r\n");
      out.write("      </div>\t\r\n");
      out.write("      <div class=\"menu_box\">\r\n");
      out.write("        <h2>栏目管理</h2>\r\n");
      out.write("        <div class=\"text\">\r\n");
      out.write("          <ul class=\"con\">\r\n");
      out.write("            <li class=\"nav_u\">\r\n");
      out.write("              <a href=\"<?php echo site_url('admin/category/index') ?>\" class=\"pos\">查看栏目</a>\t\t\t\t        \t\r\n");
      out.write("            </li> \r\n");
      out.write("          </ul>\r\n");
      out.write("          <ul class=\"con\">\r\n");
      out.write("            <li class=\"nav_u\">\r\n");
      out.write("              <a href=\"<?php echo site_url('admin/category/add_cate') ?>\" class=\"pos\">添加栏目</a>\t\t\t\t        \t\r\n");
      out.write("            </li> \r\n");
      out.write("          </ul>\r\n");
      out.write("        </div>\r\n");
      out.write("      </div>\t\r\n");
      out.write("      <div class=\"menu_box\">\r\n");
      out.write("        <h2>常用菜单</h2>\r\n");
      out.write("        <div class=\"text\">\r\n");
      out.write("          <ul class=\"con\">\r\n");
      out.write("            <li class=\"nav_u\">\r\n");
      out.write("              <a href=\"");
      out.print(request.getContextPath() );
      out.write("/index/newsMagazine.jsp\" class=\"pos\" target=\"_blank\">前台首页</a>\t\t\t        \t\r\n");
      out.write("            </li> \r\n");
      out.write("          </ul>\r\n");
      out.write("          <ul class=\"con\">\r\n");
      out.write("            <li class=\"nav_u\">\r\n");
      out.write("              <a href=\"");
      out.print(request.getContextPath() );
      out.write("/adminView/copy.jsp\" class=\"pos\">系统信息</a>\t\t\t\t        \t\r\n");
      out.write("            </li> \r\n");
      out.write("          </ul>\r\n");
      out.write("          <ul class=\"con\">\r\n");
      out.write("            <li class=\"nav_u\">\r\n");
      out.write("              <a href=\"<?php echo site_url('admin/admin/change') ?>\" class=\"pos\">密码修改</a>\t\t\t\t        \t\r\n");
      out.write("            </li> \r\n");
      out.write("          </ul>\r\n");
      out.write("        </div>\r\n");
      out.write("      </div>\t\t\t\r\n");
      out.write("    </div>\r\n");
      out.write("    <!-- 右侧 -->\r\n");
      out.write("    <div id=\"right\">\r\n");
      out.write("      <iframe  frameboder=\"0\" border=\"0\" scrolling=\"yes\" name=\"iframe\" src=\"<\r\n");
      out.write("               ?php echo site_url().'/admin/admin/copy' ?>\"></iframe><!--传递给controllers/admin/admin.php/copy方法-->\r\n");
      out.write("    </div>\r\n");
      out.write("    <!-- 底部 -->\r\n");
      out.write("    <div id=\"foot_box\">\r\n");
      out.write("      <div class=\"foot\">\r\n");
      out.write("        <p>@Copyright © 2013-2013 buildhappy.com All Rights Reserved. 京ICP备0000000号</p>\r\n");
      out.write("      </div>\r\n");
      out.write("    </div>\r\n");
      out.write("  </body>\r\n");
      out.write("</html>\r\n");
      out.write("<!--[if IE 6]>\r\n");
      out.write("    <script type=\"text/javascript\" src=\"<?php echo base_url().'style/admin/' ?>js/iepng.js\"></script>\r\n");
      out.write("    <script type=\"text/javascript\">\r\n");
      out.write("        DD_belatedPNG.fix('.adm_pic, #left_box .pos, .span_server, .span_people', 'background');\r\n");
      out.write("    </script>\r\n");
      out.write("<![endif]-->\r\n");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
