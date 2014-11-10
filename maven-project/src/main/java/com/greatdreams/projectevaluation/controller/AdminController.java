package com.greatdreams.projectevaluation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/admin")
public class AdminController {
	@RequestMapping("/")
	public String index() {
		return "admin/index";
	}
}
