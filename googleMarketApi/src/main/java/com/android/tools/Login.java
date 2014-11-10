package com.android.tools;

import com.android.market.api.MarketSession;

public class Login {
	//public static MarketSession session = null;
	private String email = "11buildhappy@gmail.com";
	private String password = "cai545114597412";//QX-QaUxGoLhs67OCk
	private String androidId = "328578e2e8b5d73d";//"0123456789123456 ";//"q1w2e3r4t5y6u7i8";
	
	
	public Login(){
		//session = new MarketSession(true);
		//session.login(email, password, androidId);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAndroidId() {
		return androidId;
	}

	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	}
}
