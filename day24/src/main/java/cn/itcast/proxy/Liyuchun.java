package cn.itcast.proxy;

public class Liyuchun implements Person {

	public String sing(String name){
		System.out.println("春哥唱"+name+"歌了！！");
		return "谢谢哟！！";
	}
	
	public String dance(String name){
		System.out.println("春哥跳"+name+"舞了!!");
		return "飞吻！！";
	}
	
	
	
}
