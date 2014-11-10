package mybatis.entity;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

@Alias("User")
public class User implements Serializable{
	
	private static final long serialVersionUID =  -2478236396012275225L;
	
	private String id;
	//用户名
	private String username;
	//密码  
	private String password; 
	//姓名 
	private String name;
	//昵称 
	private String nickname; 
	//性别 
	private String sex;    
	//用户头像图片  
	private String picture;
	//创建时间   
	private String createtime;
	//上次登录时间 
	private String lastlogintime;
	private String tilepath;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getLastlogintime() {
		return lastlogintime;
	}
	public void setLastlogintime(String lastlogintime) {
		this.lastlogintime = lastlogintime;
	}
	public String getTilepath() {
		return tilepath;
	}
	public void setTilepath(String tilepath) {
		this.tilepath = tilepath;
	} 	

}