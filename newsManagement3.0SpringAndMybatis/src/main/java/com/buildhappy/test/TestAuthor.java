package com.buildhappy.test;

import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.buildhappy.domain.Author;
import com.buildhappy.service.AuthorService;

public class TestAuthor {

	public static void main(String[] args) {
		GenericXmlApplicationContext gxc = new GenericXmlApplicationContext();
		gxc.load("classpath:web-app.xml");
		gxc.refresh();
		//ApplicationContext ct = 
		
		AuthorService authorService = gxc.getBean("authorService" , AuthorService.class);
		Set<Author> allAuthor;
		//allAuthor = authorService.selectAllAuthor();
		//showAuthor(allAuthor);
		System.out.println(authorService.selectPasswordByName("buildhappy"));
	}

	public static void showAuthor(Set<Author> allAuthor){
		for(Author author: allAuthor){
			System.out.println(author);
		}
	}
}
