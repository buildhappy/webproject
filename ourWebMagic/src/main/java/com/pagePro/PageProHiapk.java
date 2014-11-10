package com.pagePro;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class PageProHiapk implements PageProcessor {

	Site site = Site.me().setRetryTimes(3).setCharset("utf-8").setSleepTime(2000);
	public Site getSite() {
		return site;
	}

	public void process(Page page) {
		// TODO Auto-generated method stub
		//System.out.println(22);
		List<String> url1 = page.getHtml().xpath("//div[@class='page']").links().all();
		List<String> url2 = page.getHtml().links().regex("(http://apk\\.hiapk\\.com/appinfo/.*)").all();
		page.addTargetRequests(url1);
		page.addTargetRequests(url2);
		String str = page.getHtml().xpath("//div[@id='appSoftName']/html()").toString();
		//System.out.println(str);
		String name = null ;
		String version =null ;
		
		if(null!=str){
			String strname = str.trim() ;
			int start =strname.indexOf("(");
			int end =strname.indexOf(")");
			name = strname.substring(0, start);
			version =strname.substring(start+1,end);
			page.putField("name", name);
			page.putField("version", version);
		}
		page.putField("downloadUrl", page.getHtml().xpath("//a[@class='link_btn']/@href").toString());
		//System.out.println(page.getHtml().xpath("//div[@class='detail_right']/div/div[7]/span[2]/text()"));
		if(page.getResultItems().get("name")==null
				||page.getResultItems().get("downloadUrl")==null
				||page.getResultItems().get("version")==null){
			page.setSkip(true);
			
		}
		page.putField("os", page.getHtml().xpath("//div[@class='detail_right']/div/div[7]/span[2]/text()").toString());
		System.out.println(1);

	}

}
