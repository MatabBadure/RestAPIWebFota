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

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_SN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_WIFI;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_LTE;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_VER;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.CRC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_DATA;


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
		if(params.get(0).getName().equalsIgnoreCase("device_model_type") && params.get(0).getValue().equalsIgnoreCase("HillRom_Monarch")){
			for(NameValuePair nameValuePair : params){
				if(DEVICE_SN.equalsIgnoreCase(nameValuePair.getName()))
					qclJsonData.put(DEVICE_SN, nameValuePair.getValue());
				if(DEVICE_WIFI.equalsIgnoreCase(nameValuePair.getName()))
					qclJsonData.put(DEVICE_WIFI, nameValuePair.getValue());	
				if(DEVICE_LTE.equalsIgnoreCase(nameValuePair.getName()))
					qclJsonData.put(DEVICE_LTE, nameValuePair.getValue());	
				if(DEVICE_VER.equalsIgnoreCase(nameValuePair.getName()))
					qclJsonData.put(DEVICE_VER, nameValuePair.getValue());	
				if(DEVICE_DATA.equalsIgnoreCase(nameValuePair.getName()))
					qclJsonData.put(DEVICE_DATA, nameValuePair.getValue());	
				if(CRC.equalsIgnoreCase(nameValuePair.getName()))
					qclJsonData.put(CRC, nameValuePair.getValue());					
				qclJsonData.put("device_model_type", "HillRom_Monarch");
			}
				//	qclJsonData.put("device_model_type", params.get(0).getValue());
				//	qclJsonData.put("device_data", params.get(1).getValue());				
		}else{
			for(NameValuePair nameValuePair : params){
				if(QCL_JSON_DATA.equalsIgnoreCase(nameValuePair.getName()))
					qclJsonData = (JSONObject) JSONValue.parse(nameValuePair.getValue());
				qclJsonData.put("device_model_type", "HillRom_Vest");
			}
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
