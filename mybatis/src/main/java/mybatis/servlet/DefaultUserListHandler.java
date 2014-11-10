package mybatis.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mybatis.entity.User;
import mybatis.impl.UserServiceImpl;
import mybatis.service.UserService;
import mybatis.utils.JsonUtil;

/**
 * Servlet implementation class DefaultUserListHandler
 */
public class DefaultUserListHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public DefaultUserListHandler() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		String returnResults = "";
		PrintWriter out = response.getWriter();
		UserService us = new UserServiceImpl();
		String method = "";
		method = request.getParameter("method");
		if(method.equals("users")){
			returnResults = "Users";
			for(User user:us.getUsers()){
				System.out.println(user.getId());//new JsonUtil<User>().getJsonByListObj(us.getUsers());
			}
		}else if(method.equals("user")){
			String id = request.getParameter("id");
			returnResults = new JsonUtil<User>().getJsonByEntity(us.getUser(id));
		}else {//(method.equals("update")){
			returnResults = new String("fafsadfs");
		}
		out.write(returnResults);
		out.flush();
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}
