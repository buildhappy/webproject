package itcast.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * 在web工程的普通java程序中如何读取资源文件——类装载器
 * @author buildhappy
 *
 */
public class StudentDao {  //该类已经不是servlet，不能用ServletContext函数

	public String get() throws IOException {  //类装载器读
		test1();
		//test2();
		return null;
	}
	
	//以下代码在读文件时，可以读到更新后的文件
	public void test2() throws IOException{	
		ClassLoader loader = StudentDao.class.getClassLoader();//获取StudentDao的类装载器，获取文件位置
		URL url = loader.getResource("itcast/dao/db.properties");
		String filepath = url.getPath();//得到文件绝对路径
		
		FileInputStream in = new FileInputStream(filepath);
		
		Properties prop = new Properties();  //map
		prop.load(in);
		
		String dburl = prop.getProperty("url");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");
		
		System.out.println(dburl);
		System.out.println(username);
		System.out.println(password);
	}
	
	
	//以下代码在读文件时，读到不断更新后的文件
	public void test1() throws IOException{
		ClassLoader loader = StudentDao.class.getClassLoader();
		System.out.println(loader.toString());
		//通过类加载器获得数据流
		InputStream in = loader.getResourceAsStream("itcast/dao/db.properties");
		
		Properties prop = new Properties();  //map
		prop.load(in);
		
		String url = prop.getProperty("url");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");
		
		System.out.println(url);
		System.out.println(username);
		System.out.println(password);
		
	}
	
	
	//通过类装载器读文件时，需要注意的问题——文件的更新

}
