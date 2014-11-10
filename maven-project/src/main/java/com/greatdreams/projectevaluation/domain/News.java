package com.greatdreams.projectevaluation.domain;

/**
 * 该类用于存放新闻实体
 *
 *创建数据库
use projectevaluation;
create table news(
	id int primary key auto_increment,
	author varchar(15),
	authorId int,
	type varchar(20),
	title varchar(40),
	content text,
	createTime datetime,
	constraint  fk_admin foreign key(authorId) references author(id)
)DEFAULT CHARSET=utf8;
insert into news (id,author,authorId,type,title,content,createtime) values(1,"happy",1,"life","school","upc is big","2011-01-01 00:00:00");
insert into news (id,author,authorId,type,title,content,createtime) values(2,"Jack",2,"timer","school","upc is big","2011-01-01 00:00:00");
insert into news (id,author,authorId,type,title,content,createtime) values(3,"happy",1,"home","country","upc is big","2011-01-01 00:00:00");
insert into news (id,author,authorId,type,title,content,createtime) values(4,"happy",1,"society","country","upc is big","2011-01-01 00:00:00");
insert into news (id,author,authorId,type,title,content,createtime) values(7,"happy",1,"society","考生","5月31日，柳艳兵和同学易政勇乘坐中巴从宜春市区前往袁州区洪塘镇，当车行驶到三阳长村路段时，易政勇后背突然被砍一刀，当他用手回挡时，手臂又被砍了一刀。此时柳艳兵头部和肩部被各砍一刀。就在歹徒继续行凶时，柳艳兵只身夺下其手中的菜刀，将歹徒压倒在地。","2011-01-01");

其中日期格式为：2011-01-01 00:00:00
更改id列为自动增长：alter table questionlib modify id int auto_increment

待改进：增加简述、照片属性
 */
import java.util.Date;

import org.springframework.stereotype.Repository;

@Repository//通过注解定义一个DAO
public class News {
	private int id;
	private String author;
	private int authorId;
	private String type;
	private String title;//题目
	private String content;//内容
	private Date createTime;//编辑新闻的日期 
	
	
	public int getauthorId() {
		return authorId;
	}

	public void setauthorId(int authorId) {
		this.authorId = authorId;
	}

	public News(){
		
	}
	
	public News(int id , String author , int authorId , String type , String title , String contents , Date createTime){
		this.id = id;
		this.author = author;
		this.authorId = authorId;
		this.type = type;
		this.title = title;
		this.content = contents;
		this.createTime = createTime;
	}
	public News(String author , int authorId , String type , String title , String contents , Date createTime){
		this.author = author;
		this.authorId = authorId;
		this.type = type;
		this.title = title;
		this.content = contents;
		this.createTime = createTime;
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


	public Date getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public String toString(){
		return "News-Id:" + this.id + ". authoer:" + this.author + ". title:" + this.title;
	}
	
}
