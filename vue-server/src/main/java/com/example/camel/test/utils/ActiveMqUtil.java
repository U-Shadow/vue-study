package com.example.camel.test.utils;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMqUtil {

	/** 
	 * tcp地址 
	 */
	public static final String BROKER_URL = "tcp://localhost:61616";
	
	public static void creatQueue(String desrination) throws JMSException {
		
		Connection connection = null;
		Session session = null;
	 
		try {
			// 创建链接工厂.
			ConnectionFactory factory = new ActiveMQConnectionFactory(
			ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
			// 通过工厂创建一个链接.
			connection = factory.createConnection();
			// 启动链接.
			connection.start();
			// 创建一个session会话.
			session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			// 创建一个消息队列.
			Destination destination = session.createQueue(desrination);
			// 创建消息制作者.
			MessageProducer producer = session.createProducer(destination);
			// 设置持久化模式
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
//			sendMessage(session, producer);
			// 提交会话.
			session.commit();
	 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}
}
