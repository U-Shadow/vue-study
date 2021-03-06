package com.example.camel.test.camel.jms.activeMQ;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 消息接受者
 * @author Administrator
 *
 */
public class MessageReceiver {
 
	/** 
	 * tcp地址 
	 */
	public static final String BROKER_URL = "tcp://localhost:61616";
	
	/** 
	 * 目标
	 */
	public static final String DESTINATION = "test";
 
	public static void main(String[] args) throws Exception {
			MessageReceiver.run();
	}
 
	public static void run() throws Exception {
 
		Connection connection = null;
		Session session = null;
	 
		try {
			// 创建链接工厂
			ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
//			ConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", BROKER_URL);
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
			Queue destination = session.createQueue(DESTINATION);
			// 创建消息消费者.
//			MessageConsumer consumer = session.createConsumer(destination);
			String str = "name = 'apusic'";
			MessageConsumer consumer = session.createConsumer(destination, str);
//			TextMessage msg = (TextMessage) consumer.receive();
//			System.out.println(msg.getText());
	 
			consumer.setMessageListener(new MessageListener() {
		 
				@Override
				public void onMessage(Message message) {
		 
					try {
						if (message instanceof TextMessage) {
							TextMessage txtMsg = (TextMessage) message;
							System.out.println(txtMsg.getStringProperty("name"));
							System.out.println("Received Text message : " + txtMsg.getText());
						} else if (message != null) {
							BytesMessage bytesMsg = (BytesMessage) message;
							byte[] bytes = new byte[(int) bytesMsg.getBodyLength()];
							bytesMsg.readBytes(bytes);
							System.out.println("Received byte message: " + new String(bytes));
						}
//						message.acknowledge();
					} catch (JMSException e) {
						e.printStackTrace();
					}
			 
				}
			});
		 
			// 通用没有具体业务意义的代码，只是为了保证主线程不退出
			synchronized (MessageReceiver.class) {
				MessageReceiver.class.wait();
			}
	 
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
	 
	}
 
}

