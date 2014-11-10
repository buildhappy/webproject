package com.chapter3_02.jdkDynamicProxy;

public class ProductManagerImp implements ProductManager {
	public void deleteProduct(String name){
		System.out.println("deleteProduct");
	}
	public void findProduct(String name){
		System.out.println("findProduct");
	}
	public void saveProduct(String name){
		System.out.println("saveProduct");
	}
	public void updateProduct(String name){
		System.out.println("updateProduct");
	}
}
