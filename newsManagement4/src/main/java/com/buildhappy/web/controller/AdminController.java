package com.buildhappy.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")//负责处理后台用户管理类的请求
public class AdminController {
	@RequestMapping(value="/")
	public String adminPage(){
		return "admin/index";
	}
}
