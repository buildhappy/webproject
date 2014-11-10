package ch11;

public class P31 {
	public static void main(String[] args){
		//从运行程序的系统中获取的所有“属性”，提供环境信息
		System.getProperties().list(System.out);
		System.out.println(System.getProperty("user.name"));
		System.out.println(System.getProperty("java.library.path"));
	}

}
