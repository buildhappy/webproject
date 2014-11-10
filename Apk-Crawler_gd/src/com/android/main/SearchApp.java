package com.android.main;

import java.util.HashSet;
import java.util.Set;

import com.android.tools.Login;
import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.MarketSession.Callback;
import com.gc.android.market.api.model.Market.AppType;
import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.gc.android.market.api.model.Market.ResponseContext;

/**
 * 获取app文件信息
 * 该类中用的MarketSession是androidmarketapi-0.6.jar和protobuf-java-2.2.0.jar中的
 */
public class SearchApp {
	private Login login;
	private MarketSession session = null;
	
	private String query;//查询条件
	private Set<MyApp> apps;//查询结果
	private MyApp app = null;//查询结果
	
	
	public SearchApp(){
		apps = new HashSet<MyApp>();
		session = new MarketSession();
		login = new Login();
	}
	
	public Set<MyApp> search(){//List<App>
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
					MyApp app = new MyApp();
					app.setAppId(response.getApp(i).getId());
					app.setAppTitle(response.getApp(i).getTitle());
					app.setAppDescriptor(response.getApp(i).getDescriptor().toString());
					app.setCreatorId(response.getApp(i).getCreatorId());
					app.setAppType(response.getApp(i).getAppType());
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

	public Set<MyApp> getApps() {
		return apps;
	}

	public void setApps(Set<MyApp> apps) {
		this.apps = apps;
	}

	public class MyApp{
		private String appId;
		private String appTitle;
		private String appDescriptor;
		private String creatorId;
		private AppType appType;
		
		public MyApp(){
		}
		public AppType getAppType() {
			return appType;
		}
		public void setAppType(AppType appType) {
			this.appType = appType;
		}
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
		
		@Override
		public int hashCode(){
			return appId.hashCode() + ((appTitle==null)?0:appTitle.hashCode());
		}
		@Override
		public boolean equals(Object myApp){
			return this.hashCode() == myApp.hashCode();
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
