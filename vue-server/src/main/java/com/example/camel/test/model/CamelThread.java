package com.example.camel.test.model;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class CamelThread extends Thread {
	
	private String uri;
	
	public CamelThread(String uri) {
		this.uri = uri;
	}
	
	@Override
	public void run() {
		ApplicationContext appCtx = new FileSystemXmlApplicationContext(this.uri);
		System.out.println(3 + Thread.currentThread().getName() + ":" + Thread.currentThread().getId() + Thread.currentThread().getState());
	}
}
