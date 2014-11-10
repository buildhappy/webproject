package com.buildhappy.domain;
/**
 * 新闻管理员
 * @author buildhappy
 *创建数据库表
	create table author(
		id int primary key,
		name varchar(15),
		password varchar(10)
	);
	insert into author(id,name,password) values(1,'buildahppy','123');
	insert into author(id,name,password) values(2,'Jack','123');
 */
public class Author {
	private int id;
	private String name;
	private String password;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String toString(){
		return "Author-name:" + this.name + ". password:" + this.password;
	}
	
}
