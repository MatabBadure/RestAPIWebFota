package com.hillrom.vest.service.util;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.hillrom.vest.security.Base16Encoder;

public class ParserUtil {
	
	private ParserUtil(){
		
	}
	
	public static Map<String,String> getDecodedParamsMapFromEncodedString(String message) {
		List<NameValuePair> nameValuePairs =  URLEncodedUtils.parse(message, Charset.defaultCharset());
		Map<String,String> decodedParamsMap = new HashMap<>();
		for(NameValuePair nvp : nameValuePairs){
			decodedParamsMap.put(nvp.getName(),nvp.getValue());
		}
		return decodedParamsMap;
	}

	public static String getFieldByStartAndEndOffset(String encodedString,int startOffset,int endOffset){
		if(StringUtils.isNotBlank(encodedString)){
			if(endOffset <= encodedString.length())
				return encodedString.substring(startOffset, endOffset);
		}
		return null;
	}

	public static String convertToBase16String(String base64String) {
		byte[] decodedBytes = DatatypeConverter.parseBase64Binary(base64String);
		String base16String = Base16Encoder.encode(decodedBytes);
		return base16String;
	}
	
	public static Integer convertHexStringToInteger(String hexString){
		return Integer.parseInt(hexString, 16);
	} 
	
	public static Long convertHexStringToLong(String hexString){
		return Long.parseLong(hexString, 16);
	}
	
	public static String prepareRawMessage(Map<String,String> deviceRawLogData){
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for(String key : deviceRawLogData.keySet()){
			builder.append(key).append("=").append(deviceRawLogData.get(key));
			++i;
			if( i < deviceRawLogData.size()){
				builder.append("&");
			}
		}
		return builder.toString();
	}
}
