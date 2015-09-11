package com.hillrom.vest.service.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minidev.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class RequestUtil {

	public static JSONObject checkRequiredParams(Map<String,String> paramsMap,String requiredKeys[]){
		JSONObject jsonObject = new JSONObject();
		ArrayList<String> missingParams = new ArrayList<> ();
		Set<String> keySet = paramsMap.keySet();
		for(String key : requiredKeys){
			if(!keySet.contains(key)){
				missingParams.add(key);
			}
		}
		if(missingParams.size() > 0){
			jsonObject.put("ERROR", "Missing Params : "+missingParams);
		}
		return jsonObject;
	}
	
	public static JSONObject checkRequiredParamsInQueryString(String queryString,String requiredParams[]){
		if(StringUtils.isNotBlank(queryString)){			
			Map<String,String> parameterNameValueMap = new HashMap<>();
			String nameValues[] = queryString.split("&");
			for(String nameValue : nameValues){
				String keyValues[] = nameValue.split("=");
				parameterNameValueMap.put(keyValues[0],keyValues[1]);
			}
			return checkRequiredParams(parameterNameValueMap, requiredParams);
		}else{
			JSONObject errorJson = new JSONObject();
			errorJson.put("ERROR","Missing Params : "+String.join(",", requiredParams));
			return errorJson;
		}
	} 
}
