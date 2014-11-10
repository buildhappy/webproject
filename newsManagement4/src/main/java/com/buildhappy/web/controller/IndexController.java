package com.buildhappy.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.buildhappy.dao.NewsDao;
import com.buildhappy.domain.News;

@Controller
@RequestMapping(value="/news")//处理 查看新闻 类的请求
public class IndexController {
	@Qualifier("newsService")
	private NewsDao newsDao;
	@RequestMapping(value="/index")
	public ModelAndView indexPage(){//List<News> newsDao
		Map<String , List> result = new HashMap<>();
		
		//获取最新的三条新闻
		List top3News = new ArrayList();//newsDao.selectTop3LatestNewsOfAll();
		
		top3News.add(new News(1001 , "buildhappy" , 10 , "军事" , "中国占领钓鱼岛" , "中国占领钓鱼岛" , new Date()));
		top3News.add(new News(1002 , "buildhappy" , 10 , "军事" , "中国占领钓鱼岛" , "中国占领钓鱼岛" , new Date()));
		top3News.add(new News(1003 , "buildhappy" , 10 , "军事" , "中国占领钓鱼岛" , "中国占领钓鱼岛" , new Date()));
		result.put("top3News" , top3News);
		
		//取出society类最新的新闻
		News news = new News(1001 , "buildhappy" , 10 , "军事" , "中国占领钓鱼岛" , "中国占领钓鱼岛" , new Date());//newsDao.selectLatestNewsOfTheType("society");
		List societyNews =  new ArrayList();
		societyNews.add(news);
		result.put("societyNews" , societyNews);
		
		//news = newsDao.selectLatestNewsOfTheType("technology");
		List<News> technologyNews =  new ArrayList<News>();
		technologyNews.add(news);
		result.put("technologyNews" , technologyNews);
		
		return new ModelAndView("newsMagazine" , result);
	}
	
	
	
}
