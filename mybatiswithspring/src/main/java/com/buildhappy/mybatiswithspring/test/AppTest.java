package com.buildhappy.mybatiswithspring.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.context.support.GenericXmlApplicationContext;

import com.buildhappy.mybatiswithspring.domain.Contact;
import com.buildhappy.mybatiswithspring.domain.ContactTelDetail;
import com.buildhappy.mybatiswithspring.service.ContactService;


public class AppTest {

	public static void main(String[] args) {
		GenericXmlApplicationContext gtx = new GenericXmlApplicationContext();
		gtx.load("classpath:app-context.xml");
		gtx.refresh();
//		for(String beanname: gtx.getBeanDefinitionNames()){
//			System.out.println(beanname);
//		}
		ContactService contactService = gtx.getBean("contactService" , ContactService.class);
		
		//查询所以数据
		List<Contact> contacts;
		//contacts = contactService.findAll();
		//listContacts(contacts);
		
		//插入数据
		Date date = new Date(1997 , 8 , 12);
		//Calendar
		Contact contact = new Contact(1 , "zhen" , "guo" , date);
		//contactService.insertContact(contact);
		
		//delete data
		//contactService.deletByName("tiger");
		
		//update data
		//contactService.updateById(contact);
		
		//association select
		//contacts = contactService.findAllWithDetail();
		//listContactWithDetail(contacts);
	}
	
	//show the Contact
	public static void listContacts(List<Contact> contacts){
		System.out.println("");
		System.out.println("Listing contacts without details");
		for(Contact contact : contacts){
			System.out.println(contact);
			System.out.println();
		}
	}
	
	//show the Contact with telDetail
	public static void listContactWithDetail(List<Contact> contacts){
		for(Contact contact:contacts){
			if(contact.getContactTelDetails() != null){
				for(ContactTelDetail contactTelDetail:contact.getContactTelDetails()){
					System.out.println(contactTelDetail);
				}
			}
		}
	}
}
