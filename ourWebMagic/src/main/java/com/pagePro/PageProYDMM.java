package com.pagePro;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;



public class PageProYDMM implements PageProcessor {
	Site site = Site.me().setCharset("utf-8").setRetryTimes(3).setSleepTime(2000);
	public Site getSite() {
		return site;
	}

	public void process(Page page) {
		// TODO Auto-generated method stub
		List<String> url = page.getHtml().xpath("//div[@class='list-page']").links().regex("(.*android/(software|game).*)").all();
		List<String> url1 = page.getHtml().links().regex("(http://mm\\.10086\\.cn/android/info/.*)").all();
		page.addTargetRequests(url);
		page.addTargetRequests(url1);
		page.putField("name", page.getHtml().xpath("//div[@class='mj_big_title font-f-yh']/span/text()").toString());	
		//System.out.println(page.getHtml().xpath("//div[@class='mj_info font-f-yh']/ul/li[7]/text()").toString());
		page.putField("downloadUrl", page.getHtml().xpath("//a[@class='mj_xzdbd']/@href").toString());
		String str_version=page.getHtml().xpath("//div[@class='mj_info font-f-yh']/ul/li[2]/text()").toString() ;
		if(str_version!=null){
		//System.out.println(str_version.substring(5));
		page.putField("version", str_version.substring(5));
		}
		
		if(page.getResultItems().get("name")==null
				||page.getResultItems().get("downloadUrl")==null
				||page.getResultItems().get("version")==null){
			page.setSkip(true);
			
		}
		String os =page.getHtml().xpath("//div[@class='mj_info font-f-yh']/ul/li[7]/text()").toString();
		if(os!=null){
		//System.out.println(os.substring(5));
		page.putField("os", os.substring(5));
		}
	}

}
