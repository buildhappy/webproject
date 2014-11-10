package com.greatdreams.projectevaluation.dao.imp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greatdreams.projectevaluation.dao.NewsDao;
import com.greatdreams.projectevaluation.domain.News;
import com.greatdreams.projectevaluation.persistence.NewsMapper;

@Service("newsService")
@Repository
@Transactional
public class NewsDaoMybatis implements NewsDao {
	@Autowired
	private NewsMapper newsMapper;
	
	@Override
	@Transactional(readOnly=true)
	public Set<News> selectAllNews() {
		Set<News> news=new HashSet<News>();
		news = newsMapper.selectAllNews();
		return news;
	}

	@Override
	public News selectNewsById(int id) {
		return null;
	}

	@Override
	public Set<News> selectNewsByTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<News> selectNewsByType(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<News> selectNewsByAuthor(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<News> selectTop3LatestNewsOfAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public News selectLatestNewsOfTheType(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteById(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteByAuthor(String author) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertNews(News news) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateNews(News news) {
		// TODO Auto-generated method stub
		
	}
	
}
