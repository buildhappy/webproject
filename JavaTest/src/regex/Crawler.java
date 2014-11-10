package regex;

import java.io.*;
import java.net.*;

public class Crawler {

	public static void main(String[] args) throws Exception {
		String dest = "https://www.google.com/accounts/ClientLogin?Email=buildhappy@gmail.com&Passwd=cai545114597412&service=androidsecure&accountType=HOSTED_OR_GOOGLE";
		//"https://www.google.com"; 
				//"https://www.google.com/accounts/ClientLogin?Email=buildhappy@gmail.com&Passwd=cai545114597412&service=androidsecure&accountType=HOSTED_OR_GOOGLE";//
				//http://mm.10086.cn/download/android/300008285959?from=www";
		downloadApp(dest);
	}

	public static void downloadApp(String dest) throws Exception{
		URL url = new URL(dest);
		URLConnection conn = url.openConnection();//(HttpURLConnection)
		//conn.set
		conn.setConnectTimeout(5000);
		
		FileWriter fw = new FileWriter("aa.txt");
		BufferedWriter buffWriter = new BufferedWriter(fw);
		InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
		BufferedReader buffReader = new BufferedReader(streamReader);
		char[] cbuf = new char[1024];
		int len = 0;
		while((len = buffReader.read(cbuf)) != -1){
			buffWriter.write(cbuf, 0, len);
		}
		
		buffWriter.close();
		buffReader.close();
		/*读取字节文件
		FileOutputStream fot = new FileOutputStream("./file" +"aa.txt");
		BufferedOutputStream buffOut = new BufferedOutputStream(fot);
		BufferedInputStream buffIn = new BufferedInputStream(conn.getInputStream());
		byte[] buf = new byte[1024];
		int len = 0;
		while((len = buffIn.read(buf)) != -1){
			buffOut.write(buf , 0 , len);
		}
		
		buffOut.close();
		buffIn.close();
		*/
	}
}
