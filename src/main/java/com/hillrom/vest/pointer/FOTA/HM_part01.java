package com.hillrom.vest.pointer.FOTA;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HEXAFILEPATH;
import static com.hillrom.vest.config.FOTA.FOTAConstants.REQUEST_TYPE1;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CHUNK_SIZE_VALUE;

import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HM_part01 {

	private final static Logger log = LoggerFactory.getLogger(HM_part01.class);
	
	public static final byte[] CHUNK_SIZE = new byte[]{38,99,104,117,110,107,83,105,122,101,61};
	
	private static Map<Integer, String> fileChunks = null;
	
	
	private static int totalChunk = 0;
	private static HM_part01 instance;

	private HM_part01() {
	} // avoid instantiation.

	public static HM_part01 getInstance(String rawMessage, String requestType, String filePath) {
		if (instance == null) {
			instance = new HM_part01();
			totalChunk = readHexByteDataFromFile(rawMessage,filePath);
			instance.setTotalChunk(totalChunk);
			instance.setFileChunks(fileChunks);
		}else if(instance != null && requestType.equals(REQUEST_TYPE1) ){
			instance = new HM_part01();
			totalChunk = readHexByteDataFromFile(rawMessage,filePath);
			instance.setTotalChunk(totalChunk);
			instance.setFileChunks(fileChunks);
		}
		return instance;
	}

	private static int readHexByteDataFromFile(String rawMessage, String filePath) {
		int ctr = 0;
		int totalChunk = 0;
		String hexDataStr = "";
		String[] output = null;
		try {
			/*Path pp = FileSystems.getDefault().getPath(HEXAFILEPATH,
					"193165_charger_mainboard.hex");*/
			Path pp = FileSystems.getDefault().getPath(filePath);
			FileInputStream fis = new FileInputStream(pp.toFile());
			log.debug("File Length :" + (int) pp.toFile().length());
			byte[] byteArray = new byte[(int) pp.toFile().length()];
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
		
		//Get Chunk Size from request
		String chunkStr = getChunk(rawMessage);
		
		//Decimal conversion
		int chunkSize = hex2decimal(chunkStr);
		
		output = hexDataStr.split("(?<=\\G.{"+(chunkSize*2)+"})");
		fileChunks = new LinkedHashMap<Integer, String>();
		for (String str : output) {
			fileChunks.put(ctr++, str);
			log.debug("fileChunks :" + str);
			//fileChunkBasedOnSize.put(ctr++, str);
			
		}
		
		//fileChunks.put(String.valueOf(chunkSize), fileChunkBasedOnSize);
		totalChunk = fileChunks.size();
		//totalChunk = fileChunkBasedOnSize.size()-1;
		log.debug("totalChunk :" + totalChunk);
		return totalChunk;
	}

	private static String getChunk(String rawMessage) {
		byte[] getChunkByte = java.util.Base64.getDecoder().decode(rawMessage);
		int chunkByteIndex = returnMatch(getChunkByte, CHUNK_SIZE);
		log.error("chunkByteIndex: " + chunkByteIndex);
		//StringBuilder handleRes = new StringBuilder();
		// handleRes.
		//handleRes.append(Integer.toHexString(getChunkByte[chunkSize] & 0xFF));
		int chunkSizeValue = getChunkByte[chunkByteIndex] & 0xFF;
		int chunkSizeValue1 = getChunkByte[chunkByteIndex+1] & 0xFF;
		
		String chunkSize1 = Integer.toHexString(chunkSizeValue);
		String chunkSize2 = Integer.toHexString(chunkSizeValue1);
				
		chunkSize1 =	("00"+ chunkSize1).substring(chunkSize1.length());
		chunkSize2 =	("00"+ chunkSize2).substring(chunkSize2.length());
		
		StringBuilder sb = new StringBuilder();
		sb.append(chunkSize1);
		sb.append(chunkSize2);
		
		String littleEndianChunk = toLittleEndian(sb.toString());
		return littleEndianChunk;
	}

		//To read non readable character
		private static int returnMatch(byte[] inputArray,byte[] matchArray){

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
		private static String toLittleEndian(final String hex) {
		    //int ret = 0;
		    String hexLittleEndian = "";
		    if (hex.length() % 2 != 0) return hexLittleEndian;
		    for (int i = hex.length() - 2; i >= 0; i -= 2) {
		        hexLittleEndian += hex.substring(i, i + 2);
		    }
		   // ret = Integer.parseInt(hexLittleEndian, 16);
		    return hexLittleEndian;
		}
		
		public static int hex2decimal(String s) {
	         String digits = "0123456789ABCDEF";
	         s = s.toUpperCase();
	         int val = 0;
	         for (int i = 0; i < s.length(); i++) {
	             char c = s.charAt(i);
	             int d = digits.indexOf(c);
	             val = 16*val + d;
	         }
	         return val;
	     }
	private static String getDataInHexString(byte[] byteArray) {
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

	public Map<Integer, String> getFileChunks() {
		return fileChunks;
	}

	public void setFileChunks(Map<Integer, String> fileChunks) {
		this.fileChunks = fileChunks;
	}

	public static int getTotalChunk() {
		return totalChunk;
	}

	public static void setTotalChunk(int totalChunk) {
		HM_part01.totalChunk = totalChunk;
	}

	/*public static Map<String, Integer> getSendChunkCounter() {
		return sendChunkCounter;
	}

	public static void setSendChunkCounter(Map<String, Integer> sendChunkCounter) {
		HM_part01.sendChunkCounter = sendChunkCounter;
	}
*/
	
}
