package javautils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 该类用于加载数据库驱动、链接、释放数据库
 * @author buildhappy
 * 
 */
public class JdbcUtils {
	private static Properties config = new Properties();// 将数据库的配置信息单独放在一个配置文件中

	static {
		try {
			config.load(JdbcUtils.class.getClassLoader().getResourceAsStream(
					"db.properties"));
			Class.forName(config.getProperty("driver"));
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	// 返回数据库连接
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(config.getProperty("url"),
				config.getProperty("user"), config.getProperty("password"));
	}

	// 释放链接
	public static void releaseConnection(Connection conn, Statement st,
			ResultSet rs) throws Exception{
		if (rs != null) {
			try {
				rs.close();// 可能产生异常
			} catch (Exception e) {
				e.printStackTrace();
			}
			rs = null;// 将rs置为null，变成垃圾由回收器进行处理
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