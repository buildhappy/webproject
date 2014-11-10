package com.spiderDemo;


import java.io.IOException;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.pagePro.AppPagepro91;
import com.pagePro.PagePro360;
import com.pagePro.PagePro91;
import com.pagePro.PageProAnzhi;
import com.pagePro.PageProBaidu;
import com.pagePro.PageProGoogle;
import com.pagePro.PageProHiapk;
import com.pagePro.PageProYDMM;
import com.pagePro.PagePromyapp;
import com.pagePro.PageproWanDouJia;
import com.utils.HBaseUtils;

public class SpiderMain {
	public static void main(String[] args) throws IOException{
		
		String url ="http://as.baidu.com";
		String url1="http://www.wandoujia.com/apps";
		String urlydmm_1="http://mm.10086.cn/android/game?pay=1";
		String urlydmm_2="http://mm.10086.cn/android/software?pay=1";
		String urlydmm_3="http://mm.10086.cn/iphone/game?pay=1";
		String urlydmm_4="http://mm.10086.cn/iphone/software?pay=1";
		String url360_1="http://zhushou.360.cn/list/index/cid/1";
		String url360_2="http://zhushou.360.cn/list/index/cid/2";
		String url91_1="http://apk.91.com/soft";
		String url91_2="http://apk.91.com/game";
		String url91_3="http://app.91.com/soft";
		String url91_4="http://app.91.com/game";
		String urlGoogle="https://play.google.com/store";
		
		String urlbaidu_1 ="http://shouji.baidu.com/software/?from=as";
		String urlbaidu_2 ="http://shouji.baidu.com/game/?from=as";
		String urlwandoujia="http://www.wandoujia.com/apps";
		String urlhiapk_1="http://apk.hiapk.com/apps" ;
		String urlhiapk_2="http://apk.hiapk.com/games" ;
		String urlanzhi_1="http://www.anzhi.com/applist.html" ;
		String urlanzhi_2="http://www.anzhi.com/gamelist.html" ;
		
		String urlmyapp="http://sj.qq.com/myapp/category.htm?orgame=1";
		
		//run(urlmyapp) ;
		String name = "微信";
		String version = "V6.0.0.54_r849063";
		HBaseUtils.get(name, version);
	}
	public static void run(String...urls){
		PageProcessor pro =null;
		ConsolePipe con = null;
		String url= urls[0] ;
		if(url.matches(".*baidu.*")){
			pro=new PageProBaidu();
			con=new ConsolePipe("apk/baidu");
			
		}else if(url.matches(".*wandoujia.*")){
			pro= new PageproWanDouJia();
			con=new ConsolePipe("apk/wandoujia");
		}else if(url.matches(".*10086.*")){
			pro = new PageProYDMM();
			con=new ConsolePipe("apk/ydmm");
		}else if(url.matches(".*360.*")){
			pro = new PagePro360();
			con=new ConsolePipe("apk/360");
		}else if(url.matches(".*apk\\.91.*")){
			pro=new PagePro91();
			con =new ConsolePipe("apk/91");
		}else if(url.matches(".*app\\.91.*")){
			pro=new AppPagepro91();
			con =new ConsolePipe();
		}else if(url.matches(".*apk\\.hiapk.*")){
			pro=new PageProHiapk();
			con =new ConsolePipe("apk/hiapk");
		}else if(url.matches(".*anzhi.*")){
			pro=new PageProAnzhi();
			con =new ConsolePipe("apk/anzhi");
		}else if(url.matches(".*qq.*")){
			pro=new PagePromyapp();
			con =new ConsolePipe("apk/myapp");
		}else if(url.matches(".*google.*")){
			pro = new PageProGoogle();
			con=new ConsolePipeGoogle("app/googleplay");
		}
		Spider.create(pro)
		.addPipeline(con)
		.addUrl(urls).thread(1).run();
	}
	
	public static void start(String url){
		
		String urlBaidu ="http://as.baidu.com";
		String url1="http://www.wandoujia.com/apps";
		String urlydmm_1="http://mm.10086.cn/android/game?pay=1";
		String urlydmm_2="http://mm.10086.cn/android/software?pay=1";
		String urlydmm_3="http://mm.10086.cn/iphone/game?pay=1";
		String urlydmm_4="http://mm.10086.cn/iphone/software?pay=1";
		String url360_1="http://zhushou.360.cn/list/index/cid/1";
		String url360_2="http://zhushou.360.cn/list/index/cid/2";
		String url91_1="http://apk.91.com/soft";
		String url91_2="http://apk.91.com/game";
		String url91_3="http://app.91.com/soft";
		String url91_4="http://app.91.com/game";
		String urlGoogle="https://play.google.com/store";
		
		run(url) ;
	}
}

