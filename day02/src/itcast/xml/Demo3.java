package itcast.xml;

import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 增删改查操作(CRUD)
 * @author buildhappy
 *
 */
public class Demo3 {

	/**
	 * 利用dom完成文档的crud
	 * @param args
	 * @throws Exception 
	 * @throws ParserConfigurationException 
	 * @throws Exception 
	 * @throws SAXException 
	 * @throws Exception 
	 */
	private Document document;
	@Before
	public void before() throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse("src/book.xml");
	}
	
	//遍历
	@Test
	public void listXml() throws ParserConfigurationException, SAXException, Exception{
		list(document);
	}
	
	public void list(Node node){
		System.out.println(node.getNodeName());
		NodeList list = node.getChildNodes();
		for(int i=0;i<list.getLength();i++){
			Node child = list.item(i);
			list(child);    //子节点的子节点通过递归访问
		}
	}
	
	
	//读取书名节点的值：<书名>javaweb开发</书名>
	@Test
	public void test1(){
		Document document = this.document;
		Node node = document.getElementsByTagName("书名").item(0);
		String value = node.getTextContent();//获取节点的值
		System.out.println(value);
	}
	
	//读取书名属性的值：<售价 type="rmb">39.00元</售价> 
	@Test
	public void test2(){
		Document document = this.document;
		Node node = document.getElementsByTagName("售价").item(0);
		Element price =  (Element) node;   //发现node满足不了，把node强转成相应类型
		String attValue = price.getAttribute("type");
		System.out.println(attValue);
	}
	
	//向xml文档中添加售价节点
	@Test
	public void test3() throws Exception{
		Document document = this.document;
		//创建要挂的节点
		Element price = document.createElement("售价");
		price.setTextContent("59元");
		//把创建的结点挂到书节点下
		Node book = document.getElementsByTagName("书").item(0);
		book.appendChild(price);
		
		//把内存中更新后对象树，重新定回到xml文档中
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer tf = factory.newTransformer();
		tf.transform(new DOMSource(document), new StreamResult(new FileOutputStream("src/book.xml")));
	}
	
	
	//向xml文档中指定位置上添加售价节点
	@Test
	public void test4() throws Exception{
		Document document = this.document;
		
		//创建要添加的节点
		Element price = document.createElement("售价");
		price.setTextContent("59元");
		
		//得到要向哪个节点上挂子节点
		Node book = document.getElementsByTagName("书").item(0);
		
		//向参考节点前，挂新节点
		book.insertBefore(price, document.getElementsByTagName("售价").item(0));
		
		//把内存中更新后对象树，重新定回到xml文档中
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer tf = factory.newTransformer();
		tf.transform(new DOMSource(document), new StreamResult(new FileOutputStream("src/book.xml")));
		
	}
	
	//向xml文档添加节点属性
	@Test
	public void test5() throws Exception{
		Document document = this.document;
		
		//得到要添加属性的节点
		Element author = (Element) document.getElementsByTagName("作者").item(0);
		author.setAttribute("id", "12");  //向节点挂属性
		
		//把内存中更新后对象树，重新定回到xml文档中
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer tf = factory.newTransformer();
		tf.transform(new DOMSource(document), new StreamResult(new FileOutputStream("src/book.xml")));
		
	}
	
	
	//删除xml文档中的售价节点
	@Test
	public void test6() throws Exception{
		
		Document document = this.document;
		
		//得到要删除的节点
		Node price = document.getElementsByTagName("售价").item(0);
		
		//得到要删除的节点的父亲
		Node parent = document.getElementsByTagName("书").item(0);
		
		parent.removeChild(price);
		

		//把内存中更新后对象树，重新定回到xml文档中
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer tf = factory.newTransformer();
		tf.transform(new DOMSource(document), new StreamResult(new FileOutputStream("src/book.xml")));
	}
	
	//删除2   : 删除售价节点所在的书结点
	@Test
	public void test7() throws Exception{
		
		Document document = this.document;
		
		//得到要删除的节点
		Node price = document.getElementsByTagName("售价").item(0);
		price.getParentNode().getParentNode().removeChild(price.getParentNode());
		

		//把内存中更新后对象树，重新定回到xml文档中
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer tf = factory.newTransformer();
		tf.transform(new DOMSource(document), new StreamResult(new FileOutputStream("src/book.xml")));
	}
	
	
	@Test
	public void test8() throws Exception{
		Document document = this.document;
		document.getElementsByTagName("售价").item(1).setTextContent("19元");
		//把内存中更新后对象树，重新定回到xml文档中
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer tf = factory.newTransformer();
		tf.transform(new DOMSource(document), new StreamResult(new FileOutputStream("src/book.xml")));
	}

}
