package com.hillrom.vest.service.FOTA;

import static com.hillrom.vest.config.FOTA.FOTAConstants.AMPERSAND;
import static com.hillrom.vest.config.FOTA.FOTAConstants.BUFFER_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.BUFFER_LEN_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CRC;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CRC_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HANDLE;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HANDLE_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HEXAFILEPATH;
import static com.hillrom.vest.config.FOTA.FOTAConstants.INIT;
import static com.hillrom.vest.config.FOTA.FOTAConstants.NOT_OK;
import static com.hillrom.vest.config.FOTA.FOTAConstants.OK;
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

import net.minidev.json.JSONObject;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.service.util.FOTA.FOTAParseUtil;
@Service
@Transactional
public class FOTAService {

	private final Logger log = LoggerFactory.getLogger(FOTAService.class);
	private static Map<Long,String> storeChunk ;
	private static Map<Long,String> handleHolder ;
	
	private static long handleHolderCount ;
	
	private int bufferLen = 0;
	
	private String buffer = " ";
	
	private TaskScheduler scheduler = new ConcurrentTaskScheduler();
	
	//private static Map<Long,String> storeChunk1 ;
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
			data = new String(byteArray, 0, byteArray.length, "ASCII");
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

	public String FOTAUpdate(String rawMessage) {

		String decoded_string = "";

		StringBuilder responseString = new StringBuilder();

		Map<String, String> fotaJsonData = new LinkedHashMap<String, String>();

		// Decoding raw data
		decoded_string = decodeRawMessage(rawMessage);
		// Parsing into key value pair
		fotaJsonData = FOTAParseUtil
				.getFOTAJsonDataFromRawMessage(decoded_string);

		// Checking if request Type is 01 & //Checking if request Type is 02
		for (Map.Entry<String, String> entry : fotaJsonData.entrySet()) {
			long totalChunk = 0L;
			int handleValue = 0;
			// long handleCount = 0;
			if (entry.getValue().equals(REQUEST_TYPE1)) {
				handleHolder = new LinkedHashMap<Long, String>();
				log.error("totalChunk: " + handleHolder.size());
				totalChunk = readHexByteDataFromFile();
				log.error("totalChunk: " + totalChunk);
				Random rand = new Random();
				handleValue = rand.nextInt(100) + 1;
				responseString.append(RESULT_EQ);
				responseString.append("Yes");
				responseString.append(AMPERSAND);
				responseString.append(HANDLE_EQ);
				responseString.append(handleValue);
				responseString.append(AMPERSAND);
				responseString.append(TOTAL_CHUNK);
				responseString.append(totalChunk);
				responseString.append(AMPERSAND);
				responseString.append(CRC_EQ);
				String crc = "";
				for (Map.Entry<String, String> entry1 : fotaJsonData.entrySet()) {
					if (entry1.getKey().equals(CRC)) {
						log.debug("CRC:" + entry1.getValue());
						crc = entry1.getValue();
						break;
					}
				}
				responseString.append(crc);
			} else if (entry.getValue().equals(REQUEST_TYPE2)) {
				for (Map.Entry<String, String> entry1 : fotaJsonData.entrySet()) {
					if (entry1.getValue().equals(INIT)) {
						for (Map.Entry<Long, String> chunk : storeChunk
								.entrySet()) {
							if (chunk.getKey() == handleHolderCount) {
								log.debug("Output into chnuks :"
										+ chunk.getValue());
								BigInteger bigint = new BigInteger(
										chunk.getValue(), 16);
								bufferLen = chunk.getValue().length() / 2;
								StringBuilder sb = new StringBuilder();
								byte[] bytes = Base64.encodeInteger(bigint);
								for (byte b : bytes) {
									sb.append((char) b);
								}
								buffer = new String(sb.toString());
								log.debug("Ecoded bas64 data :" + buffer);
								// handleHolder.put(chunk.getKey(), "OK");
							}
						}
						 //handleHolderCount = handleHolderCount;
						log.debug("handleHolderCount:"
								+ handleHolderCount);
					}
					// handleHolder.put((long) handleCount++,
					// entry1.getValue());
					else if (entry1.getValue().equals(OK)) {
						// handleCount = handleHolderCount;
						// handleHolder.put(handleHolderCount++,
						// entry1.getValue());
						handleHolderCount = handleHolderCount+1;
						for (Map.Entry<Long, String> chunk : storeChunk
								.entrySet()) {
							if (chunk.getKey() == handleHolderCount) {
								log.debug("Output into chnuks :"
										+ chunk.getValue());
								BigInteger bigint = new BigInteger(
										chunk.getValue(), 16);
								bufferLen = chunk.getValue().length() / 2;
								StringBuilder sb = new StringBuilder();
								byte[] bytes = Base64.encodeInteger(bigint);
								for (byte b : bytes) {
									sb.append((char) b);
								}
								buffer = new String(sb.toString());
								log.debug("Ecoded bas64 data :" + buffer);
								// handleHolder.put(chunk.getKey(), "OK");
							}
						}
						// handleHolderCount = handleCount;
						log.debug("handleHolderCount:"
								+ handleHolderCount);

					} else if (entry1.getValue().equals(NOT_OK)) {
						// handleCount = handleHolderCount;
						// handleHolder.put(handleHolderCount--,
						// entry1.getValue());
						handleHolderCount = handleHolderCount-1;
						for (Map.Entry<Long, String> chunk : storeChunk
								.entrySet()) {
							if (chunk.getKey() == handleHolderCount) {
								log.debug("Output into chnuks :"
										+ chunk.getValue());
								BigInteger bigint = new BigInteger(
										chunk.getValue(), 16);
								bufferLen = chunk.getValue().length() / 2;
								StringBuilder sb = new StringBuilder();
								byte[] bytes = Base64.encodeInteger(bigint);
								for (byte b : bytes) {
									sb.append((char) b);
								}
								buffer = new String(sb.toString());
								log.debug("Ecoded bas64 data :" + buffer);
								// handleHolder.put(chunk.getKey(), "OK");
							}
						}
						//handleHolderCount = handleHolderCount+1;
						log.debug("handleHolderCount:"
								+ handleHolderCount);
						// handleHolderCount = handleCount;
					}

				}

				String handle = "";
				for (Map.Entry<String, String> entry1 : fotaJsonData.entrySet()) {
					if (entry1.getKey().equals(HANDLE)) {
						log.debug("HANDLE:" + entry1.getValue());
						handle = entry1.getValue();
						break;
					}
				}
				responseString.append(HANDLE_EQ);
				responseString.append(handle);
				responseString.append(AMPERSAND);
				responseString.append(BUFFER_LEN_EQ);
				responseString.append(bufferLen);
				responseString.append(AMPERSAND);
				responseString.append(BUFFER_EQ);
				responseString.append(buffer);
				responseString.append(AMPERSAND);
				String crc = "";
				for (Map.Entry<String, String> entry1 : fotaJsonData.entrySet()) {
					if (entry1.getKey().equals(CRC)) {
						log.debug("CRC:" + entry1.getValue());
						crc = entry1.getValue();
						break;
					}
				}
				responseString.append(CRC_EQ);
				responseString.append(crc);

			} else if (entry.getValue().equals(REQUEST_TYPE3)) {
				for (Map.Entry<String, String> entry1 : fotaJsonData.entrySet()) {

					if (entry1.getValue().equals(OK)) {
						responseString.append("Download completed");

					}

					else if (entry1.getValue().equals(NOT_OK)) {
						responseString.append("Download Failed");
					}

				}

			}
			
		}

		byte[] encoded = java.util.Base64.getEncoder().encode(
				responseString.toString().getBytes());
		String encodedCheckUpdate = new String(encoded);
		log.error("encodedCheckUpdate: " + encodedCheckUpdate);
		return encodedCheckUpdate;
	}


	/*@PostConstruct
	private void executeJob() {
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				
			}
		}, 5000);
	}
*/
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
	
	
}
