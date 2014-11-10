package com.pagePro;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class PageProBaidu implements PageProcessor {
	Site site =Site.me().setCharset("utf-8").setRetryTimes(3).setSleepTime(2000);
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	public void process(Page page) {
		List<String> url2 = page.getHtml().links().regex("(http://shouji\\.baidu\\.com/game/.*)").all();
		List<String> url1 = page.getHtml().links().regex("(http://shouji\\.baidu\\.com/software/.*)").all();
		List<String> url3 = page.getHtml().links().regex("(http://shouji\\.baidu\\.com/soft/.*)").all();
		page.addTargetRequests(url1);
		page.addTargetRequests(url2);
		page.addTargetRequests(url3);
		page.putField("name", page.getHtml().xpath("//h1[@class='app-name']/span/text()").toString());

		page.putField("downloadUrl", page.getHtml().xpath("//a[@class='apk']/@href").toString());
		String version = page.getHtml().xpath("//span[@class='version']/text()").toString() ;
		if(version!=null){
			//System.out.println(version.substring(4));
			page.putField("version", version.substring(4));
		}
		if(page.getResultItems().get("name")==null||page.getResultItems().get("downloadUrl")==null||page.getResultItems().get("version")==null){
			page.setSkip(true);
		}
	//	System.out.println("**************"+page.getResultItems().get("name")+"**************");

		page.putField("os", null);
	}

}
