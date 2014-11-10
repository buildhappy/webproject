package com.pagePro;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class AppPagepro91 implements PageProcessor{
	Site site =Site.me().setRetryTimes(3).setCharset("utf-8").setSleepTime(2000);
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	public void process(Page page) {
		// TODO Auto-generated method stub
		List<String> url =page.getHtml().xpath("//div[@id=ndNetPager]").links().regex("(.*(soft|game).*)").all() ;
		List<String> url1 =page.getHtml().links().regex("(http://app\\.91\\.com/Soft/iPhone/.*)").all() ;
		page.addTargetRequests(url1);
		page.addTargetRequests(url);
		System.out.println(11);
		page.putField("name", page.getHtml().xpath("//div[@class='s_title clearfix']/h1/html()").toString());
		page.putField("downloadUrl", page.getHtml().xpath("//a[@class='s_btn s_btn4']/@href").toString());
		String verStr=page.getHtml().xpath("//ul[@class='s_info']/li[1]/text()").toString();
		if(verStr!=null){
			page.putField("version", verStr.substring(3));
	}
		if(page.getResultItems().get("name")==null||
		   page.getResultItems().get("downloadUrl")==null||
		   page.getResultItems().get("version")==null){
			page.setSkip(true);
		}
		page.putField("os", page.getHtml().xpath("//ul[@class='s_info']/li[4]/text()").toString());

	}

}
