package com.buildhappy.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.buildhappy.domain.User;
import com.buildhappy.service.UserService;

@Controller
@RequestMapping(value="/admin")
public class LoginController {
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/login.html")
	public String loginPage(@RequestParam("userName") String userName){
							//当输入的URL后面带有参数userName时将参数传递过来
		System.out.println(userName);
		System.out.println("userName");
		return "login";
	}
	
	@RequestMapping(value="/loginCheck.html")
	public ModelAndView loginCheck(HttpServletRequest request , LoginCommand loginCommand){
		boolean isValidUser = userService.hasMatchUser(loginCommand.getUserName(), loginCommand.getPassword());
		if(!isValidUser){
 			return new ModelAndView("login" , "error" , "用户名和密码错误");
		}else{
			User user = userService.findUserByName(loginCommand.getUserName());
			user.setLastVisit(new Date());
			userService.loginSuccess(user);
			request.getSession().setAttribute("user", user);
			return new ModelAndView("main");
		}
	}
}
