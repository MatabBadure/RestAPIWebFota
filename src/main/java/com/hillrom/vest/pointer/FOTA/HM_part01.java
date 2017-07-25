package com.hillrom.vest.pointer.FOTA;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HEXAFILEPATH;
import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HM_part01 {

	private final static Logger log = LoggerFactory.getLogger(HM_part01.class);
	
	private static Map<Integer, String> fileChunks = new LinkedHashMap<Integer, String>();
	
	//private static Map<String, Integer> sendChunkCounter = new LinkedHashMap<String, Integer>();
	
	private static int totalChunk = 0;
	private static HM_part01 instance;

	private HM_part01() {
	} // avoid instantiation.

	public static HM_part01 getInstance() {
		if (instance == null) {
			instance = new HM_part01();
			totalChunk = readHexByteDataFromFile();
			instance.setTotalChunk(totalChunk);
			instance.setFileChunks(fileChunks);
		}
		return instance;
	}

	private static int readHexByteDataFromFile() {
		int ctr = 0;
		int totalChunk = 0;
		String hexDataStr = "";
		String[] output = null;
		try {
			Path pp = FileSystems.getDefault().getPath(HEXAFILEPATH,
					"193164_charger_mainboard.hex");
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
		output = hexDataStr.split("(?<=\\G.{512})");
		for (String str : output) {
			fileChunks.put(ctr++, str);
			log.debug("fileChunks :" + str);
		}
		totalChunk = fileChunks.size()-1;
		log.debug("totalChunk :" + totalChunk);
		return totalChunk;
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
