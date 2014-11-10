package com.buildhappy.dao.mybatis;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buildhappy.dao.AuthorDao;
import com.buildhappy.domain.Author;
import com.buildhappy.persistence.AuthorMapper;

@Service("authorService")
@Repository
@Transactional

public class AuthorDaoImp implements AuthorDao {
	//@Autowired
	private AuthorMapper authorMapper;
	
	public Set<Author> selectAllAuthor(){
		Set<Author> allAuthor = authorMapper.selectAllAuthor();
		return allAuthor;
	}
	public Author selectAuthorById(int id){
		return authorMapper.selectAuthorById(id);
	}
	
	public String selectPasswordByName(String name){
		return authorMapper.selectPasswordByName(name);
	}
	
	public void deleteAuthorById(int id){
		authorMapper.deleteAuthorById(id);
	}
	
	public void insertAuthor(Author author){
		authorMapper.insertAuthor(author);
	}
	
	public void updateAuthor(Author author){
		authorMapper.updateAuthor(author);
	}
}
