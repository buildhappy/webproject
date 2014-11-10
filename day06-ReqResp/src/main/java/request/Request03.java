package request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 获取客户机带过来的数据(get方式)
 * @author buildhappy
 *
 */
public class Request03 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//1.根据名称获取数据  getParameter
		//http://localhost:8080/day06/servlet/RequestDemo3?name=xxx
		String value = request.getParameter("name");
		System.out.println(value);
		
		System.out.println("-----");
		
		
		//2.获取所有的参数  getParameterNames
		//http://localhost:8080/day06/RequestDemo3?name=xxx&password=123
		Enumeration e = request.getParameterNames();
		while(e.hasMoreElements()){
			String name = (String) e.nextElement();
			value = request.getParameter(name);
			System.out.println(name + "=" + value );
		}
		
		System.out.println("----");
		
		//3.获取指定名称的参数的所有的值   getParameterValues
		//http://localhost:8080/day06/servlet/RequestDemo3?name=xxx&name=yyyy
		String values[] = request.getParameterValues("name");
		/*if(values!=null){
			for(String value1 : values){
				System.out.println(value1);
			}
		}*/
		for(int i = 0; values != null && i < values.length; i++){   //这样的代码可以预防null指针的问题
			System.out.println(values[i]);
		}
		
		
		System.out.println("--map--");
		//4.获取所有的数据    getParameterMap
		// http://localhost:8080/day06/RequestDemo3?a=1&a=2&b=1
		Map<String,String[]> map = request.getParameterMap();  //a=1&a=2&b=1
		for(Map.Entry<String, String[]> entry : map.entrySet()){
			String name  = entry.getKey();
			values = entry.getValue();   //[]
			for(int i=0; values != null && i < values.length; i++){
				value = values[i];
				System.out.println(name + "=" + value);
			}
		}
		
		//5.通过流获取
		InputStream in = request.getInputStream();  //这种方式只用在文件上传上(后面讲)
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
