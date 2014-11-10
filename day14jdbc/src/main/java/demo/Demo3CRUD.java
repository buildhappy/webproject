package demo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import utils.JdbcUtils;

public class Demo3CRUD {
	private Connection conn = null;
	private Statement st = null;
	private ResultSet rs = null;

	@Test
	public void insert() throws Exception {
		try {
			String sql = "insert into users(id,name,password,email,birthday) values(6,'zhangs','123','zah@13.com','1955-1-2')";
			conn = JdbcUtils.getConnection();
			st = conn.createStatement();
			int row = st.executeUpdate(sql);
			if(row > 0){
				System.out.println("insert ok");
			}
		} finally {
			JdbcUtils.releaseConnection(conn, st, rs);
		}
	}
}
