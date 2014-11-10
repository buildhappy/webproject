package ch02;

import java.awt.peer.SystemTrayPeer;
import java.util.Random;

import sun.awt.AWTAccessor.SystemTrayAccessor;

public class P50 {
	public static void main(String[] args){
		test2();		
	}
	
	/**
	 * P50 >>>=��λ����
	 */
	public static void test1(){
		
		int i = -1;
		System.out.print(Integer.toBinaryString(i));
		System.out.println(":" + Integer.toBinaryString(i).length());
		i >>>= 10;
		System.out.println(Integer.toBinaryString(i) + ":" + Integer.toBinaryString(i).length() + "\n");
		
		long l = -1;
		System.out.println(Long.toBinaryString(l) + ":" + Long.toBinaryString(l).length());
		l >>>= 10;
		System.out.println(Long.toBinaryString(l) + ":" + Long.toBinaryString(l).length() + "\n");
		
		short s = -1;
		System.out.println(Integer.toBinaryString(s) + ":" + Integer.toBinaryString(s).length());
		s >>>= 10;
		System.out.println(Integer.toBinaryString(s) + ":" + Integer.toBinaryString(s).length() + "\n");
		
		byte b = -1;
		System.out.println(Integer.toBinaryString(b));
		b >>>= 10;
		System.out.println(Integer.toBinaryString(b));
		b = -1;
		System.out.println(Integer.toBinaryString(b));
		System.out.println(Integer.toBinaryString(b >>= 10));
	}
	
	
	/**
	 * ���а�λ�����Ĳ�����
	 */
	static void test2(){
		Random rand = new Random(47);
		int i = rand.nextInt();
		int j = rand.nextInt();
		printBinaryInt("-1", -1);
		printBinaryInt("+1" , 1);
		
		int maxpos = 2147483647;
		//System.out.println(Integer.MAX_VALUE);
		printBinaryInt("maxpos" , maxpos);     //????Ϊʲô�����������Ķ�����ȫΪ1
		int maxneg = -2147483648;
		printBinaryInt("maxneg", maxneg);
		
		printBinaryInt("i", i);
		System.out.println(Integer.toBinaryString(i).length());
//		printBinaryInt("~i", ~i);//��λ��
//		System.out.println(Integer.toBinaryString(~i).length());//����������λ����ͬ����Ϊ��ͷ��0�Ļ�ʡ�Բ�д
//		
//		printBinaryInt("j", j);
//		printBinaryInt("i & j", i & j);//��λ��
//		
//		printBinaryInt("i | j", i | j);//��λ��
//		
//		printBinaryInt("i ^ j", i ^j);//��λ�����һ����1�����1
		
		printBinaryInt("i << 5", i << 5);//����5λ
		
		printBinaryInt("i >> 5", i >> 5);//����5λ
		
		//xxxxxx printBinaryInt("i <<< 5", i <<< 5);//����5λ,
		
		printBinaryInt("i >>> 5", i >>> 5);//����5λ,����������λ��0
		
		boolean b1 = true;
		boolean b2 = false;
		boolean b3 = b1 & b2;
		System.out.println(b3);
	}
	
	static void printBinaryInt(String s ,int i){
		System.out.println(s + ". int: " + i + "\n binary:" + Integer.toBinaryString(i));
	}
	static void printBinaryLong(String s ,long l){
		System.out.println(s + ". long: " + l + "\n binary:" + Long.toBinaryString(l));
	}
}
