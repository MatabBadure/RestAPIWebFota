package com.hillrom.vest.web.rest.FOTA.dto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
	private String devSN;
	private String version_No;
	private DateTime downloadTime;
	private int totalChunk;
	private int chunkSize;
	private boolean abortFlag;
	private Map<Integer, String> fileChunks = null;
	private static final int HEX = 16;

	public PartNoHolder() {
		// Default constructor
	}
	//Parameterized constructor
	public PartNoHolder(int chunkSize, FOTAInfo fotaInfo) throws IOException {
		totalChunk = readHexByteDataFromFile(chunkSize, fotaInfo);		
	}
	//Reading file and dividing chunks based on chunk size
	private int readHexByteDataFromFile(int chunkSize, FOTAInfo fotaInfo) throws IOException {
		String hexDataStr = null;
		String[] output = null;
		int ctr = 0;
			Path pp = FileSystems.getDefault().getPath(fotaInfo.getFilePath());
			hexDataStr = new String(Files.readAllBytes(pp)).replace(":", "")
					.replace("\n", "").replace("\r", "");
			// Splitting in hex format
			output = hexDataStr.split("(?<=\\G.{" + (chunkSize * 2) + "})");
			// Storing chunks into hash map
			fileChunks = new LinkedHashMap<>();
			for (String str : output) {
				fileChunks.put(ctr++, str);
			}
			totalChunk = fileChunks.size();
		return totalChunk;
	}
	//Check CRC 32 after spawned class
	public boolean checkCRC32(FOTAInfo fotaInfo) {
		boolean result = false;
		ByteArrayOutputStream crcData = new ByteArrayOutputStream();
		ByteArrayOutputStream crcData2 = new ByteArrayOutputStream();
		try {
			long start_time = System.nanoTime();
			long end_time = 0;

			int totalChunk = 0;
			byte state = 0;

			int data_cnt = 0;
			int data_size = 0;
			int checksum = 0;
			long addr = 0;
			long upperaddr = 0;
			int record_type = 0;
			byte[] tempDataBytes = null;

			long crcStartAddress = 0;
			long crcEndAddress = 0;
			long crcLocationAddress = 0;
			int crcValueInFile = 0;
			long crc2StartAddress = 0;
			long crc2EndAddress = 0;
			long crc2LocationAddress = 0;
			int crc2ValueInFile = 0;

			if (StringUtils.isNotEmpty(fotaInfo.getRegion1StartAddress()) && fotaInfo.getRegion1StartAddress().length() == 8) {
				crcStartAddress = Long.parseLong(
						fotaInfo.getRegion1StartAddress(), 16);
			}
			if (StringUtils.isNotEmpty(fotaInfo.getRegion1EndAddress()) && fotaInfo.getRegion1EndAddress().length() == 8) {
				crcEndAddress = Long.parseLong(fotaInfo.getRegion1EndAddress(),
						16);
			}

			if (StringUtils.isNotEmpty(fotaInfo.getRegion1CRCLocation()) && fotaInfo.getRegion1CRCLocation().length() == 8) {
				crcLocationAddress = Long.parseLong(
						fotaInfo.getRegion1CRCLocation(), 16);
			}
			if (StringUtils.isNotEmpty(fotaInfo.getRegion2StartAddress()) && fotaInfo.getRegion2StartAddress().length() == 8) {
				crc2StartAddress = Long.parseLong(
						fotaInfo.getRegion2StartAddress(), 16);
			}
			if (StringUtils.isNotEmpty(fotaInfo.getRegion2EndAddress()) && fotaInfo.getRegion2EndAddress().length() == 8) {
				crc2EndAddress = Long.parseLong(
						fotaInfo.getRegion2EndAddress(), 16);
			}

			if (StringUtils.isNotEmpty(fotaInfo.getRegion2CRCLocation()) && fotaInfo.getRegion2CRCLocation().length() == 8) {
				crc2LocationAddress = Long.parseLong(
						fotaInfo.getRegion2CRCLocation(), 16);
			}

			totalChunk = fileChunks.size();

			int currChunk = 0;
			while (currChunk < totalChunk) { // Get the chunks based on index
				String chunkStr = fileChunks.get(currChunk);
				byte[] chunkBytes = new byte[chunkStr.length() / 2];
				for (int chunkByteCnt = 0; chunkByteCnt < chunkBytes.length; chunkByteCnt++) {
					String num = chunkStr.substring((2 * chunkByteCnt),
							(2 * chunkByteCnt) + 2);
					chunkBytes[chunkByteCnt] = (byte) Integer
							.parseInt(num, HEX);
				}
				currChunk++;

				// Parse Bytes
				for (int chunkByteCnt = 0; chunkByteCnt < chunkBytes.length; chunkByteCnt++) {
					byte b = chunkBytes[chunkByteCnt];
					switch (state) {
					case 0:
						data_cnt = 0;
						data_size = b;
						if (data_size > 0) {
							tempDataBytes = new byte[data_size];
						}
						checksum = b;
						state = 1;
						break;
					case 1: {
						addr = (b & 0xFF);
						checksum += b;
						state = 2;
					}
						break;
					case 2: {
						addr <<= 8;
						addr |= (b & 0xFF);
						checksum += b;
						state = 3;
					}
						break;
					case 3: {
						record_type = b;
						checksum += b;
						switch (record_type) {
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
						if (data_size > 0) {
							state = 4;
						} else {
							state = 5;
						}
					}
						break;
					case 4: {
						tempDataBytes[data_cnt] = b;
						checksum += b;
						data_cnt++;
						if (data_cnt == data_size) {
							state = 5;
						} else {
							state = 4;
						}
					}
						break;
					case 5:
						byte tmpSum = (byte) ((~(checksum) + 1) & 0xFF);
						if (tmpSum != b) {
							log.error("CRC not matching" + checksum + " " + b);
							return false;
						}

						addr = addr | upperaddr;

						switch (record_type) {
						case 0:

							long tmpAddr = addr;
							long tmp2Addr = addr;

							for (byte c : tempDataBytes) {
								if ((tmpAddr >= crcLocationAddress)
										&& (tmpAddr < (crcLocationAddress + 4))) {
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

								if ((addr >= crcStartAddress)
										&& (addr <= crcEndAddress)) {
									crcData.write(c);
								}

								if ((tmp2Addr >= crc2LocationAddress)
										&& (tmp2Addr < (crc2LocationAddress + 4))) {
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

								if ((addr >= crc2StartAddress)
										&& (addr <= crc2EndAddress)) {
									crcData2.write(c);
								}
							}

							break;

						case 1:
							break;

						case 2:

							if (data_size == 2) {
								upperaddr = ((tempDataBytes[0] & 0xFF) << 12)
										+ ((tempDataBytes[1] & 0xFF) << 4);
							} else {
								log.error("Invalid SEG record");
								return false;
							}

							break;

						case 4:

							if (data_size == 2) {
								upperaddr = ((tempDataBytes[0] & 0xFF) << 24)
										+ ((tempDataBytes[1] & 0xFF) << 16);
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
						// return;
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

			result = (crc == crcValueInFile);

			if (crcData2.size() > 0 && result == true) {
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
					tmp2EndAddress++;
				}

				// flip bits
				crc = crc ^ 0xffffffff;

				result &= (crc == crc2ValueInFile);
			}
			end_time = System.nanoTime();
			log.debug("Time taken: " + ((end_time - start_time) / 1000000));

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (crcData != null) {
					crcData.close();
				}
				if (crcData2 != null) {
					crcData2.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public String getDevSN() {
		return devSN;
	}
	public void setDevSN(String devSN) {
		this.devSN = devSN;
	}
	public DateTime getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(DateTime downloadTime) {
		this.downloadTime = downloadTime;
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

	/*public DateTime getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(DateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
	}*/

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

}
