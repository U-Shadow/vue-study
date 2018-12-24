package com.example.camel.test.camel;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class CamelApplication {
	@SuppressWarnings("unused")
	private static ApplicationContext appCtx = null;
	public static void main(String[] args) throws Exception {		
		appCtx = new FileSystemXmlApplicationContext("classpath:cib/camel_20181107.xml");		
	}
}
