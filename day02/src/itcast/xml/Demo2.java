package itcast.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

public class Demo2 {

	/**
	 * 得到dom解析器
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		//DocumentBuilderFactory是一个抽象工厂类，它不能直接实例化，
		//但该类提供了一个newInstance方法 ，这个方法会根据本地平台默认安装的解析器，自动创建一个工厂的对象并返回
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		//得到dom解析器
		DocumentBuilder builder = factory.newDocumentBuilder();
		System.out.println(builder);
		
		//形成dom解析树
		Document document = builder.parse("src/book.xml");
	}

}
