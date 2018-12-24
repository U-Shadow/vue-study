package com.example.camel.test.utils;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class RequestUtil {
	
	/**
	 * 将request中的参数转换成Map<String, Object>
	 * @param request
	 * @return
	 */
    public static Map<String, Object> paramToMap(HttpServletRequest request){

        Map<String, Object> cond = new HashMap<String, Object>();
        Map<String, String[]> origin = request.getParameterMap();
        for(String key : origin.keySet()){
            if("filter".equals(key)){
                JSONObject json = JSON.parseObject(origin.get(key)[0]);
                for(String jk : json.keySet()){
                    cond.put(jk, json.get(jk));
                }
            }else{
                String[] vals = origin.get(key);
                if(vals==null || vals.length==0){
                    cond.put(key, "");
                }else if(vals.length==1){
                    //cond.put(key, vals[0]);
                    // 转义标识符列
                    cond.put(key, vals[0]);
                }else{
                    cond.put(key, vals);
                }
            }
        }
        return cond;
    }
}
