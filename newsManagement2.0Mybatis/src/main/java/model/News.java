package model;

/**
 * 该类用于存放新闻实体
 *
 *创建数据库
create table news(
	id int primary key auto_increment,
	author varchar(15),
	type varchar(20),
	title varchar(40),
	content text,
	createtime datetime
);
insert into news (id,author,type,title,content,createtime) values(1,"happy","life","school","upc is big","2011-01-01 00:00:00");
其中日期格式为：2011-01-01 00:00:00
更改id列为自动增长：alter table questionlib modify id int auto_increment
 */
import java.util.Date;

public class News {
	private int id;
	private String author;
	private String type;
	private String title;//题目
	private String content;//内容
	private String createTime;//编辑新闻的日期 
	
	public News(){
		
	}
	
	public News(int id , String author ,String type , String title , String contents , String createTime){
		this.id = id;
		this.author = author;
		this.type = type;
		this.title = title;
		this.content = contents;
		this.createTime = createTime;
	}
		
	//将新闻类的一个对象放到数据库中(id为自动增长)
	public boolean pushDB() throws Exception{
		String sql = "insert into news(author,type,title,content,createtime) values(";
		sql +="'"+this.author +"','"+ this.type +"','" + this.title +"','"+ this.content +"','"+ this.createTime+"')";
		System.out.println("sql:" + sql);
		SQLHelper sqlHelper = new SQLHelper();
		return sqlHelper.insert(sql);
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getCreateTime() {
		return createTime;
	}


	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
