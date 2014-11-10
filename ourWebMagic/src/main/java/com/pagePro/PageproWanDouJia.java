package com.pagePro;


import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class PageproWanDouJia implements PageProcessor{
	Site site = Site.me().setRetryTimes(3).setCharset("utf-8").setSleepTime(2000);
	public Site getSite() {
		return site;
	}

	public void process(Page page) {
		List<String> url =page.getHtml().links().regex("(http://www\\.wandoujia\\.com/apps/.*)").all();
		//List<String> url =page.getHtml().links().all();
		List<String> url1 =page.getHtml().links().regex("(http://www\\.wandoujia\\.com/tag/.*)").all();
		//System.out.println(url);
		page.addTargetRequests(url);
		page.addTargetRequests(url1);
		System.out.println(page.getHtml().xpath("//p[@class='app-name']/span/text()").toString());
		page.putField("name", page.getHtml().xpath("//p[@class='app-name']/span/text()").toString());
		page.putField("downloadUrl", page.getHtml().xpath("//a[@class='install-btn']/@href").toString());
		page.putField("version", page.getHtml().xpath("//dl[@class='infos-list']/dd[4]/text()").toString());
		
		if(page.getResultItems().get("name")==null
				||page.getResultItems().get("downloadUrl")==null
				||page.getResultItems().get("version")==null){
			page.setSkip(true);
		}
		//page.setSkip(true);
		page.putField("os", page.getHtml().xpath("//dd[@class='perms']/text()").toString());
	}

}
