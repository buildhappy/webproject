package String;

public class Test1 {
	public static void main(String[] args){
		String s = "http://www.baidu.com/aa.html";
		s = s.substring(7);
		s = s.replaceAll("[\\?/:*|<>\"]" , "_") + ".html";
		System.out.println(s);
		s = " a ";
		System.out.println(s.trim());
		
	}
}
