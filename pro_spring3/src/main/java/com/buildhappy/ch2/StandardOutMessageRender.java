/**
 * version2.0
 */
package com.buildhappy.ch2;

import com.buildhappy.ch2.inter.MessageProvider;
import com.buildhappy.ch2.inter.MessageRender;

public class StandardOutMessageRender implements MessageRender{
	private MessageProvider messageProvider = null;

	public void render() {
		if(this.messageProvider == null){
			throw new RuntimeException("you must set the property messageProvider of class:"
					+ StandardOutMessageRender.class.getName());
		}
		System.out.println(messageProvider.getMessage());
	}

	public MessageProvider getMessageProvider() {
		return this.messageProvider;
	}

	public void setMessageProvider(MessageProvider messageProvider) {
		this.messageProvider = messageProvider;
	}
	
	
}
