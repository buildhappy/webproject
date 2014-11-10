package com.spiderDemo;



import java.io.File;
import java.io.IOException;

import com.utils.ApkDownUtils;
import com.utils.HttpClientLib;
import com.utils.HBaseUtils;
import com.dao.ToMysql;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

public class ConsolePipe extends FilePersistentBase implements Pipeline {
	public ConsolePipe(){
		setPath("E://1apk//soft");//wins 该下
	}
	public ConsolePipe(String path){
		setPath("E://1apk//soft//"+path);
	}
	
	public void process(ResultItems result, Task task) {

		String name =result.get("name") ;
		String downloadUrl =result.get("downloadUrl");
		String version =result.get("version");
		String os =result.get("os");
		String sourceUrl =result.getRequest().getUrl();
		String fileStorePath = this.path;
		
		System.out.println(name+"_"+version+"_"+downloadUrl+"_"+sourceUrl+"_"+os);
		//System.out.println(fileStorePath);
		//new ToMysql().save(name, version, downloadUrl, sourceUrl, os);
		
		//ApkDownUtils.downApk(name+"_"+version, downloadUrl, fileStorePath);
		
		try {
			System.out.println("call for StoredOnHBase");
			HBaseUtils.store(name, version, os, downloadUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
