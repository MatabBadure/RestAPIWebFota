package com.hillrom.vest.service.util;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import javax.xml.bind.DatatypeConverter;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hillrom.vest.security.Base16Encoder;
import com.hillrom.vest.service.ChargerDataService;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.TWO_NET_PROPERTIES;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.VALUE;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.QCL_JSON_DATA;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_SN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_WIFI;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_LTE;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_VER;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.CRC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_DATA;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FRAG_TOTAL;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FRAG_CURRENT;


public class ParserUtil {
	
	private static final Logger log = LoggerFactory.getLogger(ParserUtil.class);
	
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
			qclJsonData.put("device_model_type", "HillRom_Vest");
		}
		
		return qclJsonData;
	}

	public static JSONObject getChargerJsonDataFromRawMessage(String rawMessage){

		JSONObject qclJsonData = new JSONObject();
			
		StringTokenizer st = new StringTokenizer(rawMessage, "&");
		while (st.hasMoreTokens()) {
		  String pair = st.nextToken();
		  log.debug("StringTokenizer NameValue Pair : " + pair);
		  if(pair.contains("=")){
			  StringTokenizer st_NameValue = new StringTokenizer(pair, "=");
			  String nameToken =  st_NameValue.nextToken();
			  String valueToken = st_NameValue.nextToken();
			  log.debug("StringTokenizer Name : " + nameToken);
			  log.debug("StringTokenizer Value : " + valueToken);
			  
				if(DEVICE_SN.equalsIgnoreCase(nameToken))
					qclJsonData.put(DEVICE_SN, valueToken);
				if(DEVICE_WIFI.equalsIgnoreCase(nameToken))
					qclJsonData.put(DEVICE_WIFI, valueToken);	
				if(DEVICE_LTE.equalsIgnoreCase(nameToken))
					qclJsonData.put(DEVICE_LTE, valueToken);	
				if(DEVICE_VER.equalsIgnoreCase(nameToken))
					qclJsonData.put(DEVICE_VER, valueToken);
				if(FRAG_TOTAL.equalsIgnoreCase(nameToken))
					qclJsonData.put(FRAG_TOTAL, valueToken);
				if(FRAG_CURRENT.equalsIgnoreCase(nameToken))
					qclJsonData.put(FRAG_CURRENT, valueToken);
				if(DEVICE_DATA.equalsIgnoreCase(nameToken))
					qclJsonData.put(DEVICE_DATA, valueToken);	
				if(CRC.equalsIgnoreCase(nameToken))
					qclJsonData.put(CRC, valueToken);					
				qclJsonData.put("device_model_type", "HillRom_Monarch");
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
	
	public static int[] convertToIntArray(byte[] input)
	{
	    int[] ret = new int[input.length];
	    for (int i = 0; i < input.length; i++)
	    {
	        ret[i] = input[i] & 0xff; // Range 0 to 255, not -128 to 127
	    }
	    return ret;
	}
}
