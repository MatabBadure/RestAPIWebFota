package com.hillrom.vest.service.util.FOTA;

import static com.hillrom.vest.config.FOTA.FOTAConstants.ABORT;
import static com.hillrom.vest.config.FOTA.FOTAConstants.AMPERSAND;
import static com.hillrom.vest.config.FOTA.FOTAConstants.BUFFER_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.BUFFER_LEN_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CHUNK_NUMBER_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CHUNK_NUMBER_RAW;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CHUNK_SIZE_RAW;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CONNECTION_TYPE;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CRC_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_PARTNUMBER;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_SN;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEV_VER_RAW;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HANDLE_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HANDLE_RAW;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HEX;
import static com.hillrom.vest.config.FOTA.FOTAConstants.No;
import static com.hillrom.vest.config.FOTA.FOTAConstants.OK;
import static com.hillrom.vest.config.FOTA.FOTAConstants.RESULT_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.TOTAL_CHUNK;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.FOTA.FOTAInfo;
import com.hillrom.vest.web.rest.FOTA.dto.HandleHolder;
import com.hillrom.vest.web.rest.FOTA.dto.PartNoHolder;
@Service
@Transactional
public class CommonFOTAUtil {

	private static final Logger log = LoggerFactory
			.getLogger(CommonFOTAUtil.class);
	
	
	@Inject
	private FOTAServiceUtil utilService;

	public boolean validateCRC(String rawMessage) {

		log.debug("Inside  calculateCRC : ", rawMessage);

		int nCheckSum = 0;

		byte[] decoded = java.util.Base64.getDecoder().decode(rawMessage);

		int nDecodeCount = 0;
		for (; nDecodeCount < (decoded.length - 2); nDecodeCount++) {
			int nValue = (decoded[nDecodeCount] & 0xFF);
			nCheckSum += nValue;
		}

		System.out.format("Inverted Value = %d [0X%x] \r\n", nCheckSum,
				nCheckSum);

		nCheckSum = nCheckSum & 0xFFFF;

		int nMSB = decoded[nDecodeCount + 1] & 0xFF;
		int nLSB = decoded[nDecodeCount] & 0xFF;

		System.out.format("MSB = %d [0x%x]\r\n", nMSB, nMSB);
		System.out.format("LSB = %d [0x%x]\r\n", nLSB, nLSB);
		log.debug("Total Value = " + nCheckSum);
		nCheckSum = ((~nCheckSum) & 0xFFFF) + 1;
		System.out.format("Checksum Value = %d [0X%x] \r\n", nCheckSum,
				nCheckSum);

		String msb_digit = Integer.toHexString(nMSB);
		String lsb_digit = Integer.toHexString(nLSB);
		String checksum_num = Integer.toHexString(nCheckSum);
		checksum_num = ("0000" + checksum_num).substring(checksum_num.length());

		if (msb_digit.length() < 2)
			msb_digit = "0" + msb_digit;
		if (lsb_digit.length() < 2)
			lsb_digit = "0" + lsb_digit;

		log.debug("MSB : " + msb_digit + " " + "LSB : " + lsb_digit);
		log.debug("Checksum : " + checksum_num);

		if ((msb_digit + lsb_digit).equalsIgnoreCase(checksum_num)) {
			return true;
		} else {
			log.error("CRC VALIDATION FAILED :");
			return false;
		}
	}

	public String hexToAscii(String hexStr) {
		String str = "";
		StringBuilder output = new StringBuilder("");
		try {
			for (int i = 0; i < hexStr.length(); i += 2) {
				str = hexStr.substring(i, i + 2);
				output.append((char) Integer.parseInt(str, 16));
			}
		} catch (Exception ex) {

		}
		return new String(output.toString());
	}

	public String calculateCRC(String encodedString) {

		log.debug("Inside  calculateCRC : ", encodedString);

		int nCheckSum = 0;
		byte[] decoded = java.util.Base64.getDecoder().decode(encodedString);

		int nDecodeCount = 0;
		for (; nDecodeCount <= (decoded.length - 1); nDecodeCount++) {
			int nValue = (decoded[nDecodeCount] & 0xFF);
			nCheckSum += nValue;
		}

		System.out.format("Inverted Value = %d [0X%x] \r\n", nCheckSum,
				nCheckSum);
		nCheckSum = nCheckSum & 0xFFFF;
		log.debug("Total Value = " + nCheckSum);
		nCheckSum = ((~nCheckSum) & 0xFFFF) + 1;
		System.out.format("Checksum Value = %d [0X%x] \r\n", nCheckSum,
				nCheckSum);

		String checksum_num = Integer.toHexString(nCheckSum);
		checksum_num = ("0000" + checksum_num).substring(checksum_num.length());
		log.debug("Checksum : " + checksum_num);
		return toLittleEndian(checksum_num);

	}

	public String toLittleEndian(final String hex) {
		String hexLittleEndian = "";
		if (hex.length() % 2 != 0)
			return hexLittleEndian;
		for (int i = hex.length() - 2; i >= 0; i -= 2) {
			hexLittleEndian += hex.substring(i, i + 2);
		}
		return hexLittleEndian;
	}

	public String failedResponse(String finalResponseStr,
			String crsResultValue, String message, String resultPair,
			String crcPair) {
		crsResultValue = asciiToHex(message);
		resultPair = getResponePairResult();
		crcPair = getResponePair3();
		String crsRaw = resultPair.concat(crsResultValue).concat(crcPair);

		byte[] encodedCRC = java.util.Base64.getEncoder().encode(
				DatatypeConverter.parseHexBinary(crsRaw));
		String encodedString = new String(encodedCRC);
		log.debug("encodedString: " + encodedString);
		String crcValue = calculateCRC(encodedString);

		finalResponseStr = resultPair.concat(crsResultValue).concat(crcPair)
				.concat(crcValue);

		return finalResponseStr;
	}

	public String getResponePair3() {
		StringBuilder response = new StringBuilder();
		response.append(AMPERSAND);
		response.append(CRC_EQ);
		String responePair3 = asciiToHex(response.toString());
		log.debug("responePair3: " + responePair3);
		return responePair3;
	}

	public String getResponePairResult() {

		String getResponePairResult = asciiToHex(RESULT_EQ);
		log.debug("getResponePairResult: " + getResponePairResult);
		return getResponePairResult;
	}

	// Convert String to ASCII
	public String asciiToHex(String asciiValue) {
		char[] chars = asciiValue.toCharArray();
		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int) chars[i]));
		}
		return hex.toString();

	}

	public int hex2decimal(String chunkStr) {

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

	public String getDeviceVersion(String rawMessage) {

		byte[] getHandleByte = java.util.Base64.getDecoder().decode(rawMessage);
		int deviceIndex = returnMatch(getHandleByte, DEV_VER_RAW);
		log.debug("str1: " + deviceIndex);
		StringBuilder deviceRes = new StringBuilder();
		String device1 = Integer.toHexString(getHandleByte[deviceIndex] & 0xFF);
		String device2 = Integer
				.toHexString(getHandleByte[deviceIndex + 1] & 0xFF);
		String device3 = Integer
				.toHexString(getHandleByte[deviceIndex + 2] & 0xFF);
		String device4 = Integer
				.toHexString(getHandleByte[deviceIndex + 3] & 0xFF);

		device1 = ("00" + device1).substring(device1.length());
		device2 = ("00" + device2).substring(device2.length());
		device3 = ("00" + device3).substring(device3.length());
		device4 = ("00" + device4).substring(device4.length());
		deviceRes.append(device1);
		deviceRes.append(device2);
		deviceRes.append(device3);
		deviceRes.append(device4);
		// written new code
		String deviceVer = toLittleEndian(deviceRes.toString());
		log.debug("deviceVer: " + deviceVer);
		return deviceVer;
	}

	// To read non readable character
	public int returnMatch(byte[] inputArray, byte[] matchArray) {

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

	public String getBufferLen4HexByte(int bufferLen) {
		// Convert to hex
		String bufferLenHex = Integer.toHexString(bufferLen);
		// convert in two byte format
		bufferLenHex = ("00000000" + bufferLenHex).substring(bufferLenHex.length());
		// converting to little Endian
		String bufferInLsb = hexToAscii(asciiToHex(toLittleEndian(bufferLenHex)));
		return bufferInLsb;
	}

	public String getValuesFromRequest(String rawMessage, byte[] handleRaw) {

		byte[] rawMessageByte = java.util.Base64.getDecoder().decode(rawMessage);
		int valueIndex = returnMatch(rawMessageByte, handleRaw);
		log.debug("valueIndex: " + valueIndex);
		StringBuilder valuesRes = new StringBuilder();
		// handleRes.
		String index1 = Integer.toHexString(rawMessageByte[valueIndex] & 0xFF);
		String index2 = Integer
				.toHexString(rawMessageByte[valueIndex + 1] & 0xFF);
		String index3 = Integer
				.toHexString(rawMessageByte[valueIndex + 2] & 0xFF);
		String index4 = Integer
				.toHexString(rawMessageByte[valueIndex + 3] & 0xFF);
		index1 = ("00" + index1).substring(index1.length());
		index2 = ("00" + index2).substring(index2.length());

		index3 = ("00" + index3).substring(index3.length());
		index4 = ("00" + index4).substring(index4.length());
		valuesRes.append(index1);
		valuesRes.append(index2);
		valuesRes.append(index3);
		valuesRes.append(index4);
		// written new code
		String requestValue = toLittleEndian(valuesRes.toString());
		log.debug("requestValue: " + requestValue);
		return requestValue;
	}

	public String getInitReponsePair3() {
		StringBuilder response = new StringBuilder();
		response.append(AMPERSAND);
		response.append(BUFFER_EQ);
		String initResponsePair3 = asciiToHex(response.toString());
		return initResponsePair3;
	}

	public String getInitResponsePair2() {
		StringBuilder response = new StringBuilder();
		response.append(AMPERSAND);
		response.append(BUFFER_LEN_EQ);
		String initResponsePair3 = asciiToHex(response.toString());
		return initResponsePair3;
	}

	public String getChunkRaw(int totalChunks) {
		BigInteger toHex = new BigInteger(String.valueOf(totalChunks), 10);
		String totalChunkHexString = toHex.toString(16);
		totalChunkHexString = ("00000000" + totalChunkHexString)
				.substring(totalChunkHexString.length());
		// converting to little Indian
		String strTotalChunk = hexToAscii(asciiToHex(toLittleEndian((totalChunkHexString))));
		log.debug("totalChunks: " + totalChunks);
		return strTotalChunk;
	}

	public String getResponePair2() {
		StringBuilder response = new StringBuilder();
		response.append(AMPERSAND);
		response.append(TOTAL_CHUNK);
		String responePair2 = asciiToHex(response.toString());
		log.debug("responePair2: " + responePair2);
		return responePair2;
	}

	public String getResponePair1() {
		StringBuilder response = new StringBuilder();
		response.append(AMPERSAND);
		response.append(HANDLE_EQ);
		String responePair1 = asciiToHex(response.toString());
		log.debug("responePair1: " + responePair1);
		return responePair1;
	}

	/**
	 * Approve key in CRC32 calculations
	 * @param fotaInfo
	 * @param region1crc
	 * @param region2crc
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public boolean validateApprCRC32(FOTAInfo fotaInfo, String region1crc,
			String region2crc) throws Exception {

		boolean eof = false;
		boolean result = false;
		int recordIdx = 0;
		long upperAddress = 0;
		
		long crcStartAddress = 0;
		long crcEndAddress = 0;

		long crcValueInFile = 0;
		long dataStartAddress = 0;

		long crc2StartAddress = 0;
		long crc2EndAddress = 0;
		//long crc2LocationAddress = 0;

		long crc2ValueInFile = 0;
		long data2StartAddress = 0;

		ByteArrayOutputStream crcData = new ByteArrayOutputStream();
		ByteArrayOutputStream crcData2 = new ByteArrayOutputStream();

		int record_length;
		int record_address;
		byte[] record_data;

		FileInputStream fs = null;

		if (StringUtils.isNotEmpty(fotaInfo.getRegion1StartAddress()) && fotaInfo.getRegion1StartAddress().length() == 8) {
			crcStartAddress = Long.parseLong(fotaInfo.getRegion1StartAddress(),
					16);
		}
		if (StringUtils.isNotEmpty(fotaInfo.getRegion1EndAddress()) && fotaInfo.getRegion1EndAddress().length() == 8) {
			crcEndAddress = Long.parseLong(fotaInfo.getRegion1EndAddress(), 16);
		}
		if (StringUtils.isNotEmpty(fotaInfo.getRegion2StartAddress()) && fotaInfo.getRegion2StartAddress().length() == 8) {
			crc2StartAddress = Long.parseLong(
					fotaInfo.getRegion2StartAddress(), 16);
		}
		if (StringUtils.isNotEmpty(fotaInfo.getRegion2EndAddress()) && fotaInfo.getRegion2EndAddress().length() == 8) {
			crc2EndAddress = Long
					.parseLong(fotaInfo.getRegion2EndAddress(), 16);
		}
		
		if (StringUtils.isNotEmpty(region1crc) && region1crc.length() == 8 ) {
			crcValueInFile = Long.parseLong(toLittleEndian(region1crc),16);
		}
		if (StringUtils.isNotEmpty(region2crc) && region2crc.length() == 8) {
			crc2ValueInFile = Long.parseLong(toLittleEndian(region2crc),16);
		}
		fs = new FileInputStream(fotaInfo.getFilePath());

		InputStreamReader isr = new InputStreamReader(fs);
		BufferedReader rdr = new BufferedReader(isr);
		eof = false;
		recordIdx = 1;
		upperAddress = 0;
		String recordStr;
		while ((recordStr = rdr.readLine()) != null) {
			if (eof) {
				throw new Exception("Data after eof (" + recordIdx + ")");
			}

			if (!recordStr.startsWith(":")) {
				throw new Exception("Invalid Intel HEX record (" + recordIdx
						+ ")");
			}

			int lineLength = recordStr.length();
			byte[] hexRecord = new byte[lineLength / 2];

			int sum = 0;
			for (int i = 0; i < hexRecord.length; i++) {
				String num = recordStr.substring(2 * i + 1, 2 * i + 3);
				hexRecord[i] = (byte) Integer.parseInt(num, HEX);
				sum += hexRecord[i] & 0xff;
			}
			sum &= 0xff;

			if (sum != 0) {
				throw new Exception("Invalid checksum (" + recordIdx + ")");
			}

			record_length = hexRecord[0];
			if ((record_length + 5) != hexRecord.length) {
				throw new Exception("Invalid record length (" + recordIdx + ")");
			}
			record_data = new byte[record_length];
			System.arraycopy(hexRecord, 4, record_data, 0, record_length);

			record_address = ((hexRecord[1] & 0xFF) << 8)
					+ (hexRecord[2] & 0xFF);

			long addr = record_address | upperAddress;
			switch (hexRecord[3] & 0xFF) {
			case 0:
				for (byte c : record_data) {
					if (addr >= crcStartAddress && addr <= crcEndAddress) {
						if (dataStartAddress == 0) {
							dataStartAddress = addr;
						}
						crcData.write(c);
					}

					if (addr >= crc2StartAddress && addr <= crc2EndAddress) {
						if (data2StartAddress == 0) {
							data2StartAddress = addr;
						}
						crcData2.write(c);
					}
				}
				break;
			case 1:
				break;
			case 2:
				if (record_length == 2) {
					upperAddress = ((record_data[0] & 0xFF) << 12)
							+ (((record_data[1] & 0xFF)) << 4);
				} else {
					throw new Exception("Invalid SEG record (" + recordIdx
							+ ")");
				}
				break;
			case 4:
				if (record_length == 2) {
					upperAddress = ((record_data[0] & 0xFF) << 24)
							+ (((record_data[1] & 0xFF)) << 16);
				} else {
					throw new Exception("Invalid EXT_LIN record (" + recordIdx
							+ ")");
				}
				break;
			default:
				break;
			}
			recordIdx++;
		}
		;
		rdr.close();
		isr.close();
		fs.close();

		// CRC Calculation Table initialize
		int crc;
		int i;
		if (crcStartAddress != 0) {
			byte[] crcBytes = crcData.toByteArray();

			crc = 0xFFFFFFFF; // initial contents of LFBSR
			int poly = 0xEDB88320; // reverse polynomial

			for (byte b : crcBytes) {
				int temp = (crc ^ b) & 0xff;

				// read 8 bits one at a time
				for (i = 0; i < 8; i++) {
					if ((temp & 1) == 1)
						temp = (temp >>> 1) ^ poly;
					else
						temp = (temp >>> 1);
				}
				crc = (crc >>> 8) ^ temp;
			}

			long tmpEndAddress = dataStartAddress + crcBytes.length;
			while (tmpEndAddress <= (crcEndAddress)) {
				int temp = (crc ^ 0xFF) & 0xff;

				for (i = 0; i < 8; i++) {
					if ((temp & 1) == 1)
						temp = (temp >>> 1) ^ poly;
					else
						temp = (temp >>> 1);
				}
				crc = (crc >>> 8) ^ temp;
				tmpEndAddress++;
			}

			// flip bits
			crc = crc ^ 0xffffffff;
			//crcValueInFile = ((int) c & 0xFF);
			result = (crc == (int) (long) crcValueInFile);
			crcData.close();
			log.debug("Calculated Region1CRC32: "
					+ (String.format("0x%08X", crc)) + "In file :"
					+ (String.format("0x%08X", (crcValueInFile))));

		}
		if (crc2StartAddress != 0 && result == true) {
			byte[] crc2Bytes = crcData2.toByteArray();

			crc = 0xFFFFFFFF; // initial contents of LFBSR
			int poly2 = 0xEDB88320; // reverse polynomial

			for (byte b : crc2Bytes) {
				int temp = (crc ^ b) & 0xff;

				// read 8 bits one at a time
				for (i = 0; i < 8; i++) {
					if ((temp & 1) == 1)
						temp = (temp >>> 1) ^ poly2;
					else
						temp = (temp >>> 1);
				}
				crc = (crc >>> 8) ^ temp;
			}

			long tmp2EndAddress = data2StartAddress + crc2Bytes.length;
			while (tmp2EndAddress <= (crc2EndAddress)) {
				int temp = (crc ^ 0xFF) & 0xff;

				for (i = 0; i < 8; i++) {
					if ((temp & 1) == 1)
						temp = (temp >>> 1) ^ poly2;
					else
						temp = (temp >>> 1);
				}
				crc = (crc >>> 8) ^ temp;
				tmp2EndAddress++;
			}
			// flip bits
			crc = crc ^ 0xffffffff;
			log.debug("Calculated Region2CRC32: "
					+ (String.format("0x%08X", crc)) + "In file :"
					+ (String.format("0x%08X", (crc2ValueInFile))));
			result &= (crc == (int) (long) crc2ValueInFile);
			crcData2.close();
		}
		return result;

	}

	/**
	 * getChunk
	 * @param rawMessage
	 * @return
	 */
	public String getChunkSize(String rawMessage) {

		byte[] getChunkByte = java.util.Base64.getDecoder().decode(rawMessage);
		int chunkByteIndex = returnMatch(getChunkByte, CHUNK_SIZE_RAW);
		log.debug("chunkByteIndex: " + chunkByteIndex);
		int chunkSizeValue = getChunkByte[chunkByteIndex] & 0xFF;
		int chunkSizeValue1 = getChunkByte[chunkByteIndex + 1] & 0xFF;
		int chunkSizeValue2 = getChunkByte[chunkByteIndex + 2] & 0xFF;
		int chunkSizeValue3 = getChunkByte[chunkByteIndex + 3] & 0xFF;

		String chunkSize1 = Integer.toHexString(chunkSizeValue);
		String chunkSize2 = Integer.toHexString(chunkSizeValue1);
		String chunkSize3 = Integer.toHexString(chunkSizeValue2);
		String chunkSize4 = Integer.toHexString(chunkSizeValue3);

		chunkSize1 = ("00" + chunkSize1).substring(chunkSize1.length());
		chunkSize2 = ("00" + chunkSize2).substring(chunkSize2.length());
		chunkSize3 = ("00" + chunkSize3).substring(chunkSize3.length());
		chunkSize4 = ("00" + chunkSize4).substring(chunkSize4.length());

		StringBuilder sb = new StringBuilder();
		sb.append(chunkSize1);
		sb.append(chunkSize2);
		sb.append(chunkSize3);
		sb.append(chunkSize4);

		String littleEndianChunk = toLittleEndian(sb.toString());
		return littleEndianChunk;

	}

	/**
	 * getDownLoadTime
	 * @param downloadEndDateTime
	 * @param downloadStartDateTime
	 * @return
	 */
	public String getDownLoadTime(DateTime downloadEndDateTime,
			DateTime downloadStartDateTime) {
		long elapsed = (downloadEndDateTime.getMillis())
				- (downloadStartDateTime.getMillis());
		String totalDownloadTime = "";
		if(elapsed>0){

			int hours = (int) Math.floor(elapsed / 3600000);

			int minutes = (int) Math.floor((elapsed - hours * 3600000) / 60000);

			int seconds = (int) Math
					.floor((elapsed - hours * 3600000 - minutes * 60000) / 1000);

			
			String hr =	("00"+ String.valueOf(hours)).substring(String.valueOf(hours).length());
			
			String min =	("00"+ String.valueOf(minutes)).substring(String.valueOf(minutes).length());
			
			String sec =	("00"+ String.valueOf(seconds)).substring(String.valueOf(seconds).length());
			
			totalDownloadTime = hr.concat(":")
					.concat(String.valueOf(min)).concat(":")
					.concat(String.valueOf(sec));

			return totalDownloadTime;
		}else{
			totalDownloadTime = "00:00:00";
			return totalDownloadTime;
		}

	}

	public String getOldHandle(Map<String, HandleHolder> handleHolderBin, String devSN) {
		Set<String> keys = handleHolderBin.keySet();
		String handleId = null;
        for (String key : keys) {
        	log.debug("Handle HolderBin Keys:" +handleHolderBin.get(key));
            HandleHolder holder1 = new HandleHolder();
            holder1 = handleHolderBin.get(key);
            if (holder1.getDeviceSerialNumber().equals(devSN)) {
            	handleId = holder1.getHandleId();
            	return handleId;
            }
        }
	    return handleId;
	}
	
	

	//FOTA CR
	public String getChunkWithChunkNumber(String rawMessage, String handleId, Map<String, HandleHolder> handleHolderBin, Map<String, PartNoHolder> partNosBin) {
		
		String resultPair = new String();
		String handlePair = new String();
		String bufferLenPair = new String();
		String bufferPair = new String();
		String crcPair = new String();
		String finalResponseStr = new String();
		String crsResultValue = new String();
		//Initially 
		HandleHolder holder = new HandleHolder();
		PartNoHolder partNoHolder = new PartNoHolder();
		
		//Get Handle from the request
		handleId = getValuesFromRequest(rawMessage,HANDLE_RAW);
		log.debug("Send Chunk With chunk Number Handle Id ="+handleId);
		
		//Get handle object based on handleId
		holder = handleHolderBin.get(handleId);
		//Frame key to get partNumber details
		if(Objects.nonNull(holder)){
			String storePartNoKey = holder.getPartNo().concat(":").concat(holder.getUpdatedSoftVersion()).concat(":").concat(String.valueOf(holder.getChunkSize()));
			partNoHolder =  partNosBin.get(storePartNoKey);
			log.debug("Send  Chunk with chunk number Framed Part No key:"+storePartNoKey+"Part No Obj="+partNoHolder);
		}else{
			//Added No Response
			finalResponseStr = failedResponse(finalResponseStr,crsResultValue,No,resultPair,crcPair);
			return finalResponseStr;
		}
		//Check If the file is no longer the latest version 
		if(Objects.nonNull(partNoHolder)){
			if(partNoHolder.getAbortFlag() == false){
			//Get Chunk Number value from the request
			String chunkNumberStr = getValuesFromRequest(rawMessage,CHUNK_NUMBER_RAW);
			//Hex to decimal
			int chunkNumber = hex2decimal(chunkNumberStr);
			if(chunkNumber < partNoHolder.getTotalChunk() ){
				//Get the particular chunk from the based chunk count
				String zeroChunk = partNoHolder.getFileChunks().get(chunkNumber);
				//Start time for only when 0th chunk number
				if(chunkNumber == 0){
					holder.setDownloadStartDateTime(new DateTime());
					//Save device details to DB
					utilService.saveInprogressDeviceDetails(holder);
					handleHolderBin.put(handleId, holder);
				}
				log.debug("Send  Chunk with chunk number Handle Id ="+handleId);
				log.debug("Chunk Number ="+chunkNumber);
				log.debug("Send  Chunk with chunk number ="+holder.getChunkSize());
				log.debug("Send  Chunk with chunk number value in Hex Str:"+zeroChunk);
				//Zero the Chunk in raw format
				String buffer = hexToAscii(asciiToHex(zeroChunk));
				log.debug("buffer Encoded:" + buffer);
				
				//Chunk size in hex byte
				int bufferLen = zeroChunk.length() / 2;
				log.debug("Send  Chunk with chunk number bufferLen:" + bufferLen);
				log.debug("Send  Chunk with chunk number :" + chunkNumber);
				
				//result pair1
				resultPair = getResponePairResult();
				
				//Result OK send chunk with chunk number
				crsResultValue = asciiToHex(OK);
				
				//handlePair for send chunk number with chunk number Pair1 HANDLE_EQ
				handlePair = getResponePair1();
				
				//Handle in raw format(handle Value)
				String handleIdRaw = hexToAscii(asciiToHex(toLittleEndian(handleId)));
				
				//Chunk number pair
				String chunkNumberPair = getChunkNumberResponsePair();
				
				//Chunk number raw
				String chunkNumberRaw = hexToAscii(asciiToHex(toLittleEndian(chunkNumberStr)));
				
				bufferLenPair = getInitResponsePair2();
				
				String bufferLenRaw =  getBufferLen4HexByte(bufferLen);
				
				//bufferPair send chunk number with chunk number Pair2 BUFFER_EQ
				bufferPair = getInitReponsePair3();
				
				//crcPair pair4 send chunk number with chunk number crc
				crcPair = getResponePair3();
				
				String crsRaw = resultPair.concat(crsResultValue).concat(handlePair).concat(handleIdRaw).concat(chunkNumberPair)
						.concat(chunkNumberRaw).concat(bufferLenPair).concat(bufferLenRaw).concat(bufferPair).concat(buffer).concat(crcPair);

				byte[] encodedCRC = java.util.Base64.getEncoder().encode(DatatypeConverter.parseHexBinary(crsRaw));
				String encodedString = new String(encodedCRC);
				log.debug(" EncodedString: " + encodedString);
				String crcstr = calculateCRC(encodedString);
				log.debug(" CRC value:" + crcstr);
				// Final String
				finalResponseStr = resultPair.concat(crsResultValue)
						.concat(handlePair).concat(handleIdRaw).concat(chunkNumberPair)
						.concat(chunkNumberRaw).concat(bufferLenPair)
						.concat(bufferLenRaw).concat(bufferPair).concat(buffer)
						.concat(crcPair).concat(crcstr);
				log.debug(" Chunk Number:" + chunkNumber);
				return finalResponseStr;
			}else{
				//Added No Response
				finalResponseStr = failedResponse(finalResponseStr,crsResultValue,No,resultPair,crcPair);
				return finalResponseStr;
			}
		}else{
			//Added ABORT Response
			finalResponseStr = failedResponse(finalResponseStr,crsResultValue,ABORT,resultPair,crcPair);
			return finalResponseStr;
			}
		}else{
			//Added No Response
			finalResponseStr = failedResponse(finalResponseStr,crsResultValue,No,resultPair,crcPair);
			return finalResponseStr;
		}
	}

	private String getChunkNumberResponsePair() {
		StringBuilder response = new StringBuilder();
		response.append(AMPERSAND);
		response.append(CHUNK_NUMBER_EQ);
		String responePair1 = asciiToHex(response.toString());
		log.debug("responePair1: " + responePair1);
		return responePair1;
	}
	/*
	 * getHandleHolderValues for spawned object
	 */
	public HandleHolder getHandleHolderValuesFromPartNo(PartNoHolder partNoHolder, Map<String, String> fotaJsonData, FOTAInfo fotaInfo, String reqDev, DateTime reqReleaseDate) {
		HandleHolder holder = new HandleHolder();
		holder.setCurrentChunk(String.valueOf(0));
		holder.setPartNo(partNoHolder.getPart_No());
		holder.setChunkSize(partNoHolder.getChunkSize());
		holder.setFotaInfoId(fotaInfo.getId());
		holder.setDeviceSerialNumber(fotaJsonData
				.get(DEVICE_SN));
		holder.setConnectionType(fotaJsonData
				.get(CONNECTION_TYPE));
		holder.setDeviceSoftwareVersion(reqDev);
		//request Device software date
		holder.setDeviceSoftwareDateTime(reqReleaseDate);
		holder.setUpdatedSoftVersion(fotaInfo.getSoftVersion());
		holder.setCheckupdateDateTime(new DateTime());
		holder.setPreviousChunkTransStatus("CheckUpdate");
		//added new stmt
		holder.setSoftwareVersion(fotaInfo.getSoftVersion());
		return holder;
	}

	/*
	 * getHandleHolderValues for new object
	 */
	public HandleHolder getHandleHolderValuesForNewPartNo(int chunkSize,
			Map<String, String> fotaJsonData, FOTAInfo fotaInfo, String reqDev,
			DateTime reqReleaseDate) {

		HandleHolder holder = new HandleHolder();
		holder.setCurrentChunk(String.valueOf(0));
		holder.setPartNo(fotaJsonData
				.get(DEVICE_PARTNUMBER));
		holder.setChunkSize(chunkSize);
		holder.setFotaInfoId(fotaInfo.getId());
		holder.setDeviceSerialNumber(fotaJsonData
				.get(DEVICE_SN));
		holder.setConnectionType(fotaJsonData
				.get(CONNECTION_TYPE));
		holder.setDeviceSoftwareVersion(reqDev);
		//Request Device software date
		holder.setDeviceSoftwareDateTime(reqReleaseDate);
		holder.setUpdatedSoftVersion(fotaInfo.getSoftVersion());
		holder.setCheckupdateDateTime(new DateTime());
		holder.setPreviousChunkTransStatus("CheckUpdate");
		//added new stmt
		holder.setSoftwareVersion(fotaInfo.getSoftVersion());
		return holder;
	}
	
	
}
