package com.hillrom.vest.service.util;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.DatatypeConverter;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.hillrom.vest.security.Base16Encoder;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.TWO_NET_PROPERTIES;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.VALUE;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.QCL_JSON_DATA;
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
		if(base64String.startsWith("2449")) // Hex Data has been passed 
			return base64String;
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
	
	public static JSONObject getQclJsonDataFromRawMessage(String rawMessage){
		List<NameValuePair> params = URLEncodedUtils.parse(rawMessage, Charset.defaultCharset());
		JSONObject qclJsonData = new JSONObject();
		for(NameValuePair nameValuePair : params){
			if(QCL_JSON_DATA.equalsIgnoreCase(nameValuePair.getName()))
				qclJsonData = (JSONObject) JSONValue.parse(nameValuePair.getValue());
		}
		return qclJsonData;
	}
	
	public static String getValueFromQclJsonData(JSONObject qclJsonData,String key){
		String value = "";
		JSONObject twoNetProperties = (JSONObject) qclJsonData.get(TWO_NET_PROPERTIES);
		if(Objects.nonNull(twoNetProperties) && twoNetProperties.keySet().contains(key)){
			JSONObject valueJson = (JSONObject) twoNetProperties.getOrDefault(key, new JSONObject());
			return valueJson.getOrDefault(VALUE, "").toString();
		}
		return value;
	}
}
