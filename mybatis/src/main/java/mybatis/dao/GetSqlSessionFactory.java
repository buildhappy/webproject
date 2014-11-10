package mybatis.dao;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * 获取数据源
 * @author Administrator
 *
 */
public class GetSqlSessionFactory {
	private static SqlSessionFactory sqlSessionFactory = null;
	private static GetSqlSessionFactory getSqlSessionFactory = null;
	
	private GetSqlSessionFactory(){
		String rs = "mybatis-config.xml";
		Reader reader = null;
		try{
			reader = Resources.getResourceAsReader(rs);
		}catch(IOException e){
			e.printStackTrace();
		}
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);//create the 
	}
	public static SqlSessionFactory getSqlSessionFactory(){
		return sqlSessionFactory;
	}
	public static GetSqlSessionFactory getInstance(){
		if(getSqlSessionFactory == null){
			getSqlSessionFactory = new GetSqlSessionFactory();
		}
		return getSqlSessionFactory;
	}
}
