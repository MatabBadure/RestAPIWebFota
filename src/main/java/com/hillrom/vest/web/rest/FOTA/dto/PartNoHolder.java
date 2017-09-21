package com.hillrom.vest.web.rest.FOTA.dto;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hillrom.vest.domain.FOTA.FOTAInfo;

public class PartNoHolder {

	private final static Logger log = LoggerFactory
			.getLogger(PartNoHolder.class);
	private String part_No;
	private String version_No;
	private DateTime effectiveDate;
	private DateTime downloadTime;
	private int totalChunk = 0;
	private int chunkSize = 0;
	private boolean abortFlag;
	private Map<Integer, String> fileChunks = null;

	public static final byte[] CHUNK_SIZE = new byte[] { 38, 99, 104, 117, 110,
			107, 83, 105, 122, 101, 61 };
	private static final int HEX = 16;
	
	PartNoHolder partNoHolder = null;
	
	public PartNoHolder() {
		// TODO Auto-generated constructor stub
	}
	public PartNoHolder(int chunkSize, FOTAInfo fotaInfo) throws Exception{
		partNoHolder = new PartNoHolder();
		totalChunk = readHexByteDataFromFile(chunkSize, fotaInfo);
		partNoHolder.setTotalChunk(totalChunk);
		partNoHolder.setFileChunks(fileChunks);
		partNoHolder.setAbortFlag(false);
		
	}
	
	private int readHexByteDataFromFile(int chunkSize, FOTAInfo fotaInfo) throws Exception {
        String hexDataStr = null;
        String[] output = null;
        int ctr = 0;
        
        try {
              //Path pp = FileSystems.getDefault().getPath("D:/FOTA/Hex/193164_charger_mainboard.hex");
        	Path pp = FileSystems.getDefault().getPath(fotaInfo.getFilePath());
              hexDataStr = new String(Files.readAllBytes(pp)).replace(":", "").replace("\n","").replace("\r","");
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
        //Splitting in hex format
        output = hexDataStr.split("(?<=\\G.{" + (chunkSize * 2) + "})");
        //Storing chunks into hash map
        fileChunks = new LinkedHashMap<Integer, String>();
        for (String str : output) {
        	fileChunks.put(ctr++, str);
        }
        totalChunk = fileChunks.size();
        return totalChunk;
    }
	
	public boolean checkCRC32(FOTAInfo fotaInfo) throws Exception{
		long start_time = System.nanoTime();
    	long end_time = 0;
        
        int totalChunk = 0;
        byte state = 0;

    	int data_cnt = 0;
    	int data_size = 0;
    	int checksum = 0;
    	long addr  = 0;
    	long upperaddr = 0;
    	int record_type = 0;
    	byte[] tempDataBytes = null;

    	 long crcStartAddress = 0;
         long crcEndAddress = 0;
         long crcLocationAddress = 0;
        int crcValueInFile = 0;
        ByteArrayOutputStream crcData = new ByteArrayOutputStream();
        ByteArrayOutputStream crcData2 = new ByteArrayOutputStream();
        
        long crc2StartAddress = 0;
        long crc2EndAddress = 0;
        long crc2LocationAddress = 0;
        int crc2ValueInFile = 0;
        
        
        
        if (StringUtils.isNotEmpty(fotaInfo.getRegion1StartAddress())) {
			crcStartAddress = Long.parseLong(fotaInfo.getRegion1StartAddress(),16);
		}
		if (StringUtils.isNotEmpty(fotaInfo.getRegion1EndAddress())) {
			crcEndAddress = Long.parseLong(fotaInfo.getRegion1EndAddress(),16);
		}
		
		if (StringUtils.isNotEmpty(fotaInfo.getRegion1CRCLocation())) {
			crcLocationAddress = Long.parseLong(fotaInfo.getRegion1CRCLocation(),16);
		}
		if (StringUtils.isNotEmpty(fotaInfo.getRegion2StartAddress())) {
			crc2StartAddress = Long.parseLong(fotaInfo.getRegion2StartAddress(),16);
		}
		if (StringUtils.isNotEmpty(fotaInfo.getRegion2EndAddress())) {
			crc2EndAddress = Long.parseLong(fotaInfo.getRegion2EndAddress(),16);
		}
		
		if (StringUtils.isNotEmpty(fotaInfo.getRegion2CRCLocation())) {
			crc2LocationAddress = Long.parseLong(fotaInfo.getRegion2CRCLocation(),16);
		}

		totalChunk = fileChunks.size();
        
        int currChunk = 0;
        while(currChunk<totalChunk)
        {	//Get the chunks based on index
        	String chunkStr = fileChunks.get(currChunk);
        	byte [] chunkBytes = new byte[chunkStr.length()/2];
        	for(int chunkByteCnt = 0; chunkByteCnt < chunkBytes.length; chunkByteCnt++)
        	{
        		String num = chunkStr.substring((2*chunkByteCnt), (2*chunkByteCnt)+2);
        		chunkBytes[chunkByteCnt] = (byte) Integer.parseInt(num, HEX);
        	}
        	currChunk++;
        	
        	// Parse Bytes
        	for(int chunkByteCnt = 0; chunkByteCnt < chunkBytes.length; chunkByteCnt++)
        	{
        		byte b = chunkBytes[chunkByteCnt];
        		switch(state)
        		{
        			case 0:
        				data_cnt = 0;
        				data_size = b;
        				if(data_size > 0)
        				{
        					tempDataBytes = new byte[data_size];
        				}
        				checksum = b;
        				state= 1;
        				break;
        			case 1:
        			{
        				addr = (b&0xFF);
        				checksum += b;
        				state = 2;
        			}
        			break;
        			case 2:
        			{
        				addr <<= 8;
        				addr |= (b&0xFF);
        				checksum += b;
        				state = 3;
        			}
        			break;
        			case 3:
        			{
        				record_type = b;
        				checksum += b;
        				switch(record_type)
        				{
        					case 0:
        					case 1:
        					case 2:
        					case 4:
        					case 5:
        						break;
        					default:
        						log.error("Record type invalid");
            					return false;
        				}
        				if(data_size>0)
        				{
        					state = 4;
        				}
        				else
        				{
        					state = 5;
        				}
    				}
    				break;
        			case 4:
        			{
        				tempDataBytes[data_cnt] = b;
        				checksum += b;
        				data_cnt++;
        				if(data_cnt==data_size)
        				{
        					state = 5;
        				}
        				else
        				{
        					state = 4;
        				}
        			}
        			break;
        			case 5:
        				byte tmpSum = (byte)((~(checksum)+1) & 0xFF);
        				if(tmpSum != b)
        				{
        					log.error("CRC not matching" + checksum + " " + b);
        					return false;
        				}
        				
        				addr = addr | upperaddr;
        				
        	            switch (record_type) {
	        	            case 0:
	
	        	                long tmpAddr = addr;
	        	                long tmp2Addr = addr;
	
	        	                for (byte c : tempDataBytes) {
	        	                    if ((tmpAddr >= crcLocationAddress) &&
	        	                            (tmpAddr < (crcLocationAddress + 4))) {
	        	                        int diff = (int) (tmpAddr - crcLocationAddress);
	
	        	                        switch (diff) {
	        	                        case 0:
	        	                            crcValueInFile = ((int) c & 0xFF);
	
	        	                            break;
	
	        	                        case 1:
	        	                            crcValueInFile |= (((int) c & 0xFF) << 8);
	
	        	                            break;
	
	        	                        case 2:
	        	                            crcValueInFile |= (((int) c & 0xFF) << 16);
	
	        	                            break;
	
	        	                        case 3:
	        	                            crcValueInFile |= (((int) c & 0xFF) << 24);
	
	        	                            break;
	
	        	                        default:
	        	                        }
	        	                    }
	
	        	                    tmpAddr++;
	
	        	                    if ((addr >= crcStartAddress) && (addr <= crcEndAddress)) {
	        	                        crcData.write(c);
	        	                    }
	
	        	                    if ((tmp2Addr >= crc2LocationAddress) &&
	        	                            (tmp2Addr < (crc2LocationAddress + 4))) {
	        	                        int diff = (int) (tmp2Addr - crc2LocationAddress);
	
	        	                        switch (diff) {
	        	                        case 0:
	        	                            crc2ValueInFile = ((int) c & 0xFF);
	
	        	                            break;
	
	        	                        case 1:
	        	                            crc2ValueInFile |= (((int) c & 0xFF) << 8);
	
	        	                            break;
	
	        	                        case 2:
	        	                            crc2ValueInFile |= (((int) c & 0xFF) << 16);
	
	        	                            break;
	
	        	                        case 3:
	        	                            crc2ValueInFile |= (((int) c & 0xFF) << 24);
	
	        	                            break;
	
	        	                        default:
	        	                        }
	        	                    }
	
	        	                    tmp2Addr++;
	
	        	                    if ((addr >= crc2StartAddress) && (addr <= crc2EndAddress)) {
	        	                        crcData2.write(c);
	        	                    }
	        	                }
	
	        	                break;
	
	        	            case 1:
	        	                break;
	
	        	            case 2:
	
	        	                if (data_size == 2) {
	        	                    upperaddr = ((tempDataBytes[0] & 0xFF) << 12) +
	        	                        ((tempDataBytes[1] & 0xFF) << 4);
	        	                } else {
	        	                	log.error("Invalid SEG record");
	        	                    return false;
	        	                }
	
	        	                break;
	
	        	            case 4:
	
	        	                if (data_size == 2) {
	        	                    upperaddr= ((tempDataBytes[0] & 0xFF) << 24) +
	        	                        ((tempDataBytes[1] & 0xFF) << 16);
	        	                } else {
	        	                	log.error("Invalid EXT_LIN record ");
	        	                    return false;
	        	                }
	
	        	                break;
	
	        	            default:
	        	                break;
        	            }
        				tempDataBytes = null;
        				state = 0;
        				break;
        			default:
        				//return;
        		}
        	}
        }
        int crc;
        int i;
        byte[] crcBytes = crcData.toByteArray();

        crc = 0xFFFFFFFF; // initial contents of LFBSR

        int poly = 0xEDB88320; // reverse polynomial

        for (byte b : crcBytes) {
            int temp = (crc ^ b) & 0xff;

            // read 8 bits one at a time
            for (i = 0; i < 8; i++) {
                if ((temp & 1) == 1) {
                    temp = (temp >>> 1) ^ poly;
                } else {
                    temp = (temp >>> 1);
                }
            }

            crc = (crc >>> 8) ^ temp;
        }

        long tmpEndAddress = crcStartAddress + crcBytes.length;

        while (tmpEndAddress <= (crcEndAddress)) {
            int temp = (crc ^ 0xFF) & 0xff;

            for (i = 0; i < 8; i++) {
                if ((temp & 1) == 1) {
                    temp = (temp >>> 1) ^ poly;
                } else {
                    temp = (temp >>> 1);
                }
            }

            crc = (crc >>> 8) ^ temp;
            tmpEndAddress++;
        }

        // flip bits
        crc = crc ^ 0xffffffff;

        boolean result;
        
        result = (crc == crcValueInFile); 
        crcData.close();

        if(crcData2.size() > 0 && result == true)
        {
	        byte[] crc2Bytes = crcData2.toByteArray();
	
	        crc = 0xFFFFFFFF; // initial contents of LFBSR
	
	        int poly2 = 0xEDB88320; // reverse polynomial
	
	        for (byte b : crc2Bytes) {
	            int temp = (crc ^ b) & 0xff;
	
	            // read 8 bits one at a time
	            for (i = 0; i < 8; i++) {
	                if ((temp & 1) == 1) {
	                    temp = (temp >>> 1) ^ poly2;
	                } else {
	                    temp = (temp >>> 1);
	                }
	            }
	
	            crc = (crc >>> 8) ^ temp;
	        }
	
	        long tmp2EndAddress = crc2StartAddress + crc2Bytes.length;
	
	        while (tmp2EndAddress <= (crc2EndAddress)) {
	            int temp = (crc ^ 0xFF) & 0xff;
	
	            for (i = 0; i < 8; i++) {
	                if ((temp & 1) == 1) {
	                    temp = (temp >>> 1) ^ poly2;
	                } else {
	                    temp = (temp >>> 1);
	                }
	            }
	
	            crc = (crc >>> 8) ^ temp;
	        }
	        tmp2EndAddress++;
	
	        // flip bits
	        crc = crc ^ 0xffffffff;
	        
	        result &= (crc == crc2ValueInFile); 
	       /* System.out.println("Calculated CRC: " + String.format("0x%08X", crc)  +
	        		" In file :" + String.format("0x%08X", crc2ValueInFile) + " Matches :" + (crc==crc2ValueInFile));*/
	        crcData2.close();
        }
        end_time = System.nanoTime();
        log.debug("Time taken: " + ((end_time-start_time)/1000000));
        return result;
	}
	public DateTime getDownloadTime() {
		return downloadTime;
	}
	public void setDownloadTime(DateTime downloadTime) {
		this.downloadTime = downloadTime;
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

	
	public boolean getAbortFlag() {
		return abortFlag;
	}

	public void setAbortFlag(boolean abortFlag) {
		this.abortFlag = abortFlag;
	}

	private int readHexByteDataFromFile1(int chunkSize, String filePath) {
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
