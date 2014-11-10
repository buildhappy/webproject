package com.buildhappy.dao.mybatis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buildhappy.dao.NewsDao;
import com.buildhappy.domain.News;
import com.buildhappy.persistence.NewsMapper;

@Service("newsService")
@Repository
@Transactional
public class NewsDaoImp implements NewsDao{
	@Autowired
	private NewsMapper newsMapper;
	
	/************ select ******************/
	@Transactional(readOnly=true)
	public Set<News> selectAllNews(){
		Set<News> news = newsMapper.selectAllNews();
		return news;
	}
	@Transactional(readOnly=true)
	public News selectNewsById(int id){
		News news = newsMapper.selectNewsById(id);
		return news;
	}
	@Transactional(readOnly=true)
	public Set<News> selectNewsByTitle(String title){
		Set<News> news;
		news = newsMapper.selectNewsByTitle(title);
		return news;
	}
	@Transactional(readOnly=true)
	public Set<News> selectNewsByType(String type){
		Set<News> news;
		news = newsMapper.selectNewsByType(type);
		return news;
	}
	@Transactional(readOnly=true)
	public Set<News> selectNewsByAuthor(String author){
		Set<News> news;
		news = newsMapper.selectNewsByAuthor(author);
		return news;
	}
	
	//@Bean//该方法返回值将作为一个bean加载到Spring容器中
	public List<News> selectTop3LatestNewsOfAll(){
		ArrayList<News> news = new ArrayList<News>();
		news.add(new News(1001 , "buildhappy" , 10 , "军事" , "中国占领钓鱼岛" , "中国占领钓鱼岛" , new Date()));
		news.add(new News(1002 , "buildhappy" , 10 , "军事" , "中国占领钓鱼岛" , "中国占领钓鱼岛" , new Date()));
		news.add(new News(1003 , "buildhappy" , 10 , "军事" , "中国占领钓鱼岛" , "中国占领钓鱼岛" , new Date()));
		//news = (ArrayList<News>) newsMapper.selectTop3LatestNewsOfAll();
		return news;
	}
	public News selectLatestNewsOfTheType(String type){
		return newsMapper.selectLatestNewsOfTheType(type);
	}
	
	/************ delete ******************/
	public void deleteById(int id){
		newsMapper.deleteById(id);
	}
	public void deleteByAuthor(String author){
		newsMapper.deleteByAuthor(author);
	}
	
	/************ insert ******************/
	public void insertNews(News news){
		newsMapper.insertNews(news);
	}
	
	/************ update ******************/
	public void updateNews(News news){
		newsMapper.updateNews(news);
	}
}
