package com.buildhappy.test;

import java.util.Date;
import java.util.Set;

import org.springframework.context.support.GenericXmlApplicationContext;

import com.buildhappy.domain.News;
import com.buildhappy.service.NewsService;

public class TestNews {
	public static void main(String[] args){
		GenericXmlApplicationContext gxc = new GenericXmlApplicationContext();
		gxc.load("classpath:web-app.xml");
		gxc.refresh();
		
//		for(String beanName: gxc.getBeanDefinitionNames()){
//			System.out.println(beanName);
//		}
		NewsService newsService = gxc.getBean("newsService" , NewsService.class);
		Set<News> allNews;
		
		/**************test select**************/
		/*test selectAll
		allNews =  newsService.selectAllNews();
		showNews(allNews);*/
		
		/*test selectById
		News news = newsService.selectNewsById(1);
		System.out.println(news);*/
		//allNews = newsService.selectNewsByTitle("school");
		//allNews = newsService.selectNewsByType("life");
		//showNews(allNews);
		
		/**************test delete**************/
		//newsService.deleteByAuthor("happy");
		//newsService.deleteById(2);
		
		/********** test insert *******************/
		Date date = new Date(100 , 1 , 2 , 0 , 0 , 0);
		News news = new News(1,"happy",1,"time","school","upc is big",date);
		//newsService.insertNews(news);
		
		/********** test update *******************/
		//newsService.updateNews(news);
		
	}
	
	public static void showNews(Set<News> allNews){
		for(News news: allNews){
			System.out.println(news);
		}
	}
}
