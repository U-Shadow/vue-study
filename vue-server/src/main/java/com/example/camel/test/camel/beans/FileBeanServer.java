package com.example.camel.test.camel.beans;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;

import com.example.camel.test.utils.FileUtils;

public class FileBeanServer {

	public String orderStatus(@Body String body, @Headers Map<?, ?> headers) {
		System.out.println("----------------");
		System.out.println(body);
		String customerType = (String) headers.get("customerType");
		return customerType;
	}
	
	public Map<String, Object> fileInput(@Body String body, @Headers Map<?, ?> headers, Exchange exchange) throws IOException {
		
		// 文件路径
		String filePath  = (String) headers.get("filePath");
		byte[] content = FileUtils.readFileByBytes(filePath);
		// 变量名称
		String resVariable  = (String) headers.get("resVariable");
		Map<String, Object> map = new HashMap<>();
		map.put(resVariable, new String(content));
		return map;
	}
	
	public String fileOutput(@Body String body, @Headers Map<?, ?> headers, Exchange exchange) throws IOException {
//		JSONObject obj = JSONObject.parseObject(body);
//		byte[] content = ((String) obj.get("result")).getBytes();
//		String encoding = (String) headers.get("encoding");
//		return new String(content, encoding);
		return body;
	}
}
