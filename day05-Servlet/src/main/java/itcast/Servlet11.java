package itcast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用servletContext读取web工程不同位置的资源文件
 * @author buildhappy
 *
 */
public class Servlet11 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		test5();
	}
	
	//读取src下面的配置文件
	private void test5() throws IOException {
		InputStream in = this.getServletContext().getResourceAsStream("/db.properties");
		Properties prop = new Properties();  //实质上是一种map
		prop.load(in);//从数据流in中获取数据，填充到Properties中
		
		String url = prop.getProperty("url");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");
	
		
		System.out.println(url);
		System.out.println(username);
		System.out.println(password);
	}
	
	//读取src下面的配置文件(传统方式不可取)，实际的位置难确定，相对位置是相对于jvm的启动目录，即tomcat的bin目录
	private void test4() throws IOException {
		FileInputStream in = new FileInputStream("classes/db.properties");
		System.out.println(in);
	}
	
	
	//读取配置文件的第三种方式
	private void test3() throws IOException {
		ServletContext context = this.getServletContext();
		URL url = context.getResource("/resource/db.properties");
		InputStream in = url.openStream();
	}
	

	//读取配置文件的第二种方式(能够获取文件的真实路径)
	private void test2() throws IOException {
		ServletContext context = this.getServletContext();
		String realpath = context.getRealPath("/db.properties");  //返回真实路径，形式为c:\\sdsfd\sdf\db.properties
		
		//获取到操作文件名   realpath=abc.properties
		String filename = realpath.substring(realpath.lastIndexOf("\\")+1); //返回最后一个\出现的位置
		System.out.println("当前读到的文件是：" + filename);
		
		FileInputStream in = new FileInputStream(realpath);
		Properties prop = new Properties();
		prop.load(in);
		
		String url = prop.getProperty("url");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");
		
		System.out.println("文件中有如下数据：");
		System.out.println(url);
		System.out.println(username);
		System.out.println(password);
		
	}
	
	
	//读取配置文件的第一种方式(只关心数据，用此方法)
	private void test1() throws IOException {
		
		ServletContext context = this.getServletContext();
		InputStream in = context.getResourceAsStream("/db.properties");
		
		Properties prop = new Properties();  //map
		prop.load(in);
		
		String url = prop.getProperty("url");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");
		
		System.out.println(url);
		System.out.println(username);
		System.out.println(password);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
