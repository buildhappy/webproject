package com.buildhappy.spitter.mvc;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.buildhappy.spitter.domain.Spitter;
import com.buildhappy.spitter.service.SpitterService;
/**
 * Spitter的控制器
 * @author Administrator
 */

@Controller
@RequestMapping("/spitter")
public class SpitterController {
	private final SpitterService spitterSer;
	
	@Inject
	public SpitterController(SpitterService spitterSer){
		this.spitterSer = spitterSer;
	}
	
	@RequestMapping(value="/spittles" , method=RequestMethod.GET)
	public String listSpittlesForSpitter(@RequestParam("spitter")String username , Model model){
		Spitter spitter = spitterSer.getSpitter(username);
		model.addAttribute(spitter);
		model.addAttribute(spitterSer.getSpittlesForSpitter(username));
		return "spittles/list";
	}
}
