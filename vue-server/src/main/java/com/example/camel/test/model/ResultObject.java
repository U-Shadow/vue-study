package com.example.camel.test.model;

import com.alibaba.fastjson.JSONObject;

public class ResultObject {
	
	private String msg;
	
	private Integer code;
	
	private JSONObject result;
	
	public ResultObject(Integer code, String msg, JSONObject result){
		this.code = code;
		this.msg = msg;
		this.result = result;
	}
	
	public ResultObject(Integer code, String msg){
		this.code = code;
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg;
	}

	public Integer getCode() {
		return code;
	}

	public JSONObject getResult() {
		return result;
	}

}
