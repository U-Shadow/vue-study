package com.example.camel.test.camel.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Headers;

public class DatabaseBeanServer {
	
	public Map<String, Object> dataQuery(@Body String body, @Headers Map<?, ?> headers) {
		// 变量名称
		String resVariable  = (String) headers.get("resVariable");
		Map<String, Object> map = new HashMap<>();
		map.put(resVariable, body);
		return map;
	}
}
