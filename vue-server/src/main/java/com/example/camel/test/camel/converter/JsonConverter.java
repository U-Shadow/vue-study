package com.example.camel.test.camel.converter;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConverter;

@Converter
public class JsonConverter {

	@Converter
	public String toJsonString(byte[] data, Exchange exchange) {
		TypeConverter converter = exchange.getContext().getTypeConverter();
		converter.convertTo(String.class, data);
		return "";
	}
}
