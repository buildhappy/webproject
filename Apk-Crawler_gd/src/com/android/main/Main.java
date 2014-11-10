package com.android.main;

import java.util.Iterator;
import java.util.Set;

import com.android.main.SearchApp.MyApp;

public class Main {
	public static void main(String[] args) throws Exception{
		
		String query = "RunKeeper";//"Shadow Kings";//查询条件
		SearchApp searchApp = new SearchApp();
		searchApp.setQuery(query);//查询条件
		searchApp.search();//开始查询
		Set<MyApp> apps = searchApp.search();//开始查询
		Iterator<MyApp> it = apps.iterator();
		MyApp e = null;
		System.out.println("共找到：" + apps.size() + "个文件");
		while(it.hasNext()){
			e = it.next();
			System.out.println("Id:" + e.getAppId());
			System.out.println("Title:" + e.getAppTitle());
			System.out.println("HashCode:" + e.hashCode());
			System.out.println();
		}
		/*
		DownloadApp downLoadApp = new DownloadApp();
		downLoadApp.setApps(apps);
		downLoadApp.download();*/
	
		GetAppImage getAppImage = new GetAppImage();
		getAppImage.getAppImage();
	}
}
