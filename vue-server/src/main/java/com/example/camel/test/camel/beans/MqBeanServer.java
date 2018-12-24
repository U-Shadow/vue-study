package com.example.camel.test.camel.beans;

import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;

public class MqBeanServer {
	
	/**
	 * * JMS消息获取
	 * @param headers
	 * @param exchange
	 * @return
	 */
	public Map<String, Object> mqFetch(@Headers Map<?, ?> headers, Exchange exchange) {
		
		Map<String, Object> map = new HashMap<>();
		String data = null;
		
		String userName = ActiveMQConnection.DEFAULT_USER;
		String password = ActiveMQConnection.DEFAULT_PASSWORD;
		String anonymousConn = (String) headers.get("anonymousConn");	// 是否匿名连接
		if(anonymousConn.equals("false")) {
			userName = (String) headers.get("userName");
			password = (String) headers.get("password");
		}
		String brokerURL = (String) headers.get("brokerURL");
		ConnectionFactory factory = new ActiveMQConnectionFactory(userName, password, brokerURL);
		String queueName = (String) headers.get("queueName");
		String filter = (String) headers.get("filter");
		Integer timeout = Integer.parseInt((String) headers.get("timeout"));
		String msgDataName = (String) headers.get("msgDataName");
		try {
			data = fetchJMSMessage(factory, queueName, filter, timeout * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put(msgDataName, data);
		return map;
	}
	
	/**
	 * 从指定的JMS队列里面按照指定规则获取消息。
	 * @param factory 消息连接工厂
	 * @param queuName 队列名称
	 * @param filter 筛选规则字符串,此规则字符串为基于SQL92的查询函数
	 * @throws Exception
	 */
	public String fetchJMSMessage(ConnectionFactory factory, String queueName, String filter, long timeout) throws Exception {
		String result = null;
		Connection connection = null;
		Session session = null;
	 
		try {
			// 创建链接工厂
//			ConnectionFactory factory = new ActiveMQConnectionFactory(userName, password, brokerURL);
			// 通过工厂创建一个连接
			connection = factory.createConnection();
			// 启动链接
			connection.start();
			// 创建一个session会话.
            //第一个参数:是否支持事务，如果为true，则会忽略第二个参数，被jms服务器设置为SESSION_TRANSACTED
            //第二个参数为false时，paramB的值可为Session.AUTO_ACKNOWLEDGE，Session.CLIENT_ACKNOWLEDGE，DUPS_OK_ACKNOWLEDGE其中一个。
            //Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。哪怕是接收端发生异常，也会被当作正常发送成功。
            //Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会当作发送成功，并删除消息。
            //DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认
			session = connection.createSession(Boolean.FALSE, Session.CLIENT_ACKNOWLEDGE);
			// 创建一个消息队列.
			Queue destination = session.createQueue(queueName);
			// 创建消息消费者.
			MessageConsumer consumer = session.createConsumer(destination, filter);
			// 这中形式一次只消费一条信息
			Message message = consumer.receive(timeout);
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				result = txtMsg.getText();
			} else if (message != null) {
				BytesMessage bytesMsg = (BytesMessage) message;
				byte[] bytes = new byte[(int) bytesMsg.getBodyLength()];
				bytesMsg.readBytes(bytes);
				result = new String(bytes);
			}
			message.acknowledge();
			 
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != session) {
				session.close();
			}
			if (null != connection) {
				connection.close();
			}
		}
		
		return result;
	}
}
