package com.buildhappy.dao;

import java.util.Set;

import com.buildhappy.domain.Author;

public interface AuthorDao {
	
	public Set<Author> selectAllAuthor();
	public Author selectAuthorById(int id);
	public String selectPasswordByName(String name);
	
	public void deleteAuthorById(int id);
	
	public void insertAuthor(Author author);
	
	public void updateAuthor(Author author);
}
