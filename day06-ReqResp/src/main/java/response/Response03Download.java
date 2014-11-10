package response;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 实现中文文件下载
 * @author buildhappy
 *
 */
public class Response03Download extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//1.获取需要下载的文件
		String path = this.getServletContext().getRealPath("download/日本妞.jpg");//文件应放在webapp文件夹下
			//System.out.println(path);
		String filename = path.substring(path.lastIndexOf("\\")+1);
		
		//2.通知浏览器以下载方式打开  (下载的中文文件名必须要给过URL编码)
		response.setHeader("content-disposition", "attachment;filename="+URLEncoder.encode(filename, "UTF-8"));
		
		//3.向浏览器写出数据
		FileInputStream in = null;
		try{
			OutputStream out = response.getOutputStream();
			in = new FileInputStream(path);
			
			byte buffer[] = new byte[1024];
			int len = 0;
			while((len = in.read(buffer)) > 0){//从此输入流中将最多 buffer.length个字节的数据读入一个 buffer数组中,返回总字节数
				out.write(buffer,0,len);//将指定 buffer数组中从偏移量0开始的 len个字节写入此输出流
			}
		}finally{
			if(in != null){
				in.close();
			}
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
