package com.test;

import com.android.tools.PureString;
import com.android.main.SearchApp;
import com.android.main.SearchApp.MyApp;
public class Test {

	public static void main(String[] args) {
		String s = "344442424";
		MyApp app = new SearchApp().new MyApp();
		app.setAppId("123fa");
		//s = PureString.pureString(s);
		System.out.println(app.hashCode());
		//System.out.println(Integer.parseInt(s));
	}

}
