package com.buildhappy.ch2;

import com.buildhappy.ch2.inter.MessageProvider;
import com.buildhappy.ch2.inter.MessageRender;

public class HelloWorldStart {

	
	public static void main(String[] args) {
		/* version2.0
		StandardOutMessageRender stdOutMessageRender = new StandardOutMessageRender();
		HelloWorldMessage helloMessage = new HelloWorldMessage();
		stdOutMessageRender.setMessageProvider(helloMessage);
		stdOutMessageRender.render();*/
		
		/*version3.0*/
		MessageSupportFactory mesFac = MessageSupportFactory.getInstance();
		MessageProvider mesPro = mesFac.getMessageProvider();
		MessageRender mesRender = mesFac.getMessageRender();
		mesRender.setMessageProvider(mesPro);
		mesRender.render();
	}
	
}
