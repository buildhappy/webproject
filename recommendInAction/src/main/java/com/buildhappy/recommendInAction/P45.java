package com.buildhappy.recommendInAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 《推荐系统实践》算法实现
 * P45，利用Jaccard公式计算用户的相似度
 * @author Administrator
 *
 */
class UserSimilarity {
	
	private List<Item> item_user;
	
	public UserSimilarity(){
		this.item_user = new ArrayList<Item>();
	}
	/**
	 * 读取数据文件,将用户和商品信息存到user_item中
	 * @throws IOException 
	 */
	public void readDataFile(String filePath) throws IOException{
		FileReader reader = new FileReader(new File(filePath));
		BufferedReader bufReader = new BufferedReader(reader);
		String line = null;
		int i = 0;
		while((line = bufReader.readLine()) != null){
			String user = line.split(":")[0];
			String[] itemNames = line.split(":")[1].split(",");
//			for(String itemName:itemNames){
//				System.out.print(itemName + ":");
//				Item item = new Item((String)itemName);
//				System.out.println(item.equals(new Item("a")));
//			}
			for(String itemName:itemNames){
				Item item = new Item(itemName.toString());
				int index = 0;
				if((index = item_user.indexOf(item)) != -1){//user_item已经有该货物
					item = (Item) (item_user.get(index));
					item_user.remove(index);
					item.addUser(user);
					item_user.add(item);
				}else{
					item.addUser(user);
					item_user.add(item);
				}
			}
		}
	}
	
	public void testItemEquals(){
		Item item1 = new Item("a");
		Item item2 = new Item("a");
		item2.addUser("123");
		System.out.println(item1.hashCode() == item2.hashCode());
		//System.out.println(item1.equals(item2));
		item_user.add(item1);
		//printItemUser();
		item1.addUser("mary");
		//System.out.println(item1);
		//System.out.println(item1);
		int index = 4;
		index = item_user.indexOf(item2);
		System.out.println(index);
//		item_user.remove(index);
//		item1.addUser("faf");
//		item_user.add(item1);
//		printItemUser();
		
	}
	
	/**
	 * 打印列表数据
	 * @param list
	 */
	public void printItemUser(){//List<Map<String,String[]>> list
		Iterator it = item_user.iterator();
		while(it.hasNext()){
			Item item = (Item) it.next();
			System.out.println(item.toString());
			
		}
	}
}


class Item {
	/**
	 * 商品名称
	 */
	private String itemName;
	
	/**
	 * 使用过该商品的所有用户
	 */
	private transient Set<String> users;
	
	
	public Item(String itemName){
		this.users = new HashSet<String>();
		this.itemName = itemName;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public Set<String> getUsers(){
		return this.users;
	}
	public boolean addUser(String userName){
		return users.add(userName);
	}
	
	/**
	 * 判断两个Item对象是否相等
	 */
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Item)){
			throw new ClassCastException("not match cast");
		}
		Item item = (Item)obj;
		//当商品名相同，并且所有用户名的hash值相同时，表名两个对象相同
		return this.itemName.equals(item.getItemName());//new String(item.itemName).equals(new String(this.itemName)); //new String(item.itemName).equals(new String(this.itemName));//this.itemName == item.itemName;//&&((item.getUsers().hashCode()) == this.getUsers().hashCode());
	}
	@Override
	public String toString(){
		Iterator it = users.iterator();
		String userNames = "";
		while(it.hasNext()){
			userNames += it.next() + " ";
		}
		return this.itemName + ":" + userNames;
	}
	
}
public class P45{
	public static void main(String[] args) throws IOException{
		UserSimilarity us = new UserSimilarity();
		us.readDataFile("data.txt");
		us.printItemUser();
		System.out.println(new String("new String") == new String("new String"));
		System.out.println("new String".equals(new String("new String")));
//		
		//us1.testItemEquals();
		//
	}
}
