package com.example.camel.test.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class Singleton {
	private static Logger logger = Logger.getLogger(Singleton.class);
	
	private final ExecutorService threadPool;
	
    private Singleton(){
    	logger.info("++++++++ 创建线程池 ++++++++");
    	threadPool = Executors.newFixedThreadPool(1);
    }
    
    private static class SingletonHolder{
        private final static Singleton instance=new Singleton();
    }
    
    public static Singleton getInstance(){
        return SingletonHolder.instance;
    }
    
	public ExecutorService getThreadPool() {
		return threadPool;
	}
}
