package com.example.camel.test.camel.route;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RouteBuilder extends SpringRouteBuilder {

    @Override
    public void configure() throws Exception {
         from("file:E:/temp").to("file:E:/tmp");
    }
}
