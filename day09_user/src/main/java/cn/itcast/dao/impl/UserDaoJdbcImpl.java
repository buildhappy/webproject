package cn.itcast.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import cn.itcast.dao.UserDao;
import cn.itcast.domain.User;
import cn.itcast.exception.DaoException;
import cn.itcast.utils.JdbcUtils;
import java.util.Date;

public class UserDaoJdbcImpl implements UserDao {

	public void add(User user) {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try{
			conn = JdbcUtils.getConnection();
			String sql = "insert into users(id,username,password,email,birthday,nickname) values(?,?,?,?,?,?)";
			st = conn.prepareStatement(sql);
			st.setString(1, user.getId());
			st.setString(2, user.getUsername());
			st.setString(3, user.getPassword());
			st.setString(4, user.getEmail());
			st.setDate(5, new java.sql.Date(user.getBirthday().getTime()));
			st.setString(6, user.getNickname());
			int num = st.executeUpdate();
			if(num<1){
				throw new RuntimeException("注册用户失败");
			}
		}catch (Exception e) {
			throw new DaoException(e);   //gosling  thinking in java   spring
		}finally{
			JdbcUtils.release(conn, st, rs);
		}
		
		
	}

	//username=' or 1=1 or username='   password=""
	/*public User find(String username, String password) {
		
		Connection conn = null;
		Statement st = null;   //PreparedStatment
		ResultSet rs = null;
		try{
			conn = JdbcUtils.getConnection();
			st = conn.createStatement();
			//sql = select * from users where username='' or 1=1 or username='' and password='"+password+"'"
			//sql = select * from users where true;
			
			String sql = "select * from users where username='"+username+"' and password='"+password+"'";
			rs = st.executeQuery(sql);
			if(rs.next()){
				User user = new User();
				user.setBirthday(rs.getDate("birthday"));
				user.setEmail(rs.getString("email"));
				user.setId(rs.getString("id"));
				user.setNickname(rs.getString("nickname"));
				user.setPassword(rs.getString("password"));
				user.setUsername(rs.getString("username"));
				return user;
			}
			return null;
		}catch (Exception e) {
			throw new DaoException(e); 
		}finally{
			JdbcUtils.release(conn, st, rs);
		}
	}*/
	
	/*
	statment和preparedStatement的区别:
	1. preparedStatement是statement的孩子
	2. preparedStatement可以防止sql注入的问题
	3. preparedStatement会对sql语句进行预编译，以减轻数据库服务器的压力
	
	java---class---jvm
	sql----编译---数据库
	*/
	
	public User find(String username, String password) {
		
		Connection conn = null;
		PreparedStatement st = null;   //PreparedStatment
		ResultSet rs = null;
		try{
			conn = JdbcUtils.getConnection();
			String sql = "select * from users where username=? and password=?";
			st = conn.prepareStatement(sql);   //预编译这条sql
			
			
			st.setString(1, username);
			st.setString(2, password);
			
			rs = st.executeQuery();
			if(rs.next()){
				User user = new User();
				user.setBirthday(rs.getDate("birthday"));
				user.setEmail(rs.getString("email"));
				user.setId(rs.getString("id"));
				user.setNickname(rs.getString("nickname"));
				user.setPassword(rs.getString("password"));
				user.setUsername(rs.getString("username"));
				return user;
			}
			return null;
		}catch (Exception e) {
			throw new DaoException(e); 
		}finally{
			JdbcUtils.release(conn, st, rs);
		}
	}

	public boolean find(String username) {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try{
			conn = JdbcUtils.getConnection();
			String sql = "select * from users where username=?";
			st = conn.prepareStatement(sql);
			st.setString(1, username);
			
			rs = st.executeQuery();
			if(rs.next()){
				return true;
			}
			return false;
		}catch (Exception e) {
			throw new DaoException(e); 
		}finally{
			JdbcUtils.release(conn, st, rs);
		}
	}
}
