package com.chapter3_02.jdkDynamicProxy;

public interface ProductManager {
	public void deleteProduct(String name);
	public void findProduct(String name);
	public void saveProduct(String name);
	public void updateProduct(String name);
}
