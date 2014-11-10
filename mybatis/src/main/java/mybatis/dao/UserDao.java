package mybatis.dao;

import java.security.Provider.Service;
import java.util.ArrayList;
import java.util.List;

import mybatis.entity.User;
import mybatis.service.UserService;

import org.apache.ibatis.session.SqlSession;

/**
 * 数据访问
 * DAO:data accesss object
 * @author Administrator
 *
 */
public class UserDao {
	@SuppressWarnings("static-load")
	public void add(String id ,String name ,String password){
		GetSqlSessionFactory.getInstance();
		SqlSession session = GetSqlSessionFactory.getSqlSessionFactory().openSession();
		try{
			session.getMapper(UserService.class).add(id, name, password);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	
	@SuppressWarnings("static-load")
	public void delete(String id){
		GetSqlSessionFactory.getInstance();
		SqlSession session = GetSqlSessionFactory.getSqlSessionFactory().openSession();
		try{
			session.getMapper(UserService.class).delete(id);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	
	@SuppressWarnings("static-load")
	public int update(String name , String password , String id){
		int count = 0;
		GetSqlSessionFactory.getInstance();
		SqlSession session = GetSqlSessionFactory.getSqlSessionFactory().openSession();
		try{
			count = session.getMapper(UserService.class).update(name, password, id);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			session.close();
		}
		return count;
	}
	
	@SuppressWarnings("static-load")
	public User getUser(String id){
		GetSqlSessionFactory.getInstance();
		SqlSession session = GetSqlSessionFactory.getSqlSessionFactory().openSession();
		User user = null;
		try{
			user =  session.getMapper(UserService.class).getUser(id);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			session.close();
		}
		return user;
	}
	
	@SuppressWarnings("static-load")
	public List<User> getUsers(){
		List<User> users = new ArrayList<User>();
		GetSqlSessionFactory.getInstance();
		SqlSession session = GetSqlSessionFactory.getSqlSessionFactory().openSession();
		try{
			users = session.getMapper(UserService.class).getUsers();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			session.close();
		}
		return users;
	}
	
	@SuppressWarnings("static-load")
	public List<User> getUsersByPage(int offset , int pageSize){
		List<User> users = new ArrayList<User>();
		SqlSession session = GetSqlSessionFactory.getInstance().getSqlSessionFactory().openSession();
		try{
			users = session.getMapper(UserService.class).getUsersByPage(offset, pageSize);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			session.close();
		}
		return users;
	}
}
