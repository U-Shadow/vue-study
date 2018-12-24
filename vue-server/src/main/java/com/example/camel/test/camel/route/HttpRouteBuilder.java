package com.example.camel.test.camel.route;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

import com.example.camel.test.camel.processor.MyBookProcessor;

@Component
public class HttpRouteBuilder extends SpringRouteBuilder{
    @Override
    public void configure() throws Exception {
        from("jetty:http://localhost:8080/myapp/myservice").process(new MyBookProcessor());
    }  
}
