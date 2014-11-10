package com.greatdreams.projectevaluation.dao.imp;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.greatdreams.projectevaluation.dao.NewsDao;
import com.greatdreams.projectevaluation.domain.News;
import com.greatdreams.projectevaluation.persistence.NewsMapper;

public class NewsPersistenceTest {
	@Autowired
	public static NewsMapper newsMapper;
	public static void main(String[] args) {
		GenericXmlApplicationContext gtx = new GenericXmlApplicationContext();
		gtx.load("classpath:news-test.xml");
		gtx.refresh();
		
		NewsDao newsDao = gtx.getBean("newsService" , NewsDao.class);
		
		Set<News> news = newsDao.selectAllNews();
		
		Iterator<News> it = news.iterator();
		while(it.hasNext()){
			System.out.println(it.next().getTitle());
		}
	}

}
