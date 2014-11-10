package com.chapter2_02.constructor;

public class Person {
	private String pname = "Jack";
	private Integer page = 10;
	
	
	public String getPname() {
		return pname;
	}
	public Person(){
		System.out.println("Person construstor");
	}
	
	public Person(String pname , Integer page){
		this.pname = pname;
		this.page = page;
	}
	
	public void setPname(String pname) {
		this.pname = pname;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	
}
