package request;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 请求转发(mvc设计模式)
	在mvc设计模式下，都是servlet处理用户请求，并产生用户想看的数据，然后转交给jsp显示，转交给jsp显示时,
	会把数据存在request域里面带给jsp
*/
public class Request08Forward extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//方式1  ServletContext中的getRequestDispatcher
		//this.getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
		
		String data = "a";
		//方式2  request.getRequestDispatcher
		request.setAttribute("data", data);  //request是一个域对象(容器)，存属性
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
