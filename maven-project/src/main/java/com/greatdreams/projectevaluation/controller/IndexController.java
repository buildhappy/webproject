package com.greatdreams.projectevaluation.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.greatdreams.projectevaluation.dao.NewsDao;
import com.greatdreams.projectevaluation.domain.News;

@Controller
public class IndexController {
	
	private static final Logger logger= Logger.getLogger(IndexController.class);
	
	@Autowired
	private NewsDao newsDao;
	
	@RequestMapping(value="/")
	public String index() {
		
		if(logger.isDebugEnabled())
			logger.debug("the user is fecthing homepage of this website");
		
		return "index";
	}
	
	@RequestMapping(value="/index")
	public ModelAndView index1() {
		if(logger.isDebugEnabled())
			logger.debug("the user is fecthing homepage of this website");
		Map<String , Set<News>> newsMap = new HashMap<String , Set<News>>();
		Set<News> allNews = newsDao.selectAllNews();
		newsMap.put("allNews", allNews);
		return new ModelAndView("index" , newsMap);
	}
	
	@RequestMapping(value="/index.html")
	public String index2() {
		if(logger.isDebugEnabled())
			logger.debug("the user is fecthing homepage of this website");
		return "index";
	}
	
	@RequestMapping(value="/index.jsp")
	public String index3() {
		if(logger.isDebugEnabled())
			logger.debug("the user is fecthing homepage of this website");
		return "index";
	}
	
	@RequestMapping(value="/default.html")
	public String index4() {
		if(logger.isDebugEnabled())
			logger.debug("the user is fecthing homepage of this website");
		return "index";
	}
	
	@RequestMapping(value="/default.jsp")
	public String index5() {
		if(logger.isDebugEnabled())
			logger.debug("the user is fecthing homepage of this website");
		return "index";
	}
}
