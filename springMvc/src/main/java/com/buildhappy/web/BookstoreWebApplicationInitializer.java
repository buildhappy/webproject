package com.buildhappy.web;

/**
 * Configure and register a Servlet --- method 4
 * Using WebApplicationInitializer
 */
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.DispatcherServlet;

public class BookstoreWebApplicationInitializer implements WebApplicationInitializer{
	public void onStartup(ServletContext servletContext){
		ServletRegistration.Dynamic registration;
		registration = servletContext.addServlet("dispatcher", DispatcherServlet.class);
		registration.addMapping("/*");
		registration.setLoadOnStartup(1);
	}
}
