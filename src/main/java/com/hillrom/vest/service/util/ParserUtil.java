package com.hillrom.vest.service.util;

import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class ParserUtil {
	
	private ParserUtil(){
		
	}
	
	public static String getValueFromMessage(String message, String key) {

		List<NameValuePair> params = URLEncodedUtils.parse(message, Charset.defaultCharset());
		for (NameValuePair nameValuePair : params) {
			if (nameValuePair.getName().toString().equals(key)) {
				return nameValuePair.getValue();
			}
		}
		return null;
	}


}
