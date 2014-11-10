package test;

import java.io.IOException;
import java.io.Reader;

import model.News;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class Test {
	private static SqlSessionFactory sqlSessionFactory;
	private static Reader reader;
	
	static{
		try {
			reader = Resources.getResourceAsReader("mybatis-config.xml");
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static SqlSessionFactory getSession(){
		return sqlSessionFactory;
	}
	
	public static void main(String[] args){
		SqlSession session = sqlSessionFactory.openSession();
		try{
			News news = (News) session.selectOne("mybatis.NewsMapper.selectNewsByID", 1);
			System.out.println(news.getAuthor());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
}
