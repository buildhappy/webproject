package com.buildhappy.mybatiswithspring.domain;
/**
 * 
 * @author Administrator
 create table contact_tel_detail(
 	id int not null auto_increment,
 	contact_id int not null,
 	tel_type varchar(20) not null,
 	tel_number varchar(20) not null,
 	unique uq_contact_tel_detail_1(contact_id,tel_type),
 	primary key(id),
 	constraint fk_contact_tel_detail_1 foreign key(contact_id) references contact(id)
 	);
 	
 	insert into contact_tel_detail(contact_id,tel_type,tel_number) values(1,"Mobile","123415");
 	insert into contact_tel_detail(contact_id,tel_type,tel_number) values(1,"Home","123415");
 	insert into contact_tel_detail(contact_id,tel_type,tel_number) values(2,"Home","123415");
 */
public class ContactTelDetail {
	private long id;
	private Contact contact;
	private String telType;
	private String telNumber;
	
	public String toString(){
		return "ContactTelDetail-Id:" + id + ",contact id:" + contact.getId() + ",Type:" + telType + ",Number:" + telNumber;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getTelType() {
		return telType;
	}

	public void setTelType(String telType) {
		this.telType = telType;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}
	
}
