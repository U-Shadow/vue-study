package com.example.camel.test.camel.processor;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
 
/**
 *
 * @author Administrator
 */
public class ReviveProcessor implements Processor {
 
    @SuppressWarnings("unchecked")
	@Override
    public void process(Exchange exchange) throws Exception {
        System.out.println("==========================");
        System.out.println(exchange.getIn().getBody(String.class));
        System.out.println("++++++++++++++++++++++++++");
        Map<String, Object> map = exchange.getIn().getBody(Map.class);//获取post ：raw
        System.out.println(map);
//        HttpServletRequest req = exchange.getIn(HttpMessage.class).getRequest();
//        String name = req.getParameter("name"); //获取post ：form-data
//        System.out.println(name);
//        exchange.getOut().removeHeaders("CamelHttp*"); //在process中也可以去掉 camle路由头信息
//        exchange.getOut().setHeader(Exchange.HTTP_METHOD, "POST"); //指定请求的方式
//        exchange.getOut().setBody(str);//camle 在路由时去掉了body参数，所以需要手动添加
        exchange.getOut().setHeader(Exchange.HTTP_QUERY, "hl=en&test=activemq"); //指定请求参数
        exchange.getOut().setBody(map); 
    }
 
}
