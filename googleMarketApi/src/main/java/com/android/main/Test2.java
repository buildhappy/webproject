package com.android.main;

import com.android.market.api.MarketSession;
import com.android.market.api.MarketSession.Callback;
import com.android.market.api.model.Market.AppsRequest;
import com.android.market.api.model.Market.AppsResponse;
import com.android.market.api.model.Market.ResponseContext;

public class Test2 {

	public static void main(String[] args) {
		
		//下载app
				MarketSession session = new MarketSession(true);
				String email = "buildhappy@gmail.com";
				String password = "cai545114597412";//QX-QaUxGoLhs67OCk
				String androidId = "328578e2e8b5d73d";//"0123456789123456 ";//"q1w2e3r4t5y6u7i8";
				String assetId = "7878887740798669019";//"3455869992251490210";
				session.login(email, password, androidId);
				//session.getContext().setAndroidId(androidId);
				
				System.out.println("MarketSession information");
				System.out.println("\t AuthSubToken : " + session.getAuthSubToken());

				String query = "book";
				AppsRequest appsRequest = AppsRequest.newBuilder()
				                                .setQuery(query)
				                                .setStartIndex(0).setEntriesCount(10)
				                                .setWithExtendedInfo(true)
				                                .build();
				                       
				session.append(appsRequest, new Callback<AppsResponse>() {
				         public void onResult(ResponseContext context, AppsResponse response) {
				                  // Your code here
				                  // response.getApp(0).getCreator() ...
				                  // see AppsResponse class definition for more infos
				        	 System.out.println(response.getApp(1));
				        	 System.out.println(response.getApp(1).getId());
				        	 
				         }
				});
				session.flush();
	}

}
