package com.spiderDemo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

public class ConsolePipeline extends FilePersistentBase implements Pipeline {
	public ConsolePipeline(){
		setPath("D:\\");
	}
	public ConsolePipeline(String path){
		setPath(path);
	}
	
	public void process(ResultItems result, Task task) {
		//System.out.println("ResultItems:" + result);
		System.out.println("In ConsolePineline process");
		String name =result.get("name") ;
		String url =result.get("url");
		String fileStorePath = this.path ;
		
		System.out.println("--------------------------------------"+fileStorePath);
		
		try {
			CloseableHttpClient client=HttpClients.createDefault() ;
			String filePath = fileStorePath+"/" ;
			Path target =Paths.get(filePath);
			if(!Files.isReadable(target)){
				Files.createDirectory(target);
			}
			HttpGet httpget=new HttpGet(url) ;
			HttpResponse reponse =client.execute(httpget);
			HttpEntity entity=reponse.getEntity() ;
			InputStream in =entity.getContent() ;
			FileOutputStream out =new FileOutputStream(new File(filePath+name+".apk"));

			BufferedOutputStream buffOut = new BufferedOutputStream(out);
			BufferedInputStream buffIn = new BufferedInputStream(in);
			byte[] buf = new byte[1024];
			int len = 0;
			while((len = buffIn.read(buf)) != -1){
				buffOut.write(buf , 0 , len);
			}
			buffOut.flush();
			buffOut.close();
			buffIn.close();
		} catch (Exception e) {
		}
	}
}
