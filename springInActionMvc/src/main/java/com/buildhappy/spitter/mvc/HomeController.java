package com.buildhappy.spitter.mvc;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.buildhappy.spitter.service.SpitterService;

/**
 * 用于欢迎访问Spitter应用程序的用户
 * 分析见《spring in action》P169
 */
@Controller//声明为控制器
public class HomeController {

  private SpitterService spitterService;

  @Inject
  public HomeController(SpitterService spitterService) {
    this.spitterService = spitterService;
  }
  
  @RequestMapping(value={"/","/home"}, method=RequestMethod.GET)
  public String showHomePage(Map<String, Object> model) {
    model.put("spittles", 
              spitterService.getRecentSpittles(spittlesPerPage));
    return "myHome";
  }


  //<start id="spittlesPerPage"/> 
  public static final int DEFAULT_SPITTLES_PER_PAGE = 25;
  
  private int spittlesPerPage = DEFAULT_SPITTLES_PER_PAGE;
  
  public void setSpittlesPerPage(int spittlesPerPage) {
    this.spittlesPerPage = spittlesPerPage;
  }
  
  public int getSpittlesPerPage() {
    return spittlesPerPage;
  }
}

