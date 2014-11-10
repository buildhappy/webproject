package cn.itcast.web.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.domain.User;
import cn.itcast.exception.UserExistException;
import cn.itcast.service.impl.BusinessServiceImpl;
import cn.itcast.utils.WebUtils;
import cn.itcast.web.formbean.RegisterForm;
//处理注册请求
public class RegisterServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");//解决中文乱码
		
		//1.对提交表单的字段进行合法性校验（把表单数据封装到formbean包下的RegisterForm.java中）
		RegisterForm form =  WebUtils.request2Bean(request, RegisterForm.class);
		String server_checkcode = (String) request.getSession().getAttribute("checkcode");
		form.setServer_checkcode(server_checkcode);
		boolean b = form.validate();//先封装到RegisterForm中再对其进行校验
		
		//2.如果校验失败，跳回到表单页面，回显校验失败信息
		if(!b){
			request.setAttribute("form", form); //将校验后的数据返回给register.jsp页面  
			request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
			return;
		}
		
		//3.如果校验成功，则调用service处理注册请求
		User user = new User();
		WebUtils.copyBean(form, user);
		user.setId(WebUtils.generateID());
		
		BusinessServiceImpl service = new BusinessServiceImpl();
		try {
			service.register(user);
			//6.如果serivce处理成功，跳转到网站的全局消息显示页面，为用户注册成功的消息
			request.setAttribute("message", "恭喜您，注册成功！！");
			request.getRequestDispatcher("/message.jsp").forward(request, response);
			return;
		} catch (UserExistException e) {
			//4.如果serivce处理不成功,并且不成功的原因，是因为注册用户已存在的话，则跳回到注册页面，显示注册用户已存在的消息
			form.getErrors().put("username", "注册的用户名已存在！！");
			request.setAttribute("form", form);
			request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
			return;
		}catch (Exception e) {
			//5.如果serivce处理不成功,并且不成功的原因是其它问题的话，跳转到网站的全局消息显示页面，为用户显示友好错误消息
			e.printStackTrace();
			request.setAttribute("message", "服务器出现未知错误！！！");
			request.getRequestDispatcher("/message.jsp").forward(request, response);
			return;
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}