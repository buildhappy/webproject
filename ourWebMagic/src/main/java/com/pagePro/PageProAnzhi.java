package com.pagePro;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class PageProAnzhi implements PageProcessor {

	Site site = Site.me().setRetryTimes(3).setCharset("utf-8").setSleepTime(2000);
	public Site getSite() {
		return site;
	}

	public void process(Page page) {
		// TODO Auto-generated method stub
		List<String> url1 = page.getHtml().links().regex("(.*anzhi\\.com/sort.*)").all();
		List<String> url2 = page.getHtml().links().regex("(.*anzhi\\.com/soft.*)").all();
		page.addTargetRequests(url1);
		page.addTargetRequests(url2);
		String name = page.getHtml().xpath("//div[@class='detail_line']/h3/text()").toString();
		String strVersion = page.getHtml().xpath("//div[@class='detail_line']/span/text()").toString();
		String version =null ;
		if(strVersion!=null){
			version =strVersion.substring(1,strVersion.length()-1);
			System.out.println(name);
		}
		page.putField("name", name);
		page.putField("version", version);
		//page.putField("downloadUrl", page.getHtml().xpath("//div[@class='detail_down']/a/").toString());
		String osStr = page.getHtml().xpath("//ul[@id='detail_line_ul']/li[5]/text()").toString();
		String os = null;
		if(osStr!=null){
			os = osStr.substring(3);
		}
		page.putField("os", os);
		System.out.println(os);
		page.setSkip(true);

	}

}
