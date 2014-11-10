package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 连接数据库
 * 不建议使用该方法加载驱动，使用demo2的方法
 *
 * 要先在命令行下创建数据库
create database day14;
use day14;
create table users(
	id int primary key,
	name varchar(40),
	password varchar(40),
	email varchar(60),
	birthday date
);

insert into users(id,name,password,email,birthday) values(1,'buildhappy','123','buildhappy@163.com','1988-10-16');
*/
public class Demo1 {
	public static void main(String[] args)throws SQLException{
		String url = "jdbc:mysql://localhost:3306/day14";//jdbc:mysql:是协议，默认端口是3306
		String user = "root";
		String password = "123456";
		
		//1.加载驱动(要导入sql的jar包)
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());//引入sql包中的Driver类
		//2.获取连接
		Connection conn = DriverManager.getConnection(url, user, password);
		//3.获取向数据库发sql语句的statement对象
		Statement st = conn.createStatement();
		//4.向数据库发生sql，获取数据库返回的结果集
		ResultSet rs = st.executeQuery("select * from users");
		
		while(rs.next()){
			System.out.println(rs.getObject("id"));
			System.out.println(rs.getObject("name"));
		}
		
		rs.close();
		st.close();
		conn.close();
	}
}

