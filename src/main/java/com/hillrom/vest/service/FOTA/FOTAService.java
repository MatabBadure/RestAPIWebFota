package com.hillrom.vest.service.FOTA;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minidev.json.JSONObject;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.exceptionhandler.HillromException;

@Service
@Transactional
public class FOTAService {

	private final Logger log = LoggerFactory.getLogger(FOTAService.class);

	public JSONObject processHexaToByteData(String HexaFilePath, Integer lines)
			throws HillromException {
		byte[] byteArray = new byte[lines-1];
		JSONObject jsonObject = new JSONObject();
		long count = 0;

		try {
			Path pp = FileSystems.getDefault().getPath(HexaFilePath,
					"193164_charger_mainboard.hex");
			FileInputStream fis = new FileInputStream(pp.toFile());
			int len;
			// Read bytes until EOF is encountered.
			do {
				String hexDataStr = "";
				len = fis.read(byteArray);

				hexDataStr = getDataInHexString(byteArray);

				BigInteger bigint = new BigInteger(hexDataStr, 16);

				StringBuilder sb = new StringBuilder();
				byte[] bytes = Base64.encodeInteger(bigint);
				for (byte b : bytes) {
					sb.append((char) b);
				}

				String encodedData = new String(sb.toString());
				log.debug("Ecoded bas64 data :" + encodedData);
				if (len == lines-1) {
					count = len;
					jsonObject.put("Base64 Encoded ", encodedData);
					jsonObject.put("ChunkSize:", count);

				}
				break;
			} while (len != -1);

			fis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error in Ecoded bas64 data :" + ex.getMessage());
		}
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

}
