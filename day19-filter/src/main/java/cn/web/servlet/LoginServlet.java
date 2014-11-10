package cn.web.servlet;

import java.io.IOException;
import java.security.MessageDigest;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Encoder;

import cn.domain.User;
import cn.service.BusinessService;

public class LoginServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		BusinessService service = new BusinessService();
		User user = service.login(username, password);
		if(user==null){
			request.setAttribute("message", "用户名或密码错误！！");
			request.getRequestDispatcher("/message.jsp").forward(request, response);
			return;
		}
		
		request.getSession().setAttribute("user", user);
		int expirestime = Integer.parseInt(request.getParameter("time"));
		//给客户机发送自动登陆的 cookie  
		//autologin=username:expirestime:md5(password:expirestime:username)
		Cookie cookie = makeCookie(user, expirestime);
		response.addCookie(cookie);
		response.sendRedirect("/day19/index.jsp");
	}
	
	public Cookie makeCookie(User user,int expirestime){
		long currenttime = System.currentTimeMillis();
		String cookieValue = user.getUsername() + ":" + (currenttime+expirestime*1000) + ":" + md5(user.getUsername(), user.getPassword(), (currenttime+expirestime*1000));
		Cookie cookie = new Cookie("autologin",cookieValue);
		cookie.setMaxAge(expirestime);
		cookie.setPath("/day19");
		return cookie;
	}
	
	private String md5(String username,String password,long expirestime){
		
		try{
			String value = password + ":" + expirestime + ":" + username;
			MessageDigest md = MessageDigest.getInstance("md5");
			byte md5[] = md.digest(value.getBytes());
			BASE64Encoder encode = new BASE64Encoder();
			return encode.encode(md5);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
