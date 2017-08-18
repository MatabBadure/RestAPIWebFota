package com.hillrom.vest.pointer.FOTA;

import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartNoHolder {

	private final static Logger log = LoggerFactory
			.getLogger(PartNoHolder.class);
	private String part_No;
	private String version_No;
	DateTime effectiveDate;
	private int totalChunk = 0;
	private int chunkSize = 0;
	private Map<Integer, String> fileChunks = null;

	public static final byte[] CHUNK_SIZE = new byte[] { 38, 99, 104, 117, 110,
			107, 83, 105, 122, 101, 61 };
	
	PartNoHolder partNoHolder = null;
	public PartNoHolder(){
		
	}
	public PartNoHolder(int chunkSize, String filePath) {
		/*partNoHolder = new PartNoHolder(chunkSize, filePath);*/
		partNoHolder = new PartNoHolder();
		totalChunk = readHexByteDataFromFile(chunkSize, filePath);
		partNoHolder.setTotalChunk(totalChunk);
		partNoHolder.setFileChunks(fileChunks);
		
	}

	public int hashCode() {
        return part_No.hashCode();
    }

    public boolean equals(PartNoHolder pNH) {
        if (pNH == null)
            return false;
        else if (pNH.part_No.equalsIgnoreCase(this.part_No))
            return true;
        else
            return false;
    }
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public String getPart_No() {
		return part_No;
	}

	

	public int getChunkSize() {
		return chunkSize;
	}

	public String getVersion_No() {
		return version_No;
	}

	public void setVersion_No(String version_No) {
		this.version_No = version_No;
	}

	public DateTime getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(DateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public void setPart_No(String part_No) {
		this.part_No = part_No;
	}

	public int getTotalChunk() {
		return totalChunk;
	}

	public void setTotalChunk(int totalChunk) {
		this.totalChunk = totalChunk;
	}

	public Map<Integer, String> getFileChunks() {
		return fileChunks;
	}

	public void setFileChunks(Map<Integer, String> fileChunks) {
		this.fileChunks = fileChunks;
	}

	
	private int readHexByteDataFromFile(int chunkSize, String filePath) {
		int ctr = 0;
		int totalChunk = 0;
		String hexDataStr = "";
		String[] output = null;
		try {
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

		/*// Get Chunk Size from request
		String chunkStr = getChunk(chunkSize);

		// Decimal conversion
		int chunkSize = hex2decimal(chunkStr);*/

		output = hexDataStr.split("(?<=\\G.{" + (chunkSize * 2) + "})");
		fileChunks = new LinkedHashMap<Integer, String>();
		for (String str : output) {
			fileChunks.put(ctr++, str);
			log.debug("fileChunks :" + str);
		}

		// fileChunks.put(String.valueOf(chunkSize), fileChunkBasedOnSize);
		totalChunk = fileChunks.size();
		// totalChunk = fileChunkBasedOnSize.size()-1;
		log.debug("totalChunk :" + totalChunk);
		return totalChunk;
	}

	private int hex2decimal(String chunkStr) {

		String digits = "0123456789ABCDEF";
		chunkStr = chunkStr.toUpperCase();
		int val = 0;
		for (int i = 0; i < chunkStr.length(); i++) {
			char c = chunkStr.charAt(i);
			int d = digits.indexOf(c);
			val = 16 * val + d;
		}
		return val;

	}

	private String getChunk(String rawMessage) {

		byte[] getChunkByte = java.util.Base64.getDecoder().decode(rawMessage);
		int chunkByteIndex = returnMatch(getChunkByte, CHUNK_SIZE);
		log.error("chunkByteIndex: " + chunkByteIndex);
		// StringBuilder handleRes = new StringBuilder();
		// handleRes.
		// handleRes.append(Integer.toHexString(getChunkByte[chunkSize] &
		// 0xFF));
		int chunkSizeValue = getChunkByte[chunkByteIndex] & 0xFF;
		int chunkSizeValue1 = getChunkByte[chunkByteIndex + 1] & 0xFF;

		String chunkSize1 = Integer.toHexString(chunkSizeValue);
		String chunkSize2 = Integer.toHexString(chunkSizeValue1);

		chunkSize1 = ("00" + chunkSize1).substring(chunkSize1.length());
		chunkSize2 = ("00" + chunkSize2).substring(chunkSize2.length());

		StringBuilder sb = new StringBuilder();
		sb.append(chunkSize1);
		sb.append(chunkSize2);

		String littleEndianChunk = toLittleEndian(sb.toString());
		return littleEndianChunk;

	}

	// To read non readable character
	private static int returnMatch(byte[] inputArray, byte[] matchArray) {

		for (int i = 0; i < inputArray.length; i++) {
			int val = inputArray[i] & 0xFF;
			boolean found = false;

			if ((val == 38) && !found) {
				int j = i;
				int k = 0;
				while ((inputArray[j++] == matchArray[k++])
						&& (k < matchArray.length)) {

				}
				if (k == matchArray.length) {
					found = true;
					return j;
				}
			}
		}

		return -1;

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

	private static String toLittleEndian(final String hex) {
		// int ret = 0;
		String hexLittleEndian = "";
		if (hex.length() % 2 != 0)
			return hexLittleEndian;
		for (int i = hex.length() - 2; i >= 0; i -= 2) {
			hexLittleEndian += hex.substring(i, i + 2);
		}
		// ret = Integer.parseInt(hexLittleEndian, 16);
		return hexLittleEndian;
	}

}
