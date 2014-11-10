package itcast;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import itcast.dao.StudentDao;
/**
 * 在普通java程序中如何读取资源文件的数据，配合itcast.dao中的studentDao.java文件
 * @author buildhappy
 *
 */
public class Servlet12 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//调用dao从数据库中获取数据
		StudentDao dao = new StudentDao();
		String student = dao.get();
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
