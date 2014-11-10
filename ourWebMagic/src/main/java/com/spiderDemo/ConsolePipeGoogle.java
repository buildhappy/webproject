package com.spiderDemo;



import java.util.Iterator;
import java.util.Set;

import com.android.main.DownloadApp;
import com.android.main.SearchApp;
import com.android.main.SearchApp.MyApp;
import com.dao.ToMysql;
import com.utils.ApkDownUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

public class ConsolePipeGoogle extends ConsolePipe implements Pipeline {
	public ConsolePipeGoogle(){
		setPath("D:/");
	}
	public ConsolePipeGoogle(String path){
		setPath("D:/"+path);
	}
	
	public void process(ResultItems result, Task task) {
		String apkName = result.get("name");
		SearchApp searchApp = new SearchApp();
		searchApp.setQuery(apkName);
		Set<MyApp> apps = searchApp.search();
//		Iterator<MyApp> it = apps.iterator();
//		MyApp e = null;
//		System.out.println("共找到：" + apps.size() + "个文件");
//		while(it.hasNext()){
//			e = it.next();
//			System.out.println("Id:" + e.getAppId());
//			System.out.println("Title:" + e.getAppTitle());
//			System.out.println("HashCode:" + e.hashCode());
//			System.out.println();
//		}
		
		DownloadApp downLoadApp = new DownloadApp();
		downLoadApp.setApps(apps);
		try{
			downLoadApp.download(this.path);
		}catch(Exception ee){
			ee.printStackTrace();
		}
	}
}
