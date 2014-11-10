package model;
/**
 * 新闻管理员
 * @author buildhappy
 *创建数据库表
	create table admin(
		id int primary key,
		name varchar(15),
		password varchar(10)
	);
	insert into admin(id,name,password) values(1,'buildahppy','123');
 */
public class Admin {
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
	
}
