package buildhappy.myCrawlar;

import java.util.Set;

import buildhappy.tools.DownloadFile;
import buildhappy.tools.HtmlParserTool;
import buildhappy.tools.LinkFilter;
import buildhappy.tools.LinkQueue;

/**
 * Hello world!
 *
 */
public class MyCrawler 
{
	/**
	 * 使用种子(初始的链接)URL队列
	 * @param args
	 */
	private void initCrawlerWithSeeds(String[] seeds){
		for(int i = 0; i < seeds.length; i++){
			LinkQueue.addUnvisitedUrl(seeds[i]);
		}
	}
	
	/**
	 * 抓取过程
	 * @param args
	 */
	public void crawling(String[] seeds){
		LinkFilter filter = new LinkFilter(){
			public boolean accept(String url){
				if(url.startsWith("http://www.iqiyi.com/"))
					return true;
				return false;
			}
		};
		//初始化URL队列
		initCrawlerWithSeeds(seeds);
		
		//循环条件：待抓取的连接不空且抓取的网页不多于1000
		while(!LinkQueue.unVisitedUrlsEmpty() && LinkQueue.getVisitedUrlNum() <= 1000){
			//队头URL出队列
			String visitUrl = (String)LinkQueue.unVisitedUrlDeQueue();
			if(visitUrl == null){
				continue;
			}
			DownloadFile downloader = new DownloadFile();
			//下载网页
			System.out.println("visitUrl:" + visitUrl);
			downloader.downloadFile(visitUrl);
			//该URL放入已访问的URL中
			LinkQueue.addVisitedUrl(visitUrl);
			//提取出下载网页中的URL
			Set<String> links = HtmlParserTool.ectracLinks(visitUrl , filter);
			//新的未访问的URL入队
			for(String link:links){
				LinkQueue.addUnvisitedUrl(link);
			}
		}
	}
	
    public static void main( String[] args )
    {
        MyCrawler crawler = new MyCrawler();
        crawler.crawling(new String[]{"http://www.iqiyi.com/"});
    }
}
