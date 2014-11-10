package com.buildhappy.mybatiswithspring.service;

import java.util.List;

import com.buildhappy.mybatiswithspring.domain.Contact;
import com.buildhappy.mybatiswithspring.domain.ContactTelDetail;

public interface ContactService {
	//basic CRUD
	public List<Contact> findAll();
	public void insertContact(Contact contact);
	public void deletByName(String lastName);
	public void updateById(Contact contact);
		
	//association select
	public List<Contact> findAllWithDetail();
	//public List<ContactTelDetail> selectTelDetailsForContact(long id);
}
