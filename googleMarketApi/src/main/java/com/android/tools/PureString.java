package com.android.tools;
/**
 * 将字符串中出现的. / \ : 等特殊字符去掉
 * @author Administrator
 *
 */
public class PureString {
	public static String pureString(String s){
		String regex ;
		String subS = s;
		for(int i = 32 ; i < 47 ; i++){
			String c = String.valueOf(((char) i));
			regex = "\\" + c;
			System.out.println(regex);
			subS = subS.replaceAll(regex, "");
		}
		for(int i = 58 ; i < 65 ; i++){
			String c = String.valueOf(((char) i));
			regex = "\\" + c;
			System.out.println(regex);
			subS = subS.replaceAll(regex, "");
		}
		subS = subS.replaceAll("\\/", "");
		return subS;
	}
}
