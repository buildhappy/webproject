package ch13String;

import java.util.ArrayList;

/**
 * 打印对象的内存地址
 * @author Administrator
 *
 */
public class P287 {
	public static void main(String[] args){
		
		
		ArrayList<WrongAddress> wrongs = new ArrayList<WrongAddress>();
		for(int i = 0; i < 3; i++){
			wrongs.add(new WrongAddress());
		}
		System.out.println(wrongs);
		
		ArrayList<RightAddress> rights = new ArrayList<RightAddress>();
		for(int i = 0; i < 3; i++){
			rights.add(new RightAddress());
		}
		System.out.println(rights);
	}

}

/**
 * toString方法中，当看到this时，会调用this的toString方法，将其转换成String类型，因此发生了递归调用
 * @author Administrator
 *
 */
class WrongAddress{
	@Override
	public String toString(){
		return "Address:" + this;
	}
}

/**
 * 要想获得对象的内存地址，应该调用父类的toString方法
 * @author Administrator
 *
 */
class RightAddress{
	@Override
	public String toString(){
		return "Address:" + super.toString();
	}
}