package com.buildhappy.ch2;

import com.buildhappy.ch2.inter.MessageProvider;

public class HelloWorldMessageProvider implements MessageProvider{
	public String getMessage(){
		return "hello world";
	}
}
