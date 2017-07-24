package com.hillrom.vest.service.FOTA;

import static com.hillrom.vest.config.FOTA.FOTAConstants.AMPERSAND;
import static com.hillrom.vest.config.FOTA.FOTAConstants.BUFFER_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.BUFFER_LEN_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CRC;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CRC_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HANDLE_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HEXAFILEPATH;
import static com.hillrom.vest.config.FOTA.FOTAConstants.INIT;
import static com.hillrom.vest.config.FOTA.FOTAConstants.NOT_OK;
import static com.hillrom.vest.config.FOTA.FOTAConstants.OK;
import static com.hillrom.vest.config.FOTA.FOTAConstants.PREV_REQ_STATUS;
import static com.hillrom.vest.config.FOTA.FOTAConstants.REQUEST_TYPE;
import static com.hillrom.vest.config.FOTA.FOTAConstants.REQUEST_TYPE1;
import static com.hillrom.vest.config.FOTA.FOTAConstants.REQUEST_TYPE2;
import static com.hillrom.vest.config.FOTA.FOTAConstants.REQUEST_TYPE3;
import static com.hillrom.vest.config.FOTA.FOTAConstants.RESULT;
import static com.hillrom.vest.config.FOTA.FOTAConstants.RESULT_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.TOTAL_CHUNK;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import net.minidev.json.JSONObject;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.service.util.FOTA.FOTAParseUtil;
@Service
@Transactional
public class FOTAService {

	private final Logger log = LoggerFactory.getLogger(FOTAService.class);
	/*private static Map<Long,String> storeChunk ;
	private static Map<Long,String> handleHolder ;*/
	private  Map<Long,String> storeChunk ;
	private  Map<Long,String> handleHolder ;
	
	public static final byte[] CRC_FIELD_NAME = new byte[]{38,99,114,99,61};
	public static final byte[] CHUNK_SIZE = new byte[]{38,99,104,117,110,107,83,105,122,101,61};
	public static final byte[] HANDLE = new byte[]{38,104,97,110,100,108,101,61};
	
	
	private long handleHolderCount ;
	
	private int bufferLen = 0;
	
	private String buffer = null;
	
	
	public String FOTAUpdate(String rawMessage) {

		String decoded_string = "";

		StringBuilder responseString = new StringBuilder();
		String finalString = new String();

		Map<String, String> fotaJsonData = new LinkedHashMap<String, String>();

		// Decoding raw data
		decoded_string = decodeRawMessage(rawMessage);
		// Parsing into key value pair
		fotaJsonData = FOTAParseUtil
				.getFOTAJsonDataFromRawMessage(decoded_string);

		// Checking if request Type is 01 & //Checking if request Type is 02
		
		if(fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE1)){
			long totalChunk = 0L;
			int handleValue = 0;
			handleHolder = new LinkedHashMap<Long, String>();
			log.error("totalChunk: " + handleHolder.size());
			totalChunk = readHexByteDataFromFile();
			log.error("totalChunk: " + totalChunk);
			
			responseString.append(RESULT_EQ);
			responseString.append("Yes");
			responseString.append(AMPERSAND);
			responseString.append(HANDLE_EQ);
			String strResponseString = asciiToHex(responseString.toString());
			log.error("strResponseString: " + strResponseString);
			
			//Converting raw for handle value
			//$ random generator;
			//handleValue = (int)(Math.random()*9000)+1000;
			Random random = new Random();
	        handleValue = random.nextInt();
	        String strHandleValue =  hexToAscii(asciiToHex(Integer.toHexString(handleValue)));
			log.error("strHandleValue: " + strHandleValue);
			
			StringBuilder responseString1 = new StringBuilder();
			responseString1.append(AMPERSAND);
			responseString1.append(TOTAL_CHUNK);
			String strResponseString1 = asciiToHex(responseString1.toString());
			log.error("strResponseString1: " + strResponseString1);
			
			//Converting raw for Total chunk value
			BigInteger toHex=new BigInteger(String.valueOf(totalChunk),10);
		    String totalChunkHexString =toHex.toString(16);
		    totalChunkHexString= ("00000000" + totalChunkHexString).substring(totalChunkHexString.length());
			String strTotalChunk = hexToAscii(asciiToHex(totalChunkHexString));
			log.error("strTotalChunk: " + strTotalChunk);
			
			StringBuilder responseString2 = new StringBuilder();
			responseString2.append(AMPERSAND);
			responseString2.append(CRC_EQ);
			String strResponseString2 = asciiToHex(responseString2.toString());
			log.error("strResponseString2: " + strResponseString2);
			
					
			 byte[] getCRCByte = java.util.Base64.getDecoder().decode(rawMessage);
			 int start = returnMatch(getCRCByte,CRC_FIELD_NAME);
			 int start1 = returnMatch(getCRCByte,CRC_FIELD_NAME)+1;
			 StringBuilder crcHexBilder = new StringBuilder();
			 
			 crcHexBilder.append(Integer.toHexString(getCRCByte[start]& 0xFF));
			 crcHexBilder.append(Integer.toHexString(getCRCByte[start1]& 0xFF));
			
			 log.error("crcHexBilder: " + crcHexBilder);
			 String crcHexString = hexToAscii(asciiToHex(crcHexBilder.toString()));
			 log.error("crcHexString: " + crcHexString);
			 
			finalString = strResponseString.concat(strHandleValue).concat(strResponseString1).concat(strTotalChunk).concat(strResponseString2).concat(crcHexString);
			log.error("finalString: " + finalString);
			
		
		}else if(fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE2)){
			
			if (fotaJsonData.get(PREV_REQ_STATUS).equals(INIT)) {
				log.debug("0th Chunk :" + storeChunk.get(0L));
				buffer = hexToAscii(asciiToHex(storeChunk.get(0L)));
				log.debug("buffer Encoded:" + buffer);
				bufferLen = storeChunk.get(0L).length() / 2;
				handleHolderCount = 0L;
				log.debug("handleHolderCount:" + handleHolderCount);

			}else if (fotaJsonData.get(PREV_REQ_STATUS).equals(OK)) {
				handleHolderCount = handleHolderCount + 1;
				handleHolder.put(handleHolderCount, fotaJsonData.get(PREV_REQ_STATUS));
				log.debug("Chunk By Chunk :" + storeChunk.get(handleHolderCount));
				buffer = hexToAscii(asciiToHex(storeChunk.get(handleHolderCount)));
				log.debug("buffer Encoded:" + buffer);
				bufferLen = storeChunk.get(handleHolderCount).length() / 2;
				log.debug("handleHolderCount:" + handleHolderCount);
				
			} else if (fotaJsonData.get(PREV_REQ_STATUS).equals(NOT_OK)) {
				
				handleHolderCount = handleHolderCount-1;
				handleHolder.put(handleHolderCount, fotaJsonData.get(PREV_REQ_STATUS));
				log.debug("Chunk By Chunk :" + storeChunk.get(handleHolderCount));
				buffer = hexToAscii(asciiToHex(storeChunk.get(handleHolderCount)));
				log.debug("buffer Encoded:" + buffer);
				bufferLen = storeChunk.get(handleHolderCount).length() / 2;
				log.debug("handleHolderCount:" + handleHolderCount);

			}
			responseString.append(HANDLE_EQ);
			String str = asciiToHex(responseString.toString());
			
			byte[] getCRCByte1 = java.util.Base64.getDecoder().decode(rawMessage);
			 int handleIndex = returnMatch(getCRCByte1,HANDLE);
				log.error("str1: " + handleIndex);
			StringBuilder handleRes = new StringBuilder();	
			//handleRes.
			handleRes.append(Integer.toHexString(getCRCByte1[handleIndex]& 0xFF));
			handleRes.append(Integer.toHexString(getCRCByte1[handleIndex+1]& 0xFF));
			handleRes.append(Integer.toHexString(getCRCByte1[handleIndex+2]& 0xFF));
			handleRes.append(Integer.toHexString(getCRCByte1[handleIndex+3]& 0xFF));
			
			String handleString = hexToAscii(asciiToHex(handleRes.toString()));	
			//responseString.append(handle);
			
			StringBuilder responseString1 = new StringBuilder();	
			responseString1.append(AMPERSAND);
			responseString1.append(BUFFER_LEN_EQ);
			String str1 = asciiToHex(responseString1.toString());
			
			String bufferLen =  hexToAscii(asciiToHex(Integer.toHexString(128)));
			
			//responseString1.append(bufferLen);
			
			StringBuilder responseString2 = new StringBuilder();
			responseString2.append(AMPERSAND);
			responseString2.append(BUFFER_EQ);
			
			String str2 = asciiToHex(responseString2.toString());
			
			log.error("str1: " + str1);
			
			log.error("buffer: " + buffer);
			StringBuilder responseString3 = new StringBuilder();
			
			responseString3.append(AMPERSAND);
			responseString3.append(CRC_EQ);
			
			String str3 = asciiToHex(responseString3.toString());
			
			 byte[] getCRCByte = java.util.Base64.getDecoder().decode(rawMessage);
			 int crcIndex1 = returnMatch(getCRCByte,CRC_FIELD_NAME);
			 int crcIndex2 = returnMatch(getCRCByte,CRC_FIELD_NAME)+1;
			 StringBuilder crcHexBilder = new StringBuilder();
			 
			 crcHexBilder.append(Integer.toHexString(getCRCByte[crcIndex1]& 0xFF));
			 crcHexBilder.append(Integer.toHexString(getCRCByte[crcIndex2]& 0xFF));
			
			
			String crcHexString = hexToAscii(asciiToHex(crcHexBilder.toString()));
			
			log.error("crcHexString: " + crcHexString);
			
			log.error("responseString: " + responseString);
			//Final String 
			finalString = str.concat(handleString).concat(str1).concat(bufferLen).concat(str2).concat(buffer).concat(str3).concat(crcHexString);
		
		} else if (fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE3)) {
			if (fotaJsonData.get(RESULT).equals(OK)) {
				responseString.append("Download completed");
				//Final String 
				finalString = responseString.toString();
			} else if (fotaJsonData.get(RESULT).equals(OK)) {
				responseString.append("Download Failed");
				//Final String 
				finalString = responseString.toString();
			}
		}
		
		for (Map.Entry<Long, String> handle : handleHolder.entrySet()) {
			log.debug("HandleKey:" + handle.getKey() + "HandleValue:"
					+ handle.getValue());
		}

		byte[] encoded = java.util.Base64.getEncoder().encode(DatatypeConverter.parseHexBinary(finalString));
		String finalString1 = new String(encoded);
		log.error("finalString1: " + finalString1);
		return finalString1;
	}

	
	private int returnMatch(byte[] inputArray,byte[] matchArray){

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

	private  String asciiToHex(String asciiValue)
    {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();

	}
	
	private String hexToAscii(String hexStr) {
		 String str = "";
		 StringBuilder output = new StringBuilder("");
		 try{
			 for (int i = 0; i < hexStr.length(); i+=2) {
			        str = hexStr.substring(i, i+2);
			        output.append((char)Integer.parseInt(str, 16));
			    }
			    System.out.println(output);
			 
		 }catch(Exception ex){

		 }
		 return new String(output.toString());
		}

	
	private String decodeRawMessage(String rawMessage) {
		String decoded_string = "";
		byte[] decoded = java.util.Base64.getDecoder().decode(rawMessage);
		String sout = "";
		for (int i = 0; i < decoded.length; i++) {
			int val = decoded[i] & 0xFF;
			sout = sout + val + " ";
		}
		log.debug("Input Byte Array :" + sout);
		decoded_string = new String(decoded);
		log.error("Decoded value is " + decoded_string);
		return decoded_string;
	}
	
	
	
	public JSONObject processHexaToByteData(String HexaFilePath, Integer lines)
			throws HillromException {
		byte[] byteArray = new byte[906800];
		JSONObject jsonObject = new JSONObject();
		long count = 0;
		String hexDataStr = "";
	    String [] output = null;
	    String encodedData = null;
	    int flag = 1;
	    if(flag == 0){
	    	try {
				Path pp = FileSystems.getDefault().getPath(HexaFilePath,
						"193164_charger_mainboard.hex");
				FileInputStream fis = new FileInputStream(pp.toFile());
				int len;
				// Read bytes until EOF is encountered.
				do {
					
					len = fis.read(byteArray);

					hexDataStr = getDataInHexString(byteArray);
	
				} while (len != -1);

				fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				log.error("Error in Ecoded bas64 data :" + ex.getMessage());
			}
	    	output = hexDataStr.split("(?<=\\G.{512})");
			storeChunk = new HashMap<Long, String>();
			for(String str :output ){
				storeChunk.put(count++, str);
				
				log.debug("Output into chnuks :" + str);
			}
			log.debug("Output into chnuks :" + storeChunk.size());
	    }
		
		//log.debug("Count :" + count);
		for (Map.Entry<Long, String> entry : storeChunk.entrySet())
		{
			if(entry.getKey() == 1652){
				log.debug("Output into chnuks :" + entry.getValue());
				BigInteger bigint = new BigInteger(entry.getValue(), 16);
				count = entry.getValue().length()/2;
				StringBuilder sb = new StringBuilder();
				byte[] bytes = Base64.encodeInteger(bigint);
				for (byte b : bytes) {
					sb.append((char) b);
				}
				encodedData = new String(sb.toString());
				log.debug("Ecoded bas64 data :" + encodedData);
				
			}
		}
		jsonObject.put("Base64 Encoded ", encodedData);
		jsonObject.put("ChunkSize:", count);
		return jsonObject;
	}

	private String getDataInHexString(byte[] byteArray) {
		String data = "";
		String trimData = "";
		try {
			data = new String(byteArray, 0, byteArray.length);
			trimData = data.replace(":", "").replace("\n", "")
					.replace("\r", "");
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error with getReadIntelHexFile:" + ex.getMessage());
		}
		return trimData;
	}

	private byte[] convertToBas64Encode(byte[] bas64Input) {
		byte[] encoded = null;
		if (bas64Input != null) {
			encoded = Base64.encodeBase64(bas64Input);
		}
		return encoded;
	}

	private byte[] convertToBas64Decode(byte[] bas64Input) {
		byte[] decoded = null;
		if (bas64Input != null) {
			decoded = Base64.decodeBase64(bas64Input);
		}
		return decoded;
	}

	private String getStringFormatWithSapce(List<Integer> decimalValueList) {

		StringBuilder strbul = new StringBuilder();
		String bas64Input = "";
		Iterator<Integer> iter = decimalValueList.iterator();
		while (iter.hasNext()) {
			strbul.append(iter.next());
			if (iter.hasNext()) {
				strbul.append(" ");
			}
		}
		bas64Input = strbul.toString();

		return bas64Input;
	}

	private List<Integer> getReadIntelHexFile(byte[] byteArray) {
		String data = "";
		String trimData = "";
		List<Integer> decimalValueList = new LinkedList<Integer>();
		try {
			data = new String(byteArray, 0, byteArray.length, "ASCII");
			trimData = data.replace(":", "").replace("\n", "")
					.replace("\r", "");

			String val = "2";
			String result = trimData.replaceAll("(.{" + val + "})", "$1 ")
					.trim();
			log.debug("Haxa Decimal with Space:" + result);

			String[] hexaValues = trimData.split(" ", -1);
			
			for (String hex : hexaValues) {
				
				
				int outputDecimal = Integer.parseInt(hex, 16);
				decimalValueList.add(outputDecimal);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error with getReadIntelHexFile:" + ex.getMessage());
		}

		return decimalValueList;
	}

	public JSONObject checkUpdate(String rawMessage) {
		String decoded_string = "";
		JSONObject FOTAJsonData = new JSONObject();
		Map<String, String> fotaJsonData = new LinkedHashMap<String, String>();
		byte[] decoded = java.util.Base64.getDecoder().decode(rawMessage);
		String sout = "";
		for (int i = 0; i < decoded.length; i++) {
			int val = decoded[i] & 0xFF;
			sout = sout + val + " ";
		}
		log.debug("Input Byte Array :" + sout);
		decoded_string = new String(decoded);
		log.error("Decoded value is " + decoded_string);
		fotaJsonData = FOTAParseUtil
				.getFOTAJsonDataFromRawMessage(decoded_string);
		StringBuilder resposeString = new StringBuilder();
		long totalChunk = readHexByteDataFromFile();
		log.error("totalChunk: " + totalChunk);
		
		Random rand = new Random();
		int  handleValue = rand.nextInt(100) + 1;
		resposeString.append(RESULT);
		resposeString.append("Yes");
		resposeString.append("&");
		resposeString.append(HANDLE);
		resposeString.append(handleValue);
		resposeString.append("&");
		resposeString.append(TOTAL_CHUNK);
		resposeString.append(totalChunk);
		resposeString.append("&");
		resposeString.append(CRC);
		String crc = "";
		for (Map.Entry<String, String> entry : fotaJsonData.entrySet()) {
			if (entry.getKey().equals(CRC)) {
				log.debug("CRC:" + entry.getValue());
				crc = entry.getValue();
				break;
			}
		}
		resposeString.append(crc);
		byte[] encoded = java.util.Base64.getEncoder().encode(
				resposeString.toString().getBytes());
		String encodedCheckUpdate = new String(encoded);
		log.error("encodedCheckUpdate: " + encodedCheckUpdate);
		FOTAJsonData.put("encodedCheckUpdate", encodedCheckUpdate);
		return FOTAJsonData;
	}

	private long readHexByteDataFromFile() {
		byte[] byteArray = new byte[906800];
		long count = 0;
		long totalChunk = 0L;
		String hexDataStr = "";
		String[] output = null;
		try {
			Path pp = FileSystems.getDefault().getPath(HEXAFILEPATH,
					"193164_charger_mainboard.hex");
			FileInputStream fis = new FileInputStream(pp.toFile());
			int len;
			// Read bytes until EOF is encountered.
			do {

				len = fis.read(byteArray);

				hexDataStr = getDataInHexString(byteArray);
				log.debug("hexa String :" + hexDataStr);

			} while (len != -1);

			fis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error in Ecoded bas64 data :" + ex.getMessage());
		}
		output = hexDataStr.split("(?<=\\G.{512})");
		
		storeChunk = new LinkedHashMap<Long, String>();
		
		for (String str : output) {
			storeChunk.put(count++, str);
			log.debug("Output into chunk :" + str);
		}
		totalChunk = storeChunk.size()-1;
		log.debug("totalChunk :" + totalChunk);
		return totalChunk;
	}

	
	
}
