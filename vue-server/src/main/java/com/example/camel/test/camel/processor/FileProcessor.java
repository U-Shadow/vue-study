package com.example.camel.test.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.example.camel.test.utils.FileUtils;

public class FileProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		System.out.println("======== 进入file processor ========");
		String path  = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
		/*HttpServletRequest req = exchange.getIn(HttpMessage.class).getRequest();
	    String path = req.getParameter("path");*/
	    byte[] content = FileUtils.readFileByBytes(path);
		exchange.getOut().setBody(new String(content, "utf-8"));
	}

}
