package xyz.subho.springjwt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@RequestMapping("/")
	public String home()	{
		return "Home!";
	}
	
	@RequestMapping("/hello")
	public String hello()	{
		return "Hello World!";
	}
	
}
