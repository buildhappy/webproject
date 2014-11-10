package com.pagePro;
/**
 * PageProcessor负责解析页面，抽取有用信息，以及发现新的链接。
 * WebMagic使用Jsoup作为HTML解析工具，并基于其开发了解析XPath的工具Xsoup。
 * 在这四个组件中，PageProcessor对于每个站点每个页面都不一样，是需要使用者定制的部分
 */
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class PageProGoogle implements PageProcessor {
	//部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	Site site =Site.me().setCharset("utf-8").setRetryTimes(3).setSleepTime(2000);
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	public void process(Page page) {
		//从页面发现后续的url地址来抓取
		List<String> url = page.getHtml().links().regex("(http://as\\.baidu\\.com/a/.*)").all();
		page.addTargetRequests(url);
		
		//部分二：定义如何抽取页面信息，并保存下来
		//爬虫的核心部分：从下载到的Html页面抽取到想要的信息
		//WebMagic里主要使用了三种抽取技术：XPath、正则表达式和CSS选择器
		//另外，对于JSON格式的内容，可使用JsonPath进行解析
		//page.putField("name", page.getHtml().xpath("//span[@id='appname']/text()").toString());
		Object name = page.getHtml().xpath("//div[@class='wrapper-with-footer']/div[@id='body-content']/div[@class='browse-page']/div[@class='browse-page']/div[@class='cluster-container cards-transition-enabled']/"+
				"div/div[@class='card-list']/div[@class='card no-rationale square-cover apps small']/div[@class='card-content id-track-click id-track-impression']/"+
				"div[@class='details']/a[@class='title']/");
		if(page.getResultItems().get("name")==null){
			page.setSkip(true);
		}
		//System.out.println("in pageProcess");
		page.putField("url", page.getHtml().xpath("//a[@class='bd-download event-apk-download tjitem']/@href").toString());
		page.putField("name", name);
		//部分三：从页面发现后续的url地址来抓取
//		List<String> url = page.getHtml().links().regex("(http://as\\.baidu\\.com/a/.*)").all();
//		page.addTargetRequests(url);

	}
}
