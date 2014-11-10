package com.buildhappy.persistence;
/**
 * 与/src/main/resources/com/buildhappy/persistence/AuthorMapper.xml名称要一直
 * 负责mybatis操作Author对象
 */
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.buildhappy.domain.Author;

@Repository
@Service("authorMapper")
public interface AuthorMapper {
	public Set<Author> selectAllAuthor();
	public Author selectAuthorById(int id);
	public String selectPasswordByName(String name);
	
	public void deleteAuthorById(int id);
	
	public void insertAuthor(Author author);
	
	public void updateAuthor(Author author);
}
