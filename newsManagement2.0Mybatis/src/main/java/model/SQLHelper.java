package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import net.iharder.Base64;

import org.junit.Test;

import javautils.JdbcUtils;


/**
 * 操作数据库的类
 * @author buildhappy
 * 
 */
public class SQLHelper {
	private Connection conn = null;
	private Statement st = null;
	private ResultSet rs = null;
	
	//向数据库插入数据
	public boolean insert(String sql) throws Exception{
		//sql = "insert into admin(id,name,password) values(2,'buildahppy','123')";
		this.conn = JdbcUtils.getConnection();
		this.st = conn.createStatement();
		boolean res = this.st.executeUpdate(sql) > 0 ? true:false;
		JdbcUtils.releaseConnection(this.conn, this.st, this.rs);//释放资源
		return res;
	}
	
	//更新数据库
	public boolean update(String sql) throws Exception{
		//sql = "update admin set password='123456' where id=1";
		this.conn = JdbcUtils.getConnection();
		this.st = conn.createStatement();
		boolean res = this.st.executeUpdate(sql) > 0 ? true:false;
		JdbcUtils.releaseConnection(this.conn, this.st, this.rs);//释放资源
		return res;
	}
	
	//删除数据
	public boolean delete(String sql) throws Exception{
		//sql = "delete from admin where id=1";
		this.conn = JdbcUtils.getConnection();
		this.st = conn.createStatement();
		boolean res = this.st.executeUpdate(sql) > 0 ? true:false;
		JdbcUtils.releaseConnection(this.conn, this.st, this.rs);//释放资源
		return res;
	}
	
	//查找数据(返回一条数据)
	public int queryId(String sql) throws Exception{
		//sql = "select * from admin where id=1";
		this.conn = JdbcUtils.getConnection();
		this.st = conn.createStatement();
		this.rs = st.executeQuery(sql);
		int res = 0;
		if(this.rs.next()) res = this.rs.getInt("id");
		JdbcUtils.releaseConnection(this.conn, this.st, this.rs);//释放资源
		return res;
	}
	
	//查找news表中的最新数据,返回一个News对象
	public News queryNearestNews(String tableName , String columName) throws Exception{
		String sql = "select * from news  order by createtime desc limit 0,1";
		News news = null;
		int id = 0;
		String author = null;
		String type = null;
		String newsTitle = null;
		String newsContent = null;
		String date = null;
		this.conn = JdbcUtils.getConnection();
		this.st = conn.createStatement();
		//boolean res = createIndex(tableName , columName);
		//System.out.println(res);
		this.rs = st.executeQuery(sql);
		while(this.rs.next()){
			id = this.rs.getInt("id");
			author = this.rs.getString("author");
			type = this.rs.getString("type");
			newsTitle = this.rs.getString("title");
			newsContent = new String(Base64.decode(this.rs.getString("content")));
			date = this.rs.getString("createtime");
		}
		//deleteIndex(tableName , columName);
		JdbcUtils.releaseConnection(this.conn, this.st, this.rs);//释放资源
		news = new News(id , author , type , newsTitle , newsContent , date);
		return news;
	}
	@Test
	public void queryNearestNews() throws Exception{
		//queryNearestNews("news" , "createtime");
	}
	
	//创建columName列的索引(不能单独使用，要在其他函数中被调用，因为没有建立连接)
	public boolean createIndex(String tableName , String columName) throws Exception{
		String sql = "create index "+tableName+columName+ "index on "  + tableName + " (" + columName+")"; 
		System.out.println(sql);
		//给news表的createtime列创建索引：create index timeorder on news (createtime);
		//使用索引进行查找：select * from news order by createtime;
		boolean res = this.st.execute(sql);
		return res;
		
	}
	
	//删除创建的索引(不能单独使用，要在其他函数中被调用，因为没有建立连接)
	public boolean deleteIndex(String tableName , String columName) throws Exception{
		String sql = "drop index "+tableName+columName+ "index on "  + tableName; 
		//给news表的createtime列创建索引：create index timeorder on news (createtime);
		//使用索引进行查找：select * from news order by createtime;
		return this.st.execute(sql);
	}
}
