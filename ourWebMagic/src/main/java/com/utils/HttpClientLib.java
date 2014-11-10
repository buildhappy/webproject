package com.utils;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpClientLib {
	private CookieStore curCookieStore =null ;
	private HashMap<String, Long> calcTimekeyDict;
	
	 private static final String constUserAgent_IE7_x64 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E)";
	    //IE8
	  private static final String constUserAgent_IE8_x64 = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E";
	    //IE9
	  private static final String constUserAgent_IE9_x64 = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"; // x64
	  private static final String constUserAgent_IE9_x86 = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)"; // x86
	    //Chrome
	  private static final String constUserAgent_Chrome = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4";
	    //Mozilla Firefox
	  private static final String constUserAgent_Firefox = "Mozilla/5.0 (Windows; U; Windows NT 6.1; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6";
	  
	  private static String userAgent = "" ;
	  
	  public HttpClientLib(){
		  userAgent =constUserAgent_Chrome ;
		  curCookieStore = new BasicCookieStore();
		  calcTimekeyDict =new HashMap<String,Long>();
	  }
	  // start calulate time
	  public  long calcTimeStart(String uniqueKey){
		  long startMillisec = 0;
		  startMillisec =System.currentTimeMillis();
		  calcTimekeyDict.put(uniqueKey, startMillisec);
		  return startMillisec;
	  }
	  //start calulate time
	  public long calcTimeEnd(String unqueKey){
		  long endMillisec = System.currentTimeMillis();
		  long elapsedMillisec = 0;
		  if(calcTimekeyDict.containsKey(unqueKey)){
			  long startMilliSec = calcTimekeyDict.get(unqueKey);
			  elapsedMillisec=endMillisec-startMilliSec;
		  }
		  return elapsedMillisec;
	  }
	  //format date value to String 
	  public static String dateToString(Date date,String format){
		  SimpleDateFormat dateformate = new SimpleDateFormat(format);
		  String dateTime =dateformate.format(date);
		  return dateTime;
	  }
	  //get current date time string
	  public static String getCurrentDateTimeStr(){
		  String  curDateTimeStr = "" ;
		  Date curDate = new Date();
		  curDateTimeStr =dateToString(curDate, "yyyy-MM-dd_HHmmss");
		  return curDateTimeStr;
	  }
	  //
	  public static String combinePath(String path1,String path2){
		  File file1 = new File(path1);
		  File file2 = new File(file1, path2);
		  return file2.getPath() ;
	  }
	  //remove first and last quote char "
	  public static String trimQuote(String inputStr){
		  String trimedQuote = null;
		  if(null!=inputStr){
			  trimedQuote =inputStr;
			  if(trimedQuote.startsWith("\"")){
				  trimedQuote=trimedQuote.substring(1);
			  }
			  if(trimedQuote.endsWith("\"")){
				  trimedQuote=trimedQuote.substring(0,trimedQuote.length()-1);
			  }
		  }
		  return trimedQuote;
	  }
	  //????????????????????
	  public String getFileEcode(String  path) throws MalformedURLException, IOException{
		  CodepageDetectorProxy detetor=CodepageDetectorProxy.getInstance();
		  detetor.add(new ParsingDetector(false));
		  detetor.add(JChardetFacade.getInstance());
		 // JChardetFacade jCha = JChardetFacade.getInstance();
		  detetor.add(ASCIIDetector.getInstance());
		  detetor.add(UnicodeDetector.getInstance());
		  java.nio.charset.Charset charset =null;
		  File f = new File(path);
		  charset  = detetor.detectCodepage(f.toURI().toURL());
		  if(charset!=null)
			  return charset.name();
		  else 
			  return null;
		  
	  }
	  public  static String readFileContentStr(String fullFilename){
		  String  readOutStr =null;
		  
		  try {
			  DataInputStream dis = new DataInputStream(new FileInputStream(fullFilename));
			  try {
				long len = new File(fullFilename).length();
				if(len>Integer.MAX_VALUE) throw new IOException("File"+fullFilename+"to large was"+len+" bytes.");
				byte[] bytes = new byte[(int)len];
				dis.readFully(bytes);
				readOutStr =new String(bytes,"UTF-8");
			} finally{
				dis.close();
			}
		} catch (IOException e) {
			readOutStr = null;
		}
		 return readOutStr;
	  }
	  //output string into file 
	  public static boolean outputstringToFile(String strToOutput,String fullFilename){
		   boolean ouputOk =true ;
		   try {
			 File newTextFile = new File(fullFilename);
			 FileWriter fw = new FileWriter(newTextFile);
			 fw.write(strToOutput);
			 fw.close();
		} catch (IOException e) {
			// TODO: handle exception
			ouputOk = false ;
		}
		   return ouputOk;
	  }
	  
	  public void printCookies(List<Cookie> cookieList,String url){
		  if((null!=url)&&(!url.isEmpty())){
			  System.out.println("cookie for "+url);
		  }
		  for(Cookie ck:cookieList){
			  System.out.println(ck);
		  }
	  }
	  public void printCookie(CookieStore cookieStore){
		  List<Cookie> cookieList =cookieStore.getCookies();
		  printCookies(cookieList, null);
	  }
	  public void printCookie(CookieStore cookieStore,String url){
		  List<Cookie> cookieList =cookieStore.getCookies();
		  printCookies(cookieList, url);
	  }
	  public void printCookie(List<Cookie> cookie){
		  printCookies(cookie, null);
	  }
	  public CookieStore getCurCookieStore(){
		  return curCookieStore;
	  }
	  public List<Cookie> getCurCookieList(){
		  if(null!=curCookieStore){
			  return curCookieStore.getCookies();
		  }else{
			  return null;
		  }
	  }
	  public void setCurCookieStore(CookieStore cookieStore){
		  curCookieStore=cookieStore;
	  }
	  public void setCurCookieList(List<Cookie> cookieList){
		  curCookieStore.clear();
		  for(Cookie coo:cookieList){
			  curCookieStore.addCookie(coo);
		  }
	  }
	  //get httpclient
	  public CloseableHttpClient getHttpClient(int timeOut){
		  RequestConfig config= RequestConfig.custom().setConnectTimeout(timeOut)
					.build();
		  CloseableHttpClient client= HttpClientBuilder.create()
				  							.setDefaultRequestConfig(config)
				  							.build();
		  return client;
	  }
	  //get response from url
	  public HttpResponse getUrlResponse(String url,List<NameValuePair> header,List<NameValuePair> post ,int timeout){
		  HttpResponse response = null;
		 // HttpRequest request =null;
		  CloseableHttpClient client=getHttpClient(timeout);
		   HttpPost httppost =new HttpPost(url);
		  if(header!=null){
			  for(NameValuePair params:header){
				  httppost.setHeader(params.getName(), params.getValue());
			  }
		  }
		 if(post!=null){
			 try {
				HttpEntity entity =new UrlEncodedFormEntity(post);
				httppost.setEntity(entity);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		 }
		 HttpContext localContext=getHttpContext(true);
		 try {
			response=client.execute(httppost, localContext);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return response;
		  
	  }
	  public HttpResponse getUrlReponse(String url){
		 return getUrlResponse(url,null,null,0);
	  }
	  //get httpContext
	  public HttpContext getHttpContext(){
		  HttpContext localContext=new BasicHttpContext();
		  return localContext;
	  }
	  public HttpContext getHttpContext(boolean setCookieStore){

		  if(setCookieStore){
			  HttpClientContext clientContext= HttpClientContext.create();
			  clientContext.setCookieStore(curCookieStore);
			  return clientContext;
		  }else{
			  HttpContext localContext=new BasicHttpContext();
			  return localContext;
		  }
	  }
	  public String getUrlRespHtml(String url,List<NameValuePair> header,List<NameValuePair> post,int timeout,String htmlcharset){
		  String respHtml = "";
		  String defaultCharset="UTF-8";
		  if((null==htmlcharset)||htmlcharset.isEmpty()){
			  htmlcharset=defaultCharset;
		  }
		  try{
		  HttpResponse response =getUrlResponse(url, header, post, timeout);
		  if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
			  respHtml=EntityUtils.toString(response.getEntity(), htmlcharset);
		  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return respHtml;
	  }
	  public String getUrlRespHtml(String url,List<NameValuePair> header,List<NameValuePair> post,String htmlcharset){
		 return getUrlRespHtml(url, header, post, 0,htmlcharset);
	  }
	  public String getUrlRespHtml(String url,String htmlCharset){
		  return getUrlRespHtml(url, null, null, 0,htmlCharset);
	  }
	  public String getUrlRespHtml(String url){
		  String defaultCharset ="UTF-8";
		  return getUrlRespHtml(url, defaultCharset);
	  }
	
	  public Boolean downloadFile(String url,File filename,List<NameValuePair> header){
		  Boolean downloadOk =Boolean.FALSE;
		  HttpResponse response =getUrlResponse(url, header, null, 0);
		  if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
			  HttpEntity entity= response.getEntity();
			  Boolean isStream =entity.isStreaming();
			  if(isStream){
				  try {
					InputStream in =entity.getContent();
					FileOutputStream out =new FileOutputStream(filename);
					long totalSize=entity.getContentLength();
					byte[] buf = new byte[8192];
					int len =0;
					long downloadSize = 0;
					while((len = in.read(buf))>0){
						out.write(buf, 0, len);
						downloadSize+=len;
					}
					out.close();
					downloadOk=true;
					
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
				  
			  }
		  }
		  return downloadOk;
	  }
	  public  boolean downloadFile(String url,File filename){
		  return downloadFile(url, filename, null);
	  }
	  
}
