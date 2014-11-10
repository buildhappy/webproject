package com.test;

/**
 * 下载app
 */
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;

import com.android.market.api.MyMarketSession;
import com.android.market.api.model.MyMarket.GetAssetResponse.InstallAsset;


public class DownloadTest {
	public static void main(String[] args) throws Exception{
		MyMarketSession session = new MyMarketSession(true);
		String email = "buildhappy@gmail.com";
		String password = "cai545114597412";//QX-QaUxGoLhs67OCk
		String androidId = "328578e2e8b5d73d";//"0123456789123456 ";//"q1w2e3r4t5y6u7i8";
		String assetId = "v2:org.geometerplus.zlibrary.ui.android:1:108020";//3520353580864526469 ";//"3455869992251490210";9089465703133677000;
		session.login(email, password, androidId);
		//session.getContext().setAndroidId(androidId);
		
		System.out.println("MarketSession information");
		System.out.println("\t user country : " + session.getAuthSubToken());
		
		/*
		GetAssetResponse response = session.queryGetAssetRequest(assetId);
		if(response != null)
			System.out.println("assetCount:"+response.getInstallAssetCount());
		else 
			System.out.println("...");
		return;
		*/
		//wrong here
		InstallAsset ia = session.queryGetAssetRequest(assetId).getInstallAsset(0);
		String cookieName = ia.getDownloadAuthCookieName();
		String cookieValue = ia.getDownloadAuthCookieValue();
		
		URL url = new URL(ia.getBlobUrl());
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent" , "Android-Market/2(sapphire PLAT-RC33); gzip");
		conn.setRequestProperty("Cookie" , cookieName + "=" + cookieValue);
		
		InputStream inputStream = (InputStream)conn.getInputStream();
		String fileToSave ="111.apk";
		System.out.println("downloading" + fileToSave);
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fileToSave));
		byte[] buf = new byte[1024];
		int k = 0;
		for(long l = 0L; (k = inputStream.read(buf)) != -1; l += k){
			stream.write(buf , 0, k);
		}
		inputStream.close();
		stream.close();
		
	}
}