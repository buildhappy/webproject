package controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.News;
import net.iharder.Base64;

/**
 * 接受jsp文件发送的新闻数据，并存储到数据库中
 */
public class AdminService extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public AdminService() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String newsContent = request.getParameter("editorValue");//.getParameter("newsContent");
		System.out.println("get" + newsContent);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		
		String author = null;
		String type = null;
		String newsTitle = null;
		String newsContent = null;
		Date date = new Date();
		
		//从editNews.jsp中获取数据
		author = request.getSession().getAttribute("author").toString();
		type = request.getParameter("type");
		newsTitle = request.getParameter("title");//获取新闻标题
		newsContent = Base64.encodeBytes(request.getParameter("editorValue").getBytes());//获取内容
		
		//构建News对象
		News news = new News(0, author, type , newsTitle , newsContent , date.toLocaleString());
		try {
			//System.out.println(news.pushDB());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
