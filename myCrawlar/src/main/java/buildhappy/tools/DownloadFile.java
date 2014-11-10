package buildhappy.tools;
/**
 * 网页的下载和处理
 * @author Administrator
 *
 */
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
public class DownloadFile {
	/**
	 * 根据URL和网页类型生成需要保存的网页的文件名，去除URL中的非文件名字符
	 */
	public String getFileNameByUrl(String url , String contentType){
		//url = "http://www.sina.com.cn/license/map2011.html";
		//移除http://
		url = url.substring(7);
		//text/html类型
		if(contentType.indexOf("html") != -1){
			url = url.replaceAll("[\\?/:*|<>\"]" , "_") + ".html";
			//System.out.println("获取的文件名为：" + url);
			return url;
		}
		//如果是application/pdf类型
		else{
			int beginIndex = contentType.lastIndexOf("/") + 1;
			url = url.replaceAll("[\\?/:*|<>\"]" , "_") + "." + contentType.substring(beginIndex);
			//System.out.println("获取的文件名为：" + url);
			return url;
		}
	}
	
	/**
	 * 保存网页字节数组到本地文件，filepath为要保存的文件的相对地址
	 */
	private void saveToLocal(byte[] data , String filePath){
		FileOutputStream fileOut = null;
		DataOutputStream dataOut = null;
		try{
			fileOut = new FileOutputStream(new File(filePath));
			dataOut = new DataOutputStream(fileOut);
			for(int i = 0; i < data.length; i++){
				dataOut.write(data[i]);
			}
			dataOut.flush();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(dataOut != null){
				try{
					dataOut.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 下载URL指定的网页
	 */
	public String downloadFile(String url){
		String filePath = null;
		//1.生成HttpClient對象并设置参数
		HttpClient client = new HttpClient();
		//设置链接超时5s
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		
		//2.生成GetMethod对象并设置参数
		GetMethod getMethod = new GetMethod(url);
		//设置get请求超时时间5s
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		//设置请求重试处理
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		
		//3.执行http get 方法
		try{
			int statusCode = client.executeMethod(getMethod);
			if(statusCode != HttpStatus.SC_OK){
				System.err.println("Method failed:" + getMethod.getStatusLine());
				filePath = null;
			}
			//4.处理HTTP响应的内容
			InputStream in = getMethod.getResponseBodyAsStream();
			BufferedInputStream buffIn = new BufferedInputStream(in);
			//byte[] responseBody = null;
			String responseBody = null;
			StringBuffer strBuff = new StringBuffer();
			byte[] b = new byte[1024];
			int readByte = 0;
			while((readByte = buffIn.read(b)) != -1){
				strBuff.append(new String(b));
			}
			responseBody = strBuff.toString();
			//buffIn.read(responseBody, off, len)
			//byte[] responseBody = getMethod.getResponseBody();
			//根据网页的url生成保存时的文件名
			filePath = "temp\\" + getFileNameByUrl(url , getMethod.getResponseHeader("Content-Type").getValue());
			System.out.println(filePath + "--size:" + responseBody.length());
			saveToLocal(responseBody.getBytes() , filePath);
		}catch(HttpException e){
			System.out.println("check your http address");
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			//释放连接
			getMethod.releaseConnection();
		}
		return filePath;
	}
}
