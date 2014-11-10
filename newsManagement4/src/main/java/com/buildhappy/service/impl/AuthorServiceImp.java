package com.buildhappy.service.impl;
/**
 * 
 */
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.buildhappy.domain.Author;
import com.buildhappy.persistence.AuthorMapper;
import com.buildhappy.service.AuthorService;

@Repository
@Service("authorService")
public class AuthorServiceImp implements AuthorService{

	@Autowired
	private AuthorMapper authorMapper;

	public List<Author> getById(int id) {
		Author author = authorMapper.selectAuthorById(id);
		List<Author> list = new ArrayList<Author>();
		if(author != null)
			list.add(author);
		return list;
	}

	public boolean deleteByName(String name) {
		return false;
	}

	public void addAuthor(Author author) {
		// TODO Auto-generated method stub
		
	}
	
}
