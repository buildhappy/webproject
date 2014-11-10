package response;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RequestDispatcher的include方法
 * @author buildhappy
 *
 */
public class Response01OutputStream extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//将head.html的内容包含进来
		request.getRequestDispatcher("/public/head.html").include(request, response);
		
		//用getWriter会报错
		//! response.getWriter().write("hahah");
		response.getOutputStream().write("hahahah".getBytes());
		
		request.getRequestDispatcher("/public/footer.html").include(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
