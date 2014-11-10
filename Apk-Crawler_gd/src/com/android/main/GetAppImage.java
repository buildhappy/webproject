package com.android.main;
/**
 * 获取App的截图
 */
import java.io.FileOutputStream;
import java.util.Locale;

import com.android.market.api.MyMarketSession;
import com.android.market.api.MyMarketSession.Callback;
import com.android.market.api.model.MyMarket.GetImageRequest;
import com.android.market.api.model.MyMarket.GetImageRequest.AppImageUsage;
import com.android.market.api.model.MyMarket.GetImageResponse;
import com.android.market.api.model.MyMarket.ResponseContext;
import com.android.tools.Login;

/*
import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.MarketSession.Callback;
import com.gc.android.market.api.model.Market.GetImageRequest;
import com.gc.android.market.api.model.Market.GetImageResponse;
import com.gc.android.market.api.model.Market.GetImageRequest.AppImageUsage;
import com.gc.android.market.api.model.Market.ResponseContext;
*/
public class GetAppImage {
	private MyMarketSession session = null;
	private Login login;
	public GetAppImage(){
		session = new MyMarketSession(true);
		login = new Login();
		session.login(login.getEmail(), login.getPassword(),
				login.getAndroidId());
		session.setLocale(Locale.CANADA);
		session.getContext().setVersion(1002012);
	}
	public void getAppImage() {
		GetImageRequest imgReq = GetImageRequest.newBuilder()
				.setAppId("com.urbandroid.sleep.addon.port:1:16")
				.setImageUsage(AppImageUsage.SCREENSHOT).setImageId("1")
				.build();
		System.out.println(imgReq);
		session.append(imgReq, new Callback<GetImageResponse>() {
			@Override
			public void onResult(ResponseContext context,
					GetImageResponse response) {
				try {
					FileOutputStream fos = new FileOutputStream("icon.png");
					fos.write(response.getImageData().toByteArray());
					fos.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		session.flush();
	}
}
