package com.android.main;

import com.android.tools.PureString;

public class Main {
	public static void main(String[] args) throws Exception{
		String query = "Shadow Kings";//查询条件
		SearchApp searchApp = new SearchApp();
		searchApp.setQuery(query);//查询条件
		searchApp.search();//开始查询
		
		//App[] apps = new App[5];
		DownloadApp downLoadApp = new DownloadApp();
		downLoadApp.setApps(searchApp.getApps());
		downLoadApp.download();
		
	}
}
