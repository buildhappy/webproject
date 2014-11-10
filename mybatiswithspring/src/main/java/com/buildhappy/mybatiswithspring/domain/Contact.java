package com.buildhappy.mybatiswithspring.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 联系人
 * @author Administrator
 *数据库：mybatisWithSpring
 *表的创建：
 create table contact(
 	ID int null auto_increment,
 	first_name varchar(60) not null,
 	last_name varchar(60) not null,
 	birth_day date,
 	unique uq_contact_1 (first_name,last_name),
 	primary key(id)
 	);
 	insert into contact(first_name,last_name,birth_date) values("Clarence","ho","1988-07-1");
 	insert into contact(first_name,last_name,birth_date) values("Scott","Tiger","1990-07-1");
 	insert into contact(id,first_name,last_name,birth_date) values(2,"John","Smith","1990-07-1");
 */
public class Contact {
	private long id;
	private String firstName;
	private String lastName;
	private Date birthDate;
	private Set<ContactTelDetail> contactTelDetails = new HashSet<ContactTelDetail>();
	
	public Contact(String firstName , String lastName , Date birthDate){
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
	}
	public Contact(long id , String firstName , String lastName , Date birthDate){
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
	}
	public Contact(){}
	
	public Set<ContactTelDetail> getContactTelDetails() {
		return contactTelDetails;
	}
	public void setContactTelDetails(Set<ContactTelDetail> contactTelDetails) {
		this.contactTelDetails = contactTelDetails;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	
	public String toString(){
		return "contact- id:" + this.id + ",First_Name:" + this.firstName;
	}
}
