package com.buildhappy.mybatiswithspring.persistence;

import java.util.List;

import com.buildhappy.mybatiswithspring.domain.Contact;
import com.buildhappy.mybatiswithspring.domain.ContactTelDetail;

public interface ContactMapper {
	//basic CRUD
	public List<Contact> findAll();
	public Contact findById();
	public void deletByName(String lastName);
	public void insertContact(Contact contact);
	public void updateById(Contact contact);
	
	//association select
	public List<Contact> findAllWithDetail();
	//public List<ContactTelDetail> selectTelDetailsForContact(long id);
}
