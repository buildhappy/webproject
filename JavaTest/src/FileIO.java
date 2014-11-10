import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class FileIO {

	public static void main(String[] args)throws IOException {
		String s = "中国";
		
		////////////// 字节流   ///////
		/* FileOutputStream 类
		FileOutputStream fos = new FileOutputStream("1.txt");
		fos.write(s.getBytes("UTF-8"));
		fos.close();
		*/
		
		/* FileInputStream 类
		FileInputStream fis = new FileInputStream("1.txt");
		byte[] b = new byte[1024];
		int len = fis.read(b);
		String myStr = new String(b , 0 , len , "UTF-8");
		System.out.println(myStr);
		*/
		
		//////////////  字符流    ////////////
		
		/* FileWriter 类
		FileWriter fw = new FileWriter("1.txt");
		fw.write(s);
		fw.close();
		*/
		
		/* FileReader 类
		FileReader fr = new FileReader("1.txt");
		char[] b = new char[1024];
		fr.read(b);
		String myStr = new String(b);
		System.out.println(myStr);
		*/
		
		
	}
}
