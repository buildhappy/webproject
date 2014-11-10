package com.android.main;

/**
 * 下载app
 * 该类中用的MarketSession是源码中的MyMarketSession
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import com.android.main.SearchApp.MyApp;
import com.android.market.api.MyMarketSession;
import com.android.market.api.model.MyMarket.GetAssetResponse.InstallAsset;
import com.android.tools.Login;
import com.android.tools.PureString;

public class DownloadApp {
	private MyMarketSession session = null;
	private Login login;
	private Set<MyApp> apps;

	public DownloadApp() {
		session = new MyMarketSession(true);
		login = new Login();
		session.login(login.getEmail(), login.getPassword(),
				login.getAndroidId());
	}

	public Set<MyApp> getApps() {
		return apps;
	}

	public void setApps(Set<MyApp> apps) {
		this.apps = apps;
	}

	public void download(String... filePaths) throws Exception {
		File f = null;
		String filePath = filePaths[0];
		if(!filePath.endsWith("/")){
			filePath += "/";
		}
		if(filePath != null){
			f = new File(filePath);//可变参数可以当成数组使用
			if(!f.exists()){
				f.mkdirs();
			}
		}
		String assetId = null;
		InstallAsset ia = null;
		HttpURLConnection conn = null;
		URL url = null;
		Iterator<MyApp> it = apps.iterator();
		int i = 1;
		while(it.hasNext()){
			MyApp app = it.next();
			assetId = app.getAppId();
			
			if (session.queryGetAssetRequest(assetId).getInstallAssetCount() != 0) {
				System.out.println("第" + i + "个apk文件");
				System.out.println("assetId为：" + assetId);
				ia = session.queryGetAssetRequest(assetId).getInstallAsset(0);
				String cookieName = ia.getDownloadAuthCookieName();
				String cookieValue = ia.getDownloadAuthCookieValue();

				url = new URL(ia.getBlobUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent",
						"Android-Market/2(sapphire PLAT-RC33); gzip");
				conn.setRequestProperty("Cookie", cookieName + "="
						+ cookieValue);

				InputStream inputStream = (InputStream) conn.getInputStream();
				String fileToSave = PureString.pureString(app
						.getAppTitle() + assetId)
						+ ".apk";
				System.out.println("downloading... " + fileToSave);
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(filePath + fileToSave)));
				byte[] buf = new byte[1024];
				int k = 0;
				for (long l = 0L; (k = inputStream.read(buf)) != -1; l += k) {
					stream.write(buf, 0, k);
				}
				inputStream.close();
				stream.close();
				System.out.println(fileToSave + "  done");
				System.out.println();
				i++;
			}else{
				System.out.println("Invalid assetId! Download failed\r\n");
			}
		}
	}
}
	/*
	 * public static void main(String[] args) throws Exception{ MarketSession
	 * session = new MarketSession(true); String email = "buildhappy@gmail.com";
	 * String password = "cai545114597412";//QX-QaUxGoLhs67OCk String androidId
	 * = "328578e2e8b5d73d";//"0123456789123456 ";//"q1w2e3r4t5y6u7i8"; String
	 * assetId =
	 * "v2:org.geometerplus.zlibrary.ui.android:1:108020";//3520353580864526469
	 * ";//"3455869992251490210";9089465703133677000; session.login(email,
	 * password, androidId); //session.getContext().setAndroidId(androidId);
	 * 
	 * //System.out.println("MarketSession information");
	 * //System.out.println("\t user country : " + session.getAuthSubToken());
	 * 
	 * //wrong here InstallAsset ia =
	 * session.queryGetAssetRequest(assetId).getInstallAsset(0); String
	 * cookieName = ia.getDownloadAuthCookieName(); String cookieValue =
	 * ia.getDownloadAuthCookieValue();
	 * 
	 * URL url = new URL(ia.getBlobUrl()); HttpURLConnection conn =
	 * (HttpURLConnection)url.openConnection(); conn.setRequestMethod("GET");
	 * conn.setRequestProperty("User-Agent" ,
	 * "Android-Market/2(sapphire PLAT-RC33); gzip");
	 * conn.setRequestProperty("Cookie" , cookieName + "=" + cookieValue);
	 * 
	 * InputStream inputStream = (InputStream)conn.getInputStream(); String
	 * fileToSave ="111.apk"; System.out.println("downloading..." + fileToSave);
	 * BufferedOutputStream stream = new BufferedOutputStream(new
	 * FileOutputStream(fileToSave)); byte[] buf = new byte[1024]; int k = 0;
	 * for(long l = 0L; (k = inputStream.read(buf)) != -1; l += k){
	 * stream.write(buf , 0, k); } inputStream.close(); stream.close();
	 * System.out.println("done"); }
	 */