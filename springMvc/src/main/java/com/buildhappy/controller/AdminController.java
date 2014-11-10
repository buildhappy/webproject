package com.buildhappy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {
	@RequestMapping("admin/index.html")
	public ModelAndView adminIndex(){
		return new ModelAndView("admin/index");
	}
}
