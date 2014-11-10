package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.News;
import model.SQLHelper;

/**
 * 将新闻类的对象显示出来
 */
public class PrintNews extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public PrintNews() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SQLHelper sqlHelper = new SQLHelper();
		try {
			//从news中取出最新添加的News对象
			News news = sqlHelper.queryNearestNews("news" , "createtime");
			System.out.println(news.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
