package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javabean.User;

/**
 * 连接数据库 为了更好的释放Connection连接对象，所以要用try...finally
 * 
 * 要先在命令行下创建数据库 create database day14; use day14; create table users( id int
 * primary key, name varchar(40), password varchar(40), email varchar(60),
 * birthday date );
 * 
 * insert into users(id,name,password,email,birthday)
 * values(1,'buildhappy','123','buildhappy@163.com','1988-10-16');
 */
public class Demo2 {
	public static void main(String[] args) throws SQLException,
			ClassNotFoundException {
		String url = "jdbc:mysql://localhost:3306/day14";// jdbc:mysql:是协议，默认端口是3306
		String user = "root";
		String password = "123456";

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			// 1.加载驱动(要导入sql的jar包)
			// DriverManager.registerDriver(new
			// com.mysql.jdbc.Driver());//引入sql包中的Driver类
			Class.forName("com.mysql.jdbc.Driver");//Class.forName(name)返回与name相同的类或对象
			// 2.获取连接
			conn = DriverManager.getConnection(url, user, password);
			// 3.获取向数据库发sql语句的statement对象
			st = conn.createStatement();
			// 4.向数据库发生sql，获取数据库返回的结果集
			rs = st.executeQuery("select * from users");

			// 将获取的数据放在一个javabean中
			while (rs.next()) {
				System.out.println(rs.getObject("id"));
				User users = new User();
				users.setId(rs.getInt("id"));// users.setId((Integer)rs.getObject("id"));
				users.setName(rs.getString("name"));
				users.setPassword(rs.getString("password"));
				users.setDate(rs.getDate("birthday"));
			}
		} finally {
			if (rs != null) {
				try {
					rs.close();// 可能产生异常
				} catch (Exception e) {
					e.printStackTrace();
				}
				rs = null;//将rs置为null，变成垃圾由回收器进行处理
			}

			if (st != null) {
				try {
					st.close();// 可能产生异常
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}
}
