package com.hillrom.vest.service.util.monarch;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
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

import sun.misc.BASE64Encoder;

import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.security.Base16Encoder;
import com.hillrom.vest.service.ChargerDataService;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.CRC_FIELD_NAME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_DATA_FIELD_NAME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEV_SN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEV_VER;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEV_WIFI;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEV_LTE;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DURATION_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DURATION_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.END_BATTERY_LEVEL_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.END_BATTERY_LEVEL_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.END_TIME_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.END_TIME_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_CODE_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_CODE_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_LOG_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_LOG_START_POS;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_TIMESTAMP_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_TIMESTAMP_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FRAG_CURRENT_FIELD_NAME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FRAG_TOTAL_FIELD_NAME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FREQUENCY_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FREQUENCY_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HMR_SECONDS_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HMR_SECONDS_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.INTENSITY_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.INTENSITY_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.NUMBER_OF_EVENTS_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.NUMBER_OF_EVENTS_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.NUMBER_OF_PODS_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.NUMBER_OF_PODS_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.SESSION_INDEX_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.SESSION_INDEX_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.START_BATTERY_LEVEL_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.START_BATTERY_LEVEL_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.START_TIME_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.START_TIME_LOC;
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


public class ParserUtilMonarch {
	
	private static final Logger log = LoggerFactory.getLogger(ParserUtilMonarch.class);
	
	private ParserUtilMonarch(){
		
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

	public static JSONObject getChargerQclJsonDataFromRawMessage(String rawMessage){

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

	public static String getMonarchDeviceData(String rawMessage){
		
        byte[] b = java.util.Base64.getDecoder().decode(rawMessage);
        
        String deviceData = "";
        int start = returnMatch(b,DEVICE_DATA_FIELD_NAME);
        int end = returnMatch(b,CRC_FIELD_NAME)-CRC_FIELD_NAME.length;
        log.debug("start end : "+ start + " : " + end );
        
        byte[] deviceDataArray = new byte[end];
        int j=0;
        for(int i=start;i<end;i++) {
        	deviceDataArray[j++] = b[i];
        	int val = b[i] & 0xFF;
        	deviceData = deviceData + String.valueOf(val) + " ";
        }
        return deviceData;        
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
	
	public static String getValueFromQclJsonDataMonarch(JSONObject qclJsonDataMonarch,String key){
		String value = "";
		String valuePropertiesNew = (String) qclJsonDataMonarch.get(key);
		if(Objects.nonNull(valuePropertiesNew)){
			return valuePropertiesNew;
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
	
	
	
	public static byte[] getDevSN(String encoded_string) throws HillromException{
        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
        String sout = "";
        for(int i=0;i<b.length;i++) {
        	int val = b[i] & 0xFF;
        	sout = sout + val + " ";
        }
        
        //log.debug("Input Byte Array in devSN :"+sout);

        
        String devSN = "";
        int start = returnMatch(b,DEV_SN);
        int end = returnMatch(b,DEV_WIFI)-DEV_WIFI.length;
        log.debug("start end : "+ start + " : " + end );
        if(start < 0 || end < 0){
        	return null;
        }
        byte[] devSNArray = new byte[end];
        int j=0;
        sout = "";
        for(int i=start;i<end;i++) {
        	devSNArray[j++] = b[i];
        	int val = b[i] & 0xFF;
        	devSN = devSN + val + " ";
        }

        
        log.debug("Value of devSN : "+ devSN );
        return devSNArray;
        
	}
	
	public static byte[] getDevWifi(String encoded_string) throws HillromException{
        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
        String sout = "";
        for(int i=0;i<b.length;i++) {
        	int val = b[i] & 0xFF;
        	sout = sout + val + " ";
        }
        
        //log.debug("Input Byte Array in devWifi :"+sout);
        
        String devWifi = "";
        
        int start = returnMatch(b,DEV_WIFI);
        
        int end = returnMatch(b,DEV_VER)-DEV_VER.length;
        log.debug("start end : "+ start + " : " + end );
        if(start < 0 || end < 0){
        	return null;
        }
        byte[] devWifiArray = new byte[end];
        int j=0;
        sout = "";
        for(int i=start;i<end;i++) {
        	devWifiArray[j++] = b[i];
        	int val = b[i] & 0xFF;
        	devWifi = devWifi + val + " ";
        }
        
        log.debug("Value of devWifi : "+ devWifi );
        return devWifiArray;
        
	}
	
	public static byte[] getDevVer(String encoded_string) throws HillromException{
        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
        String sout = "";
        for(int i=0;i<b.length;i++) {
        	int val = b[i] & 0xFF;
        	sout = sout + val + " ";
        }
        
        //log.debug("Input Byte Array in devVer :"+sout);

        
        String devVer = "";
        int start = returnMatch(b,DEV_VER);
        int end = returnMatch(b,FRAG_TOTAL_FIELD_NAME)-FRAG_TOTAL_FIELD_NAME.length;
        log.debug("start end : "+ start + " : " + end );
        if(start < 0 || end < 0){
        	return null;
        }
        byte[] devVerArray = new byte[end];
        int j=0;
        sout = "";
        for(int i=start;i<end;i++) {
        	devVerArray[j++] = b[i];
        	int val = b[i] & 0xFF;
        	devVer = devVer + val + " ";
        }

        
        log.debug("Value of devVer : "+ devVer );
        return devVerArray;
        
	}
	
	
	public static String getDevWifiOrLteString(String encoded_string,int flagWifiLte) throws HillromException{
        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
        String sout = "";
        for(int i=0;i<b.length;i++) {
        	int val = b[i] & 0xFF;
        	sout = sout + val + " ";
        }
                
        String devWifiOrLte = "";
        
        // Flag 1-WIFI & 2-LTE 
        int start = flagWifiLte == 1 ? returnMatch(b,DEV_WIFI) : (flagWifiLte == 2 ? returnMatch(b,DEV_LTE) : -1);
        
        int end = returnMatch(b,DEV_VER)-DEV_VER.length;
        log.debug("start end : "+ start + " : " + end );
        if(start < 0 || end < 0){
        	return null;
        }
        byte[] devWifiOrLteArray = new byte[end];
        int j=0;
        sout = "";
        for(int i=start;i<end;i++) {
        	devWifiOrLteArray[j++] = b[i];
        	int val = b[i] & 0xFF;
        	devWifiOrLte = devWifiOrLte + val + " ";
        }

        
        log.debug("Value of devWifi : "+ devWifiOrLte );
        return devWifiOrLte;
        
	}
	
	public static String getDevVerString(String encoded_string) throws HillromException{
        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
        String sout = "";
        for(int i=0;i<b.length;i++) {
        	int val = b[i] & 0xFF;
        	sout = sout + val + " ";
        }
        
        //log.debug("Input Byte Array in devVer :"+sout);

        
        String devVer = "";
        int start = returnMatch(b,DEV_VER);
        int end = returnMatch(b,FRAG_TOTAL_FIELD_NAME)-FRAG_TOTAL_FIELD_NAME.length;
        log.debug("start end : "+ start + " : " + end );
        if(start < 0 || end < 0){
        	return null;
        }
        byte[] devVerArray = new byte[end];
        int j=0;
        sout = "";
        for(int i=start;i<end;i++) {
        	devVerArray[j++] = b[i];
        	int val = b[i] & 0xFF;
        	devVer = devVer + val + " ";
        }

        
        log.debug("Value of devVer : "+ devVer );
        return devVer;
        
	}
	
	public static int getFragTotal(String encoded_string) throws HillromException{
        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
        String sout = "";
        for(int i=0;i<b.length;i++) {
        	int val = b[i] & 0xFF;
        	sout = sout + val + " ";
        }
        
        //log.debug("Input Byte Array in getFragTotal :"+sout);

        
        int start = returnMatch(b,FRAG_TOTAL_FIELD_NAME);
        log.debug("start : "+ start  );
        if(start < 0){
        	return 0;
        }
        int fragTotal = b[start] & 0xFF;
        
        log.debug("Total number of fragments : "+ fragTotal );
        return fragTotal;
        
	}
	
	public static int getFragCurrent(String encoded_string) throws HillromException{
        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
        String sout = "";
        for(int i=0;i<b.length;i++) {
        	int val = b[i] & 0xFF;
        	sout = sout + val + " ";
        }
        
        //log.debug("Input Byte Array in getFragCurrent :"+sout);

        
        int start = returnMatch(b,FRAG_CURRENT_FIELD_NAME);
        log.debug("start : "+ start  );
        if(start < 0){
        	return 0;
        }
        int fragCurrent = b[start] & 0xFF;
        
        log.debug("Current fragment number : "+ fragCurrent );
        return fragCurrent;
        
	}
	
	public static int returnMatch(byte[] inputArray,byte[] matchArray){

        for(int i=0;i<inputArray.length;i++){
        	int val = inputArray[i] & 0xFF;
        	boolean found = false;
        	
        	if((val == 38) && !found){
        		int j=i;int k=0;
        		while((inputArray[j++]==matchArray[k++]) && (k<matchArray.length)){
        			
        		}
        		if(k==matchArray.length){
        			found = true;
        			return j;
        		}
        	}
        }
        
        return -1;
    	
    }
	
	public static int intergerCombinedFromHex(byte[] input)
	{
	    
	    String hexString =  "";
	    int hexTotal = 0;
	    for (int t = 0; t < input.length; t++)
	    {
	    	hexTotal = hexTotal + Integer.parseInt(Integer.toHexString(input[t]& 0xFF), 16);
	    }
	    log.debug("hexTotal : " + hexTotal);
	    return hexTotal;
	}
	
	  public static String getCRCChecksum(String encoded_string)
	  {

		log.debug("Inside  getCRCChecksum : " ,encoded_string);
		  
	    int nCheckSum = 0;

	    byte[] decoded = java.util.Base64.getDecoder().decode(encoded_string);
	    
	    int nDecodeCount = 0;
	    for ( ; nDecodeCount < (decoded.length-2); nDecodeCount++ )
	    {
	      int nValue = (decoded[nDecodeCount] & 0xFF);
	      nCheckSum += nValue;
	    }
	    
	    
	    System.out.format("Inverted Value = %d [0X%x] \r\n" ,nCheckSum,nCheckSum);
	    
	    while ( nCheckSum >  65535 )
	    {
	      nCheckSum -= 65535;
	    }
	    
	    int nMSB = decoded[nDecodeCount+1] & 0xFF;
	    int nLSB = decoded[nDecodeCount] & 0xFF;
	    
	    System.out.format("MSB = %d [0x%x]\r\n" ,nMSB, nMSB);
	    System.out.format("LSB = %d [0x%x]\r\n" ,nLSB, nLSB);
	    log.error("Total Value = " + nCheckSum);
	    nCheckSum = ((~nCheckSum)& 0xFFFF) + 1;
	    System.out.format("Checksum Value = %d [0X%x] \r\n" ,nCheckSum,nCheckSum);
	    
	    String msb_digit = Integer.toHexString(nMSB);
	    String lsb_digit = Integer.toHexString(nLSB);
	    String checksum_num =  Integer.toHexString(nCheckSum);
	    
	    if(msb_digit.length()<2)
	    	msb_digit = "0"+msb_digit;
	    if(lsb_digit.length()<2)
	    	lsb_digit = "0"+lsb_digit;
	    
	    System.out.println("MSB : " + msb_digit + " " +  "LSB : " + lsb_digit);
	    System.out.println("Checksum : " + checksum_num);
	    

	    return checksum_num;
		
	}
	
	
	
}
