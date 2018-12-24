package com.example.camel.test.service;

import javax.jms.ConnectionFactory;

public interface ProcessService {

	/**
	 * JMS消息获取
	 * 从指定的JMS队列里面按照指定规则获取消息。
	 * @param factory 消息连接工厂
	 * @param queuName 队列名称
	 * @param filter 筛选规则字符串,此规则字符串为基于SQL92的查询函数
	 * @throws Exception
	 */
	public String fetchJMSMessage(ConnectionFactory factory, String queuName, String filter)
			throws Exception;
	
}
