package com.example.camel.test.camel.jms.activeMQ;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * 从本地目录读取文件，然后发送至mq队列中
 * @author Administrator
 *
 */
public class ActiveMQCamel {
 
	private static String user = ActiveMQConnection.DEFAULT_USER;
 
	private static String password = ActiveMQConnection.DEFAULT_PASSWORD;
 	
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
 
	public static void main(String[] args) throws Exception {
		
//		PropertyConfigurator.configure("./log4j.properties");
//		PropertyConfigurator.configureAndWatch("./log4j.properties", 1000);
	 
		CamelContext context = new DefaultCamelContext();
	 
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
//		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("shihao", "shihao", url);
	 
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
	 
		System.out.println(url + " " + user + " " + password);
	 
		context.addRoutes(new RouteBuilder() {
	 
			@Override
			public void configure() throws Exception {
				// 使用Apache Camel向mq发送消息
//				from("file:/temp").to("jms:queue:hoo.mq.queue");
				
				/*
				 * 首先从mq中读取消息，然后写入到本地文件，接着用日志打印出来 
				 */
				from("jms:queue:test")
				.to("file:e:/temp")
				.to("log:activemqcamel?showExchangeId=true");
			}
		});
	 
		context.start();
	 
		// 通用没有具体业务意义的代码，只是为了保证主线程不退出
		synchronized (ActiveMQCamel.class) {
			ActiveMQCamel.class.wait();
		}
	 
	}
 
}
