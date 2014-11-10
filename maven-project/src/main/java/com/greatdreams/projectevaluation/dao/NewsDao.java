package com.greatdreams.projectevaluation.dao;

import java.util.List;
import java.util.Set;

import com.greatdreams.projectevaluation.domain.News;

public interface NewsDao {
	public Set<News> selectAllNews();
	public News selectNewsById(int id);
	public Set<News> selectNewsByTitle(String title);
	public Set<News> selectNewsByType(String type);
	public Set<News> selectNewsByAuthor(String type);
	public List<News> selectTop3LatestNewsOfAll();
	public News selectLatestNewsOfTheType(String type);
	
	public void deleteById(int id);
	public void deleteByAuthor(String author);
	
	public void insertNews(News news);
	
	public void updateNews(News news);
}
