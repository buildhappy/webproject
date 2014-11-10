/**
 * version3.0
 */
package com.buildhappy.ch2;

import java.io.FileInputStream;
import java.util.Properties;

import com.buildhappy.ch2.inter.MessageProvider;
import com.buildhappy.ch2.inter.MessageRender;

public class MessageSupportFactory {
	public static MessageSupportFactory messageSupportFactory = null;
	public Properties properties = null;
	public MessageProvider messageProvider = null;
	public MessageRender messageRender = null;
	
	//单实例模式
	private MessageSupportFactory(){
		properties = new Properties();
		try{
			properties.load(new FileInputStream("msf.properties"));
			String renderClass = properties.getProperty("render.class");
			String providerClass = properties.getProperty("provider.class");
			messageRender = (MessageRender)Class.forName(renderClass).newInstance();
			messageProvider = (MessageProvider)Class.forName(providerClass).newInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	static {
		messageSupportFactory = new MessageSupportFactory();
	}
	public static MessageSupportFactory getInstance(){
		return messageSupportFactory;
	}
	public MessageProvider getMessageProvider() {
		return messageProvider;
	}

	public MessageRender getMessageRender() {
		return messageRender;
	}
}
