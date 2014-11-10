package com.pagePro;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class PagePromyapp implements PageProcessor{

	Site site =Site.me().setCharset("utf-8").setRetryTimes(3).setSleepTime(2000);
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	public void process(Page page) {

		List<String> url1 = page.getHtml().links().regex("(http://sj\\.qq\\.com/myapp/.*)").all();
		page.addTargetRequests(url1);
	
		page.putField("name", page.getHtml().xpath("//div[@class='det-name-int']/text()").toString());
			
		page.putField("downloadUrl", page.getHtml().xpath("//a[@class='det-down-btn']/@data-apkurl").toString());

		String version = page.getHtml().xpath("//div[@class='det-othinfo-data']/text()").toString() ;
		if(version!=null){
			//System.out.println(version.substring(4));
			//page.putField("version", version.substring(4));
			page.putField("version", version);
		}
		if(page.getResultItems().get("name")==null||page.getResultItems().get("downloadUrl")==null||page.getResultItems().get("version")==null){
			page.setSkip(true);
		}
		
		//System.out.println("*******name*******"+page.getResultItems().get("name")+"**************");
		//System.out.println("*******downloadUrl*******"+page.getResultItems().get("downloadUrl")+"**************");
		//System.out.println("*******version*******"+page.getResultItems().get("version")+"**************");
		page.putField("os", null);

}
}