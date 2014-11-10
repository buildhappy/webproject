package com.pagePro;

import java.util.ArrayList;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class PagePro360 implements PageProcessor{
	Site site = Site.me().setCharset("utf-8").setRetryTimes(3).setSleepTime(2000);
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	public void process(Page page) {
		// TODO Auto-generated method stub
		List<String> url =page.getHtml().links().regex("(http://zhushou\\.360\\.cn/detail/index/soft_id/.*)").all() ;
		
		if(page.getRequest().getUrl().equals("http://zhushou.360.cn/list/index/cid/1")||
			page.getRequest().getUrl().equals("http://zhushou.360.cn/list/index/cid/2")){
			String pageStr=page.getHtml().regex("(pg\\.pageCount\\s=\\s\\w+)").toString();
			int pageCount=Integer.parseInt(pageStr.substring(15));
			List<String> url1=new ArrayList<String>();
			for(int i= 2;i<=pageCount;i++){
				url1.add(page.getRequest().getUrl()+"?page="+i);
			}
			page.addTargetRequests(url1);
		}
		page.addTargetRequests(url);
		page.putField("name", page.getHtml().xpath("//h2[@id='app-name']/span/text()").toString());
		page.putField("downloadUrl",page.getHtml().regex("(http://shouji\\.360tpcdn\\.com(/\\w*)+(\\.\\w*)*)").toString());
		String versionUrl =page.getHtml().xpath("//div[@class='base-info']/table/tbody/tr[2]/td[1]/html()").toString();
		if(versionUrl!=null){
		String ver=null;
		ver=versionUrl.substring(versionUrl.indexOf("</strong>")+"</strong>".length(), versionUrl.indexOf("<!")).trim();
		page.putField("version",ver);
		
		}
		String osUrl=page.getHtml().xpath("//div[@class='base-info']/table/tbody/tr[2]/td[2]/html()").toString();
		
		if(osUrl!=null){
		String o =null;
		o=osUrl.substring(osUrl.indexOf("</strong>")+"</strong>".length(), osUrl.length()).trim();
		page.putField("os", o);
		}
		if(page.getResultItems().get("name")==null||
				page.getResultItems().get("downloadUrl")==null||
				page.getResultItems().get("version")==null){
			page.setSkip(true);
		}
//		String url3 =page.getHtml().xpath("//div[@class='base-info']/table/tbody/tr[2]/td[1]/html()").toString();
//		System.out.println(url3);
//		if(url3!=null){
//			System.out.println(url3.indexOf("</strong>"));
//			System.out.println(url3.indexOf("<!--"));
//			String str =url3.substring(url3.indexOf("</strong>")+"</strong>".length(), url3.indexOf("<!")).trim();
//			System.out.println(str);
//		}
		
//		String s=url3.substring(url3.indexOf("</strong>"), url3.indexOf("<!"));
//		System.out.println(s);
//		System.out.println(page.getResultItems().get("downloadUrl"));
//		System.out.println(page.getResultItems().get("name"));
//		System.out.println(page.getResultItems().get("version"));
//		System.out.println(page.getResultItems().get("os"));
	}

}
