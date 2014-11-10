package javabean;

import java.util.Date;

/**
 * 该类主要用于存储day14数据库中的users表中的内容
 * @author buildhappy
 *
 */
public class User {
	private int id;
	private String name;
	private String password;
	private Date date;
	
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
