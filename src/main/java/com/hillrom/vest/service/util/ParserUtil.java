package com.hillrom.vest.service.util;

import java.nio.charset.Charset;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.hillrom.vest.security.Base16Encoder;

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
}
