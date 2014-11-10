package com.buildhappy.spitter.mvc;

import java.io.IOException;
import java.io.Writer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/register")
public class MyRegister {
	/**
	 * 将注册请求转发到register.jsp页面
	 * @return
	 */
	@RequestMapping(value="/start")
	public String welcomePage(){
		return "register";
	}
	/**
	 * 接受注册请求，将请求中的body部分传递给处理函数
	 * 注意此处一定要用post方法进行提交
	 */
	@RequestMapping(value="/body", method=RequestMethod.POST)
	public void registerUser(@RequestBody String body , Writer writer) throws IOException{
		writer.write(body);
		
	}
}
