package com.example.camel.test.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class MyBookProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception{
         exchange.getOut().setBody("<html><body>Book is good</body></html>");
    }
}
