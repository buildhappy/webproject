package com.buildhappy.web;

/**
 * Configure and register a Servlet --- method 3
 * Using ServletContainerInitializer
 */
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class BookstoreServletContainerInitializer implements ServletContainerInitializer{
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext)throws ServletException{
		ServletRegistration.Dynamic registration;
		registration = servletContext.addServlet("dispatcher", "DispatcherServlet.class");
		registration.setLoadOnStartup(1);
		registration.addMapping("/*");
	}
}
