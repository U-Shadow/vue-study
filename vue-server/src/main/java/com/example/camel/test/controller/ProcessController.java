package com.example.camel.test.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.CamelContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.example.camel.test.constants.Constants;
import com.example.camel.test.model.ResultObject;
import com.example.camel.test.utils.ActiveMqUtil;
import com.example.camel.test.utils.RequestUtil;

@RestController
@RequestMapping("/")
public class ProcessController {

	/**
	 * 根据camel文件路径,加载camel配置文件(启动camel生效)
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "resource" })
	@RequestMapping("runProcess")
	public ResultObject runProcess(HttpServletRequest request) {
		
		Map<String, Object> map = RequestUtil.paramToMap(request);
		String uri = (String) map.get("path");
		try {
			String bean = (String) map.get("porcessName");
			ApplicationContext appCtx = new FileSystemXmlApplicationContext(uri);
			CamelContext camel = (CamelContext) appCtx.getBean(bean);
			camel.start();
		} catch (BeansException e) {
			e.printStackTrace();
			return new ResultObject(Constants.RESULT_CODE_FAILED, "启动camel路由失败, 请检查camel文件路径是否正确");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*ExecutorService threadPool = Singleton.getInstance().getThreadPool();
		if(null != uri) {
			Runnable command = new Runnable() {
				@Override
				public void run() {
					ApplicationContext appCtx = new FileSystemXmlApplicationContext(uri);
				}
			};
			threadPool.execute(command);
			return "启动camel路由成功";
		}
		return "启动camel路由失败";*/
		
		return new ResultObject(Constants.RESULT_CODE_SUCCESS, "启动camel路由成功");
	}
	
	/**
	 * 根据camel文件路径和camel上下文名称,关闭camel上下文
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@RequestMapping("stopProcess")
	public ResultObject stopProcess(HttpServletRequest request) throws Exception {
		
		Map<String, Object> map = RequestUtil.paramToMap(request);
		String uri = (String) map.get("path");
		if(null != uri) {
			ApplicationContext appCtx = new FileSystemXmlApplicationContext(uri);
			String bean = (String) map.get("porcessName");
			if(null == bean) 
				return new ResultObject(Constants.RESULT_CODE_FAILED, "关闭camel失败, 没有找到对应的bean");
			CamelContext camel = (CamelContext) appCtx.getBean(bean);
			camel.stop();
			return new ResultObject(Constants.RESULT_CODE_SUCCESS, "关闭成功");
		}
		
		return new ResultObject(Constants.RESULT_CODE_FAILED, "关闭camel失败, 没找到对应的camel配置文件");
	}
	
	/**
	 * 创建队列
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "creatQueue")
	public ResultObject creatQueue(HttpServletRequest request) throws Exception {
		
		Map<String, Object> map = RequestUtil.paramToMap(request);
		String queueName = (String) map.get("queueName");
		ActiveMqUtil.creatQueue(queueName);
		
		return new ResultObject(Constants.RESULT_CODE_SUCCESS, " http组件调用测试成功", JSONObject.parseObject(JSONObject.toJSONString(map)));
	}
	
	/**
	 * http组件调用测试方法
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "httpTest")
	public ResultObject httpTest(HttpServletRequest request) throws Exception {
		
		Map<String, Object> map = RequestUtil.paramToMap(request);
		System.out.println(map.get("type"));
		
		return new ResultObject(Constants.RESULT_CODE_SUCCESS, " http组件调用测试成功", JSONObject.parseObject(JSONObject.toJSONString(map)));
	}
	
	/**
	 * JMS消息获取
	 * 从指定的JMS队列里面按照指定规则获取消息。
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "fetchJMSMessage")
	public ResultObject fetchJMSMessage(HttpServletRequest request) throws Exception {
		
		Map<String, Object> map = RequestUtil.paramToMap(request);
		System.out.println(map.get("type"));
		
		return new ResultObject(Constants.RESULT_CODE_SUCCESS, " http组件调用测试成功", JSONObject.parseObject(JSONObject.toJSONString(map)));
	}
}
