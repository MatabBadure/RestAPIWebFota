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
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_MODEL;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_WIFI;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_LTE;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_BT;
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

	public static Integer getNextIndex(String rawMessage,String namePair){
		switch(namePair){
		case DEVICE_MODEL:
			return rawMessage.indexOf(DEVICE_SN) < 0 ? getNextIndex(rawMessage, DEVICE_SN) :  rawMessage.indexOf(DEVICE_SN)-1;			
		case DEVICE_SN:
			return rawMessage.indexOf(DEVICE_WIFI) < 0 ? getNextIndex(rawMessage, DEVICE_WIFI) :  rawMessage.indexOf(DEVICE_WIFI)-1;
		case DEVICE_WIFI:
			return rawMessage.indexOf(DEVICE_LTE) < 0 ? getNextIndex(rawMessage, DEVICE_LTE) :  rawMessage.indexOf(DEVICE_LTE)-1;
		case DEVICE_LTE:
			return rawMessage.indexOf(DEVICE_BT) < 0 ? getNextIndex(rawMessage, DEVICE_BT) :  rawMessage.indexOf(DEVICE_BT)-1;
		case DEVICE_BT:
			return rawMessage.indexOf(DEVICE_VER) < 0 ? getNextIndex(rawMessage, DEVICE_VER) :  rawMessage.indexOf(DEVICE_VER)-1;	
		case DEVICE_VER:
			return rawMessage.indexOf(FRAG_TOTAL) < 0 ? getNextIndex(rawMessage, FRAG_TOTAL) :  rawMessage.indexOf(FRAG_TOTAL)-1;
		case FRAG_TOTAL:
			return rawMessage.indexOf(FRAG_CURRENT) < 0 ? getNextIndex(rawMessage, FRAG_CURRENT) :  rawMessage.indexOf(FRAG_CURRENT)-1;
		case FRAG_CURRENT:
			return rawMessage.indexOf(DEVICE_DATA) < 0 ? getNextIndex(rawMessage, DEVICE_DATA) :  rawMessage.indexOf(DEVICE_DATA)-1;
		case DEVICE_DATA:
			return rawMessage.indexOf(CRC) < 0 ? rawMessage.length() :  rawMessage.indexOf(CRC)-1;
		default:
			return -1;
		}		
	}
	public static JSONObject getChargerJsonDataFromRawMessage(String rawMessage){

		JSONObject qclJsonData = new JSONObject();
		
		String devModel = rawMessage.indexOf(DEVICE_MODEL) < 0 ? null : rawMessage.substring(rawMessage.indexOf(DEVICE_MODEL)+DEVICE_MODEL.length()+1, getNextIndex(rawMessage,DEVICE_MODEL));
		String devSn = rawMessage.indexOf(DEVICE_SN) < 0 ? null : rawMessage.substring(rawMessage.indexOf(DEVICE_SN)+DEVICE_SN.length()+1, getNextIndex(rawMessage,DEVICE_SN));
		String devWifi = rawMessage.indexOf(DEVICE_WIFI) < 0 ? null : rawMessage.substring(rawMessage.indexOf(DEVICE_WIFI)+DEVICE_WIFI.length()+1, getNextIndex(rawMessage,DEVICE_WIFI));
		String devLte = rawMessage.indexOf(DEVICE_LTE) < 0 ? null : rawMessage.substring(rawMessage.indexOf(DEVICE_LTE)+DEVICE_LTE.length()+1, getNextIndex(rawMessage,DEVICE_LTE));
		String devBt = rawMessage.indexOf(DEVICE_BT) < 0 ? null : rawMessage.substring(rawMessage.indexOf(DEVICE_BT)+DEVICE_BT.length()+1, getNextIndex(rawMessage,DEVICE_BT));
		String devVer = rawMessage.indexOf(DEVICE_VER) < 0 ? null : rawMessage.substring(rawMessage.indexOf(DEVICE_VER)+DEVICE_VER.length()+1, getNextIndex(rawMessage,DEVICE_VER));
		String fragTotal = rawMessage.indexOf(FRAG_TOTAL) < 0 ? null : rawMessage.substring(rawMessage.indexOf(FRAG_TOTAL)+FRAG_TOTAL.length()+1 , getNextIndex(rawMessage,FRAG_TOTAL));
		String fragCurrent = rawMessage.indexOf(FRAG_CURRENT) < 0 ? null : rawMessage.substring(rawMessage.indexOf(FRAG_CURRENT)+FRAG_CURRENT.length()+1, getNextIndex(rawMessage,FRAG_CURRENT));
		String devData = rawMessage.indexOf(DEVICE_DATA) < 0 ? null : rawMessage.substring(rawMessage.indexOf(DEVICE_DATA)+DEVICE_DATA.length()+1, getNextIndex(rawMessage,DEVICE_DATA));
		String devCrc = rawMessage.indexOf(CRC) < 0 ? null : rawMessage.substring(rawMessage.indexOf(CRC)+CRC.length()+1, rawMessage.length());
		
		if(Objects.nonNull(devModel)){
			qclJsonData.put(DEVICE_MODEL, devModel);
		}
		if(Objects.nonNull(devSn)){
			qclJsonData.put(DEVICE_SN, devSn);
		}
		if(Objects.nonNull(devWifi)){
			qclJsonData.put(DEVICE_WIFI, devWifi);
		}
		if(Objects.nonNull(devLte)){
			qclJsonData.put(DEVICE_LTE, devLte);
		}
		if(Objects.nonNull(devBt)){
			qclJsonData.put(DEVICE_BT, devBt);
		}
		if(Objects.nonNull(devVer)){
			qclJsonData.put(DEVICE_VER, devVer);
		}
		if(Objects.nonNull(fragTotal)){			
			qclJsonData.put(FRAG_TOTAL, fragTotal);
		}
		if(Objects.nonNull(fragCurrent)){			
			qclJsonData.put(FRAG_CURRENT, fragCurrent);
		}
		if(Objects.nonNull(devData)){
			qclJsonData.put(DEVICE_DATA, devData);
		}
		if(Objects.nonNull(devCrc)){
			qclJsonData.put(CRC, devCrc);
		}
		
		
		/*
		StringTokenizer st = new StringTokenizer(rawMessage, "&");
		while (st.hasMoreTokens()) {
		  String pair = st.nextToken();
		  log.debug("StringTokenizer NameValue Pair : " + pair);
		  if(pair.contains("=")){
			  StringTokenizer st_NameValue = new StringTokenizer(pair, "=");
			  String nameToken =  st_NameValue.nextToken();

			  String valueToken;
			  if(DEVICE_SN.equalsIgnoreCase(nameToken) || DEVICE_WIFI.equalsIgnoreCase(nameToken) || DEVICE_LTE.equalsIgnoreCase(nameToken)
					  || DEVICE_VER.equalsIgnoreCase(nameToken) || FRAG_TOTAL.equalsIgnoreCase(nameToken) || FRAG_CURRENT.equalsIgnoreCase(nameToken) ||
					  DEVICE_DATA.equalsIgnoreCase(nameToken) || CRC.equalsIgnoreCase(nameToken)){
						  valueToken = st_NameValue.nextToken();
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
			  }else{
				  
				  valueToken = st_NameValue.nextToken();
				  log.debug("StringTokenizer Name : " + nameToken);
				  log.debug("StringTokenizer Value : " + valueToken);
				  
			  }
				

		  }
		}*/
			
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
