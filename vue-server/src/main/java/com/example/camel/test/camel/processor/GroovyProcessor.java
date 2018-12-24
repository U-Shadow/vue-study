package com.example.camel.test.camel.processor;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.example.camel.test.utils.ObjectUtils;

public class GroovyProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Object obj = exchange.getIn().getBody();
        Map<String, Object> map = ObjectUtils.objectToMap(obj);
        System.out.println(map);
//        exchange.getOut().removeHeaders("CamelHttp*");	// 转发到其他http调用的时候需要去除头信息中的CamelHttp*
        
        exchange.getOut().setBody(map.toString());
	}

}
