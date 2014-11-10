package com.buildhappy.mybatiswithspring.service.mybatis;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buildhappy.mybatiswithspring.domain.Contact;
import com.buildhappy.mybatiswithspring.domain.ContactTelDetail;
import com.buildhappy.mybatiswithspring.persistence.ContactMapper;
import com.buildhappy.mybatiswithspring.service.ContactService;

@Service("contactService")//Spring创建一个名为contactService的对象
@Repository
@Transactional
public class ContactServiceImp implements ContactService{
	private Log log = LogFactory.getLog("ContactService.class");
	
	@Autowired //通过注解的方式实现依赖注入
	private ContactMapper contactMapper;
	
	//basic CRUD
	@Transactional(readOnly=true)
	public List<Contact> findAll(){
		List<Contact> contacts = contactMapper.findAll();
		return contacts;
	}
	
	public void insertContact(Contact contact){
		contactMapper.insertContact(contact);
	}
	
	public void deletByName(String lastName){
		contactMapper.deletByName(lastName);
	}
	
	public void updateById(Contact contact){
		contactMapper.updateById(contact);
	}
	
	//association select
	public List<Contact> findAllWithDetail(){
		List<Contact> contacts = contactMapper.findAllWithDetail();
		for(Contact contact: contacts){
			populateContactTelDetail(contact);
		}
		return contacts;
	}
	
	public void populateContactTelDetail(Contact contact){
		if(contact.getContactTelDetails() != null){//if the contactDetail in contact allocate the space
			for(ContactTelDetail contactTelDetail: contact.getContactTelDetails()){
				contactTelDetail.setContact(contact);
			}
		}
	}
}
