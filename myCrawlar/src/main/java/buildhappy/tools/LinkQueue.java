package buildhappy.tools;
/**
 * 保存已经访问过的URL
 * @author Administrator
 *
 */
import java.util.HashSet;
import java.util.Set;
public class LinkQueue {
	/**
	 * 已访问过的URL集合
	 */
	private static Set<Object> visitedUrl = new HashSet<Object>(); 
	/**
	 * 待访问的URL集合
	 */
	private static Queue unVisitedUrl = new Queue();
	
	/**
	 * 获取待访问的URL队列
	 */
	public static Queue getUnVistedUrl(){
		return unVisitedUrl;
	}
	/**
	 * 添加到访问过的URL队列中
	 */
	public static void addVisitedUrl(String url){
		visitedUrl.add(url);
	}
	/**
	 * 移除访问过的URL
	 */
	public static void removeVisitedUrl(String url){
		visitedUrl.remove(url);
	}
	/**
	 * 未访问过的URL出队列
	 */
	public static Object unVisitedUrlDeQueue(){
		return unVisitedUrl.deQueue();
	}
	/**
	 * 添加到待访问队列中，保证每个URL只被访问一次
	 */
	public static void addUnvisitedUrl(String url){
		if(url != null && !url.trim().equals("") //trim去除前导空白和尾部空白
			&& !unVisitedUrl.contains(url)
			&& !visitedUrl.contains(url)){
			unVisitedUrl.enQueue(url);
		}
	}
	/**
	 * 获取已访问过的URL数目
	 */
	public static int getVisitedUrlNum(){
		return visitedUrl.size();
	}
	/**
	 * 判断未访问的URL队列是否为空
	 */
	public static boolean unVisitedUrlsEmpty(){
		return unVisitedUrl.empty();
	}
}
