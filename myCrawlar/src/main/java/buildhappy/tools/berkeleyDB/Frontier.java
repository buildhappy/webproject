package buildhappy.tools.berkeleyDB;

public interface Frontier {
	public CrawlUrl getNext()throws Exception;
	public boolean putUrl(CrawlUrl url) throws Exception;
	
}
