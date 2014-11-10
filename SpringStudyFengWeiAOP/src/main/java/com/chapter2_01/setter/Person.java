package com.chapter2_01.setter;

public class Person {
	private String pname = "Jack";
	private Integer page = 10;
	public String getPname() {
		return pname;
	}
	public Person(){
		System.out.println("Person construstor");
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
