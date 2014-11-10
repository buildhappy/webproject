package com.buildhappy.persistence;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.buildhappy.domain.News;

public interface NewsMapper {
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