package com.pagePro;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class PagePro91 implements PageProcessor{
	Site site = Site.me().setCharset("utf-8").setRetryTimes(3).setSleepTime(2000);
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	public void process(Page page) {	
		List<String> url =page.getHtml().links().regex("(http://apk\\.91\\.com/(soft|game){1}/.*)").all() ;
		List<String> url1 =page.getHtml().links().regex("(http://apk\\.91\\.com/Soft/Android/.*)").all() ;
		//System.out.println(url);
		page.addTargetRequests(url);
		page.addTargetRequests(url1);
		page.putField("name", page.getHtml().xpath("//div[@class='s_title clearfix']/h1/html()").toString());
		page.putField("downloadUrl", page.getHtml().xpath("//div[@class='fr s_btn_box clearfix']/a/@href").toString());
		String verStr=page.getHtml().xpath("//ul[@class='s_info']/li[1]/text()").toString().trim();
		if(verStr!=null){
			page.putField("version", verStr.substring(3));
		}
		if(page.getResultItems().get("name")==null||
		   page.getResultItems().get("downloadUrl")==null||
		   page.getResultItems().get("version")==null){
			page.setSkip(true);
		}
		page.putField("os", page.getHtml().xpath("//ul[@class='s_info']/li[4]/text()").toString());
//		System.out.println("ddd");
//		System.out.println(page.getResultItems().get("name"));
//		System.out.println(page.getResultItems().get("downloadUrl"));
//		System.out.println(page.getResultItems().get("verison"));
	//page.setSkip(true);
	}

}
