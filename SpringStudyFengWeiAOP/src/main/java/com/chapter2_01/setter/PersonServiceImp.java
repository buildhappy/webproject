package com.chapter2_01.setter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PersonServiceImp implements PersonService{
	private String name;
	private Integer age;
	private Person person;
	
	//inner bean(through beans.xml)
	private Person personClass;
	
	//inject Array
	private Object[] objs;
	
	//inject List
	private List lists;
	
	//inject set
	private Set sets;
	
	//inject map
	Map map;
	
	
	
	public void savePerson(){
		System.out.println("savePerson");
		System.out.println("name  " + name);
		System.out.println("person.getPname()  "+ person.getPname());
		System.out.println("person.getPage()   "+ person.getPage());
		
		System.out.println("personClass.getPname()  "+ personClass.getPname());
		System.out.println("personClass.getPage()   "+ personClass.getPage());
		
		//print Array objs
		System.out.println("objs[0]:" + objs[0]);
		System.out.println("objs[1]:" + objs[1]);
		System.out.println("objs[2]:" + objs[2]);
		
		//print List lists
		System.out.println("lists01:" + lists.get(0));
		System.out.println("lists02:" + lists.get(1));
		System.out.println("lists03:" + lists.get(2));
		
		//print Set sets
		Iterator itr = sets.iterator();
		while(itr.hasNext()){
			System.out.println(itr.next().toString());
		}
		
		//print Map map
		System.out.println("map01:" + map.get("key01"));
		System.out.println("map02:" + map.get("key02"));
	}

	
	public Object[] getObjs() {
		return objs;
	}

	public void setObjs(Object[] objs) {
		this.objs = objs;
	}

	public List getLists() {
		return lists;
	}

	public void setLists(List lists) {
		this.lists = lists;
	}

	public Set getSets() {
		return sets;
	}

	public void setSets(Set sets) {
		this.sets = sets;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public Person getPersonClass() {
		return personClass;
	}

	public void setPersonClass(Person personClass) {
		this.personClass = personClass;
	}

	public Person getPerson(){
		return new Person();
	}
	
	public void setPerson(Person person){
		this.person = person;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
	
	
}
