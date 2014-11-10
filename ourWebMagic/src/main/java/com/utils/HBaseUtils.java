package com.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 将app文件存储到HBase数据库中
 * @author Administrator
 *
 */
public class HBaseUtils {
	private static Configuration conf = null;
	
	static{
		conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "10.108.112.181");
		conf.set("hbase.zookeeper.property.clientPort", "2222");
		conf.set("hbase.master", "10.108.112.181");
		try{
			HBaseHelper helper = HBaseHelper.getHelper(conf);
			if(!helper.exitsTable("webApps")){//检查是否存在webApp数据库，不存在则创建
				helper.createTable("webApps", "info" , "source");//创建数据库，info和source两个列族
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("in StoredOnHBase static");
	}
	/**
	 * 通过抓取的url建立下载app的数据流，将该数据流保存到HBase中
	 * @param name    app应用名
	 * @param version app版本
	 * @param  os     app使用的操作系统
	 * @param url     抓取到的app下载路径
	 * @throws IOException 
	 */
	public static void store(String name , String version , String os , String url) throws IOException{
		//System.out.println("name:" + name + "  version:" + version + " os:" + os + " url:" + url);
		HTable table = new HTable(conf , "webApps");
		Put put = new Put(Bytes.toBytes(name + version));
		put.add(Bytes.toBytes("info") , Bytes.toBytes("name") , Bytes.toBytes(name));
		put.add(Bytes.toBytes("info") , Bytes.toBytes("version") , Bytes.toBytes(version));
		//put.add(Bytes.toBytes("info") , Bytes.toBytes("os") , Bytes.toBytes(os.toString()));
		put.add(Bytes.toBytes("info") , Bytes.toBytes("url") , Bytes.toBytes(url));
		
		//建立连接获取数据流
		
		CloseableHttpClient client = HttpClients.createDefault();
		
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		InputStream in;
		//synchronized(HBaseUtils.class){
		in = entity.getContent();
		in.available();
		BufferedInputStream bufIn = new BufferedInputStream(in);
		byte[] appBuf = new byte[1024];//count
		//将inputStream存到字节数组中
		ByteArrayOutputStream byteArrStr = new ByteArrayOutputStream();
		int len = 0;
		int count = 0;
		while((len = bufIn.read(appBuf)) != -1){
			count++;
			byteArrStr.write(appBuf , 0 , len);
		}
		//len = bufIn.read(appBuf);
		
		bufIn.close();
		byte[] app = byteArrStr.toByteArray();
		put.add(Bytes.toBytes("source") , Bytes.toBytes("source") , app);
		System.out.println(app.length + "****************");
		System.out.println("count:" + count);
		//}
		
		
		//????????
		boolean res = table.checkAndPut(Bytes.toBytes(name + version), Bytes.toBytes("info"), 
				      Bytes.toBytes("name"), null, put);
		//table.put(put);
	}
	
	/**
	 * 根据应用名和版本，从HBase中获取应用
	 * @throws IOException 
	 */
	public static void get(String name , String version) throws IOException{
		//从HBase中获取app的数据流保存在byte数组中
		HTable table = new HTable(conf , "webApps");
		Get get = new Get(Bytes.toBytes(name + version));
		get.addColumn(Bytes.toBytes("info"), Bytes.toBytes("url"));
		get.addColumn(Bytes.toBytes("source"), Bytes.toBytes("source"));
		Result res = table.get(get);
		byte[] source = res.getValue(Bytes.toBytes("source"), Bytes.toBytes("source"));
		System.out.println(source.length);
		//将app文件保存到本地
		File file = new File(name + version + ".apk");
		FileOutputStream fout = new FileOutputStream(file);
		System.out.println("source:" + source.length);
		fout.write(source);
		fout.close();
	}
}
