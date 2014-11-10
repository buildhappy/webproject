package junit.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileSave {
	public static void main(String[] args) throws Exception{
		/*
		String filePath = "D:/app/";
		File f = new File(filePath);
		if(!f.exists()){
			f.mkdirs();
		}
		FileInputStream fIn = new FileInputStream(new File("C:/Users/Administrator/Desktop/webproject/day01-jdk5/src/main/java/junit/test" + "/PersonTest.java"));
		FileOutputStream fOut = new FileOutputStream(new File(filePath + "PersonCopy"+ ".java"));
		byte[] buf = new byte[1024];
		int len = 0;
		while((len = fIn.read(buf)) != -1){
			fOut.write(buf, 0, len);
		}
		fOut.flush();
		fIn.close();
		fOut.close();
		System.out.print("done");
		*/
		String s = "D:/app/";
		System.out.println(s.endsWith("/"));
	}
	
}
