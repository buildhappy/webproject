package com.buildhappy.spitter.mvc;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/login")
public class LoginController {
  @RequestMapping(value="/",method=GET)
  public String showLoginForm() {
	  return "login";
  }
}