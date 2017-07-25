package com.hillrom.vest.service.FOTA;

import static com.hillrom.vest.config.FOTA.FOTAConstants.AMPERSAND;
import static com.hillrom.vest.config.FOTA.FOTAConstants.BUFFER_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.BUFFER_LEN_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CRC;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CRC_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_PARTNUMBER;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_PARTNUMBER_01;
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
import com.hillrom.vest.pointer.FOTA.HM_HandleHolder;
import com.hillrom.vest.pointer.FOTA.HM_part01;
import com.hillrom.vest.service.util.FOTA.FOTAParseUtil;
@Service
@Transactional
public class FOTAService {

	private final Logger log = LoggerFactory.getLogger(FOTAService.class);
	private  Map<Long,String> storeChunk ;
	private  Map<Long,String> handleHolder ;
	
	public static final byte[] CRC_FIELD_NAME = new byte[]{38,99,114,99,61};
	public static final byte[] CHUNK_SIZE = new byte[]{38,99,104,117,110,107,83,105,122,101,61};
	public static final byte[] HANDLE = new byte[]{38,104,97,110,100,108,101,61};
	
	private int bufferLen = 0;
	private String buffer = null;
	
	public String FOTAUpdate(String rawMessage) {

		String decoded_string = "";

		StringBuilder responseString = new StringBuilder();
		String responsePair1 = "";
		String responsePair2 = "";
		String responsePair3 = "";
		String responsePair4 = "";
		String finalResponseStr = new String();

		Map<String, String> fotaJsonData = new LinkedHashMap<String, String>();

		// Decoding raw data
		decoded_string = decodeRawMessage(rawMessage);
		// Parsing into key value pair
		fotaJsonData = FOTAParseUtil
				.getFOTAJsonDataFromRawMessage(decoded_string);
		
		//Global handler
		HM_HandleHolder globalHandleHolder = HM_HandleHolder.getInstance();

		// Checking if request Type is 01 & //Checking if request Type is 02
		if (fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE1)) {
			
			
			if(fotaJsonData.get(DEVICE_PARTNUMBER).equals(DEVICE_PARTNUMBER_01)){
				int totalChunks = 0;
				HM_part01 hmp01 = HM_part01.getInstance();
				
				String handleId = getHandleNumber();
				
				globalHandleHolder.getHandles().put(handleId,0);
				
				totalChunks = hmp01.getTotalChunk();
				// Response pair1
				responsePair1 = getResponePair1();
				// Handle in raw format
				String handleIdRaw = hexToAscii(asciiToHex(handleId));

				// Response pair2
				responsePair2 = getResponePair2();

				// Total chunk in raw format
				String totalChunkRaw = getChunkRaw(totalChunks);

				// Response pair3 crc
				responsePair3 = getResponePair3();

				// CRC in raw format
				String crcRaw = getCRC(rawMessage);

				// Final response String
				finalResponseStr = getAllResponseCheckUpdate(responsePair1, handleIdRaw,
						responsePair2, totalChunkRaw, responsePair3, crcRaw);
				log.error("finalResponseStr: " + finalResponseStr);
				
			}
			
		}else if(fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE2)){
			
			String handleId = "";
			int chunkCount = 0;
			if (fotaJsonData.get(PREV_REQ_STATUS).equals(INIT)) {
				//Get handle from request
				handleId = getHandleFromRequest(rawMessage);
				log.debug("handleId from Request:" + handleId);
				
				//get chunk based on handle
				int counter = globalHandleHolder.getHandles().get(handleId);
				log.debug("counter:" + counter+"for"+handleId);
				
				if(counter == 0){
					HM_part01 hmp01 = HM_part01.getInstance();
					String zeroChunk = hmp01.getFileChunks().get(0);
					//Zero the Chunk in raw format
					buffer = hexToAscii(asciiToHex(zeroChunk));
					log.debug("buffer Encoded:" + buffer);
					
					//Chunk size in hex byte
					bufferLen = zeroChunk.length() / 2;
					log.debug("bufferLen:" + bufferLen);
					
				}

			}else if (fotaJsonData.get(PREV_REQ_STATUS).equals(OK)) {
				
				HM_part01 hmp01 = HM_part01.getInstance();
				//Get handle from request
				handleId = getHandleFromRequest(rawMessage);
				log.debug("handleId from Request:" + handleId);
				
				//get chunk based on handle
				int counter = globalHandleHolder.getHandles().get(handleId);
				if(counter == 0){
					globalHandleHolder.getHandles().put(handleId, ++counter);
					log.debug("counter:" + counter+"for"+handleId);
					
					//OK send Chunk in raw format
					String okSendChunk = hmp01.getFileChunks().get(counter);
					
					//Buffer values
					buffer = hexToAscii(asciiToHex(okSendChunk));
					log.debug("buffer Encoded:" + buffer);
					
					//Chunk size in hex byte
					bufferLen = okSendChunk.length() / 2;
					log.debug("bufferLen:" + bufferLen);
				}
				
			} else if (fotaJsonData.get(PREV_REQ_STATUS).equals(NOT_OK)) {
				HM_part01 hmp01 = HM_part01.getInstance();
				
				//Get handle from request
				handleId = getHandleFromRequest(rawMessage);
				log.debug("handleId from Request:" + handleId);
				
				//Dont increment the counter
				Integer counter = globalHandleHolder.getHandles().get(handleId);
				globalHandleHolder.getHandles().put(handleId, counter);
				log.debug("counter:" + counter+"for"+handleId);
				
				//If not ok send previous Chunk in raw format
				String okSendChunk = hmp01.getFileChunks().get(counter);;
				
				////Buffer values
				buffer = hexToAscii(asciiToHex(okSendChunk));
				log.debug("buffer Encoded:" + buffer);
				
				//Chunk size in hex byte
				bufferLen = okSendChunk.length() / 2;
				log.debug("bufferLen:" + bufferLen);
				}
			
			//response Init Pair1 HANDLE_EQ
			responsePair1 = asciiToHex(HANDLE_EQ);
			
			//Handle in raw format(handle Value)
			String handleIdRaw = hexToAscii(asciiToHex(handleId));	
			
			//response Init Pair2(BUFFER_LEN_EQ)
			responsePair2 = getInitResponsePair2();
			
			//bufferLen = zeroChunk.length() / 2;
			////bufferLen in raw format(bufferLen Value)
			String bufferLenRaw =  hexToAscii(asciiToHex(Integer.toHexString(128)));
			
			// response Init Pair2 BUFFER_EQ
			responsePair3 = getInitReponsePair3();
			
			// Response pair4 Init  crc
			responsePair4 = getResponePair3();
			
			// CRC in raw format init request
			String crcRaw = getCRC(rawMessage);
			
			// Final response String
			finalResponseStr = getInitOKResponseSendChunk(responsePair1, handleIdRaw,
					responsePair2, bufferLenRaw, responsePair3, buffer,responsePair4,crcRaw);
			log.error("finalResponseStr: " + finalResponseStr);
			
		}else if (fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE3)) {
			if (fotaJsonData.get(RESULT).equals(OK)) {
				responseString.append("Download completed");
				//Final String 
				finalResponseStr = responseString.toString();
			} else if (fotaJsonData.get(RESULT).equals(NOT_OK)) {
				responseString.append("Download Failed");
				//Final String 
				finalResponseStr = responseString.toString();
			}
		}
		
			byte[] encoded = java.util.Base64.getEncoder().encode(DatatypeConverter.parseHexBinary(finalResponseStr));
			String finalString1 = new String(encoded);
			log.error("finalString1: " + finalString1);
			return finalString1;
		
	}
		
	private String getInitOKResponseSendChunk(String responsePair1,
			String handleIdRaw, String responsePair2, String bufferLenRaw,
			String responsePair3, String buffer, String responsePair4,
			String crcRaw) {
		//Final String 
		String finalString = responsePair1.concat(handleIdRaw).concat(responsePair2).concat(bufferLenRaw).concat(responsePair3).concat(buffer).concat(responsePair4).concat(crcRaw);
		return finalString;
	}


	private String getHandleFromRequest(String rawMessage) {

		byte[] getHandleByte = java.util.Base64.getDecoder().decode(rawMessage);
		int handleIndex = returnMatch(getHandleByte, HANDLE);
		log.error("str1: " + handleIndex);
		StringBuilder handleRes = new StringBuilder();
		// handleRes.
		handleRes
				.append(Integer.toHexString(getHandleByte[handleIndex] & 0xFF));
		handleRes.append(Integer
				.toHexString(getHandleByte[handleIndex + 1] & 0xFF));
		handleRes.append(Integer
				.toHexString(getHandleByte[handleIndex + 2] & 0xFF));
		handleRes.append(Integer
				.toHexString(getHandleByte[handleIndex + 3] & 0xFF));
		String handleId = handleRes.toString();
		/*BigInteger toHex = new BigInteger(handleId,10);
	    String handleIdString = toHex.toString(16);*/
		handleId = ("00000000"+ handleId).substring(handleId.length());
		String handleIdStringHex = hexToAscii(asciiToHex(handleId));
		log.error("handleIdStringHex: " + handleIdStringHex);
		return handleIdStringHex;
	}


	private String getInitReponsePair3() {
		StringBuilder response = new StringBuilder();	
		response.append(AMPERSAND);
		response.append(BUFFER_EQ);
		String initResponsePair3 = asciiToHex(response.toString());
		return initResponsePair3;
	}


	private String getInitResponsePair2() {
		StringBuilder response = new StringBuilder();	
		response.append(AMPERSAND);
		response.append(BUFFER_LEN_EQ);
		String initResponsePair3 = asciiToHex(response.toString());
		return initResponsePair3;
	}


	private String getAllResponseCheckUpdate(String responsePair1, String handleIdRaw,
			String responsePair2, String totalChunkRaw, String responsePair3,
			String crcRaw) {
		String finalString = responsePair1.concat(handleIdRaw).concat(responsePair2)
				.concat(totalChunkRaw).concat(responsePair3).concat(crcRaw);
		return finalString;
	}


	private String getCRC(String rawMessage) {
		byte[] getCRCByte = java.util.Base64.getDecoder().decode(rawMessage);
		int start = returnMatch(getCRCByte, CRC_FIELD_NAME);
		int start1 = returnMatch(getCRCByte, CRC_FIELD_NAME) + 1;
		StringBuilder crcHexBilder = new StringBuilder();

		crcHexBilder.append(Integer.toHexString(getCRCByte[start] & 0xFF));
		crcHexBilder.append(Integer.toHexString(getCRCByte[start1] & 0xFF));

		log.error("crcHexBilder: " + crcHexBilder);
		String crcHexString = hexToAscii(asciiToHex(crcHexBilder.toString()));
		log.error("crcHexString: " + crcHexString);
		return crcHexString;
	}


	private String getResponePair3() {
		StringBuilder response = new StringBuilder();
		response.append(AMPERSAND);
		response.append(CRC_EQ);
		String responePair3 = asciiToHex(response.toString());
		log.error("responePair3: " + responePair3);
		return responePair3;
	}


	private String getChunkRaw(int totalChunks) {
		BigInteger toHex = new BigInteger(String.valueOf(totalChunks),10);
	    String totalChunkHexString = toHex.toString(16);
	    totalChunkHexString= ("00000000" + totalChunkHexString).substring(totalChunkHexString.length());
		String strTotalChunk = hexToAscii(asciiToHex(totalChunkHexString));
		log.error("strTotalChunk: " + strTotalChunk);
		return strTotalChunk;
	}


	private String getResponePair2() {
		StringBuilder response = new StringBuilder();
		response.append(AMPERSAND);
		response.append(TOTAL_CHUNK);
		String responePair2 = asciiToHex(response.toString());
		log.error("responePair2: " + responePair2);
		return responePair2;
	}


	private String getResponePair1() {
		StringBuilder response = new StringBuilder();
		response.append(RESULT_EQ);
		response.append("Yes");
		response.append(AMPERSAND);
		response.append(HANDLE_EQ);
		String responePair1 = asciiToHex(response.toString());
		log.error("responePair1: " + responePair1);
		return responePair1;
	}

	//Generating unique handle id
	private String getHandleNumber() {
		Random random = new Random();
		int random1 = random.nextInt(10000);
		int random2 = random.nextInt(1000);
		int uniqueRandom = random1+random2;
        BigInteger toHex=new BigInteger(String.valueOf(uniqueRandom),10);	
        String handleHexString =toHex.toString(16);
        handleHexString = ("00000000" + handleHexString).substring(handleHexString.length());
        System.out.println(handleHexString);
		return handleHexString;
	}

	//To read non readable character
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
	// Convert String to ASCII
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
	//Make it ready for raw data 
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

	//DecodeRawMessage
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
	
	
	////Initial code starts
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
