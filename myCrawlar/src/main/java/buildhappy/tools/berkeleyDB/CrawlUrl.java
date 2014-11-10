package buildhappy.tools.berkeleyDB;
/**
 * Berkeley DB存储的是一个key/value的结构，并且它们都要实现java序列化接口。
 * 该类用于构建value对象，即一个封装了很多重要属性的URL类
 */
import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;

public class CrawlUrl implements Serializable {
	private static final long serialVersionUID = 7931672194843948629L;
	
	public CrawlUrl(){
	}
	
	private String oriUrl; //原始URL的值，主机部分是域名
	
	private String url;//URL的值。主机部分是IP，为了防止重复主机的出现
	private int urlNo; //url num
	private int statusCode;//获取URL返回的结果码
	private int hitNum;//此URL被其他文章引用的次数
	private String charSet;//此URL对应文章的汉字编码
	private String abstractText;//文章摘要
	private int weight; //文章的权重(包含导向词的信息)
	private String description;//文章描述
	private int filesize;//文章大小
	private Timestamp lastUpdateTime;//最后修改的时间
	private Date timeToLive;//过期的时间
	private String title;//文章的名称
	private String type;//文章类型
	private String[] urlReference;//引用的链接
	private int layer;//爬取的层次，从种子开始，依次为第0,1,2..层

	public String getOriUrl() {
		return oriUrl;
	}
	public void setOriUrl(String oriUrl) {
		this.oriUrl = oriUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getUrlNo() {
		return urlNo;
	}
	public void setUrlNo(int urlNo) {
		this.urlNo = urlNo;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public int getHitNum() {
		return hitNum;
	}
	public void setHitNum(int hitNum) {
		this.hitNum = hitNum;
	}
	public String getCharSet() {
		return charSet;
	}
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	public String getAbstractText() {
		return abstractText;
	}
	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getFilesize() {
		return filesize;
	}
	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}
	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Date getTimeToLive() {
		return timeToLive;
	}
	public void setTimeToLive(Date timeToLive) {
		this.timeToLive = timeToLive;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String[] getUrlReference() {
		return urlReference;
	}
	public void setUrlReference(String[] urlReference) {
		this.urlReference = urlReference;
	}
	public int getLayer() {
		return layer;
	}
	public void setLayer(int layer) {
		this.layer = layer;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
