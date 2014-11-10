package com.greatdreams.simplespringweb.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/hello")
public class HelloContonller {
	
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView printWelcome(){
		String message = " Welcome to the simplespringwebwithmaven project";
		return new ModelAndView("hello","message",message);
	}

}
