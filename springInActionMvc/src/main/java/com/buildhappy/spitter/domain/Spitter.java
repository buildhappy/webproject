package com.buildhappy.spitter.domain;
/**
 * Spitter -> 作者
 */
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="spitter")
//@XmlRootElement
public class Spitter implements Serializable{
	private static final long serialVersionUID = 1L;
	private long id;
	
	@Size(min=3 , max=20 , message="Username must be between 3 and 20 char")
	@Pattern(regexp="^[a-zA-Z0-9]+$",message="Username must be alphanumeric with no spaces")  
				//<co id="co_noSpaces"/> 包括文字与数字
	private String name;
	
	private String username;
	private String password;
	private String fullName;
	
	@Pattern(regexp="[a-zA-Z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}",
			message="Invalid email address.")
	private String email;
	
	private boolean updateByEmail;
	
	@Id
	@GeneratedValue()
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="username" , unique=true)
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

	@Column(name="fullname")
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullname) {
		this.fullName = fullname;
	}

	@Column(name="email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name="update_by_email")
	public boolean isUpdateByEmail() {
		return updateByEmail;
	}

	public void setUpdateByEmail(boolean updateByEmail) {
		this.updateByEmail = updateByEmail;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public List<Spittle> getRecentSpittles(){
		Spittle spittle = new Spittle();
		spittle.setId(999L);
		spittle.setSpitter(this);
		spittle.setText("test spittle#99");
		spittle.setWhen(new Date());
		return Arrays.asList(spittle);
	}
	
	@Override
	public boolean equals(Object obj){
		return new EqualsBuilder().reflectionEquals(this , obj);
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder().reflectionHashCode(this);
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
