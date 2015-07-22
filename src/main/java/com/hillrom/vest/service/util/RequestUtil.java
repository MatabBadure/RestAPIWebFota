package com.hillrom.vest.service.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import net.minidev.json.JSONObject;

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
}
