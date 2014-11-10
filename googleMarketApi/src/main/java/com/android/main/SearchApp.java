package com.android.main;

import java.util.ArrayList;
import java.util.List;

import com.android.market.api.MarketSession;
import com.android.market.api.MarketSession.Callback;
import com.android.market.api.model.Market.AppsRequest;
import com.android.market.api.model.Market.AppsResponse;
import com.android.market.api.model.Market.ResponseContext;
import com.android.tools.Login;

/**
 * 获取app文件信息
 * 该类中用的MarketSession是androidmarketapi-0.6.jar和protobuf-java-2.2.0.jar中的
 */
public class SearchApp {
	private Login login;
	private MarketSession session = null;
	
	private String query;//查询条件
	private List<App> apps;//查询结果
	private App app = null;//查询结果
	
	
	public SearchApp(){
		apps = new ArrayList<App>();
		session = new MarketSession(true);
		login = new Login();
	}
	
	public List<App> search(){
		session.login(login.getEmail(), login.getPassword(), login.getAndroidId());
		
		AppsRequest appsRequest = AppsRequest.newBuilder().setQuery(query)
				.setStartIndex(0).setEntriesCount(10).setWithExtendedInfo(true)
				.build();

		session.append(appsRequest, new Callback<AppsResponse>() {
			public void onResult(ResponseContext context, AppsResponse response) {
				// Your code here
				// response.getApp(0).getCreator() ...
				// see AppsResponse class definition for more infos
				for(int i = 0; i < response.getAppCount(); i++){
					App app = new App();
					app.setAppId(response.getApp(i).getId());
					app.setAppTitle(response.getApp(i).getTitle());
					app.setAppDescriptor(response.getApp(i).getDescriptor().toString());
					app.setCreatorId(response.getApp(i).getCreatorId());
					apps.add(app);
				}
			}
		});
		session.flush();
		return this.apps;
	}
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List getApps() {
		return apps;
	}

	public void setApps(List<App> apps) {
		this.apps = apps;
	}

	class App{
		private String appId;
		private String appTitle;
		private String appDescriptor;
		private String creatorId;
		
		public String getAppDescriptor() {
			return appDescriptor;
		}
		public void setAppDescriptor(String appDescriptor) {
			this.appDescriptor = appDescriptor;
		}
		public String getAppId() {
			return appId;
		}
		public void setAppId(String appId) {
			this.appId = appId;
		}
		public String getAppTitle() {
			return appTitle;
		}
		public void setAppTitle(String appTitle) {
			this.appTitle = appTitle;
		}
		public String getCreatorId() {
			return creatorId;
		}
		public void setCreatorId(String creatorId) {
			this.creatorId = creatorId;
		}
	}
	
	/*
	public static void main(String[] args) {
		MarketSession session = new MarketSession();
		String email = "buildhappy@gmail.com";
		String password = "cai545114597412";
		String androidId = "328578e2e8b5d73d";
		session.login(email, password, androidId);

		String query = "Shadow Kings";
		AppsRequest appsRequest = AppsRequest.newBuilder().setQuery(query)
				.setStartIndex(0).setEntriesCount(10).setWithExtendedInfo(true)
				.build();

		session.append(appsRequest, new Callback<AppsResponse>() {
			public void onResult(ResponseContext context, AppsResponse response) {
				// Your code here
				// response.getApp(0).getCreator() ...
				// see AppsResponse class definition for more infos
				
				System.out.println(response.getAppCount());
				System.out.println(response.getApp(1));
				System.out.println(response.getApp(1).getId());
				System.out.println(response.getApp(1).getTitle());
			}
		});
		session.flush();
	}*/
}
