package cn.itcast.dao;
/**
 * UserDao的作用，将User对象增、删、改、查到数据库或XML文件中
 */
import cn.itcast.domain.User;

public interface UserDao {

	void add(User user);

	User find(String username, String password);

	//查找注册的用户是否在数据库中存在
	boolean find(String username);
}