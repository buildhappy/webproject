package com.buildhappy.service;

import java.util.List;

import com.buildhappy.domain.Author;

public interface AuthorService {
	public List<Author> getById(int id);
	public boolean deleteByName(String name);
	public void addAuthor(Author author);
}
