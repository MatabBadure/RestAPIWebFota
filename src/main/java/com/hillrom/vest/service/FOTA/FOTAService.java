package com.hillrom.vest.service.FOTA;
import static com.hillrom.vest.config.FOTA.FOTAConstants.ABORTED;
import static com.hillrom.vest.config.FOTA.FOTAConstants.ABORTED_LIST;
import static com.hillrom.vest.config.FOTA.FOTAConstants.ALL;
import static com.hillrom.vest.config.FOTA.FOTAConstants.AMPERSAND;
import static com.hillrom.vest.config.FOTA.FOTAConstants.BUFFER_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.BUFFER_LEN_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CHUNK_SIZE_RAW;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CONNECTION_TYPE;
import static com.hillrom.vest.config.FOTA.FOTAConstants.CRC_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_PARTNUMBER;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_SN;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEV_VER_RAW;
import static com.hillrom.vest.config.FOTA.FOTAConstants.FAILURE_LIST;
import static com.hillrom.vest.config.FOTA.FOTAConstants.FOTA_ADMIN;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HANDLE_EQ;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HANDLE_RAW;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HEX;
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
import static com.hillrom.vest.config.FOTA.FOTAConstants.SOFT_VER_DATE;
import static com.hillrom.vest.config.FOTA.FOTAConstants.SUCCESS_LIST;
import static com.hillrom.vest.config.FOTA.FOTAConstants.TOTAL_CHUNK;
import static com.hillrom.vest.config.FOTA.FOTAConstants.YES;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.FOTA.FOTADeviceFWareUpdate;
import com.hillrom.vest.domain.FOTA.FOTAInfo;
import com.hillrom.vest.repository.FOTA.FOTADeviceRepository;
import com.hillrom.vest.repository.FOTA.FOTARepository;
import com.hillrom.vest.repository.FOTA.FOTARepositoryUtils;
import com.hillrom.vest.service.MailService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.FOTA.FOTAParseUtil;
import com.hillrom.vest.web.rest.FOTA.dto.ApproverCRCDto;
import com.hillrom.vest.web.rest.FOTA.dto.CRC32Dto;
import com.hillrom.vest.web.rest.FOTA.dto.FOTADeviceDto;
import com.hillrom.vest.web.rest.FOTA.dto.FOTAInfoDto;
import com.hillrom.vest.web.rest.FOTA.dto.HandleHolder;
import com.hillrom.vest.web.rest.FOTA.dto.PartNoHolder;
@Service
@Transactional
public class FOTAService {

	private final Logger log = LoggerFactory.getLogger(FOTAService.class);
	@Inject
	private FOTARepository fotaRepository;
	
	@Inject
	private FOTADeviceRepository fotaDeviceRepository;
	
	@Inject
	private FOTARepositoryUtils fotaRepositoryUtils;
	
	@Inject
    private MailService mailService;
	
	private int bufferLen = 0;
	private String buffer = null;
	
	//Dynamic part number
	private static Map<String,PartNoHolder> partNosBin = new LinkedHashMap<String, PartNoHolder>();
	private static Map<String,HandleHolder> handleHolderBin = new LinkedHashMap<String, HandleHolder>();
	private PartNoHolder partNoHolder;
	
	@Transactional
	public String FOTAUpdate(String rawMessage) throws Exception {
		int countInt = 0;
		String decoded_string = "";
		String resultPair = "";
		String handlePair = "";
		String totalChunkPair = "";
		String bufferLenPair = "";
		String bufferPair = "";
		String crcPair = "";
		String finalResponseStr = new String();

		Map<String, String> fotaJsonData = new LinkedHashMap<String, String>();
		// Decoding raw data
		decoded_string = decodeRawMessage(rawMessage);
		// Parsing into key value pair
		fotaJsonData = FOTAParseUtil
				.getFOTAJsonDataFromRawMessage(decoded_string);
		// Global handler
		String crsResultValue = "";
		
		// Checking if request Type is 01 & //Checking if request Type is 02
		if(validateCRC(rawMessage)){
			//crcResult = "Yes";
			crsResultValue = FOTAParseUtil.asciiToHex(YES);
			String handleId = "";
			boolean softDeleteFlag = false;
			boolean activePublishedFlag = true;
			
			//check Update request.
			if (fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE1)) {
				//Get active pending FOTA details from the DB 
				FOTAInfo fotaInfo = fotaRepository.FOTAByPartNumber(fotaJsonData.get(DEVICE_PARTNUMBER),softDeleteFlag,activePublishedFlag);
				
				// Date formating which is from request
				SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(
						"MMddyy");
				simpleDateFormat1.setLenient(false);
				Date date3 = simpleDateFormat1.parse(fotaJsonData
						.get(SOFT_VER_DATE));
				// Release date from request
				DateTime reqReleaseDate = new DateTime(date3);
				//Check if null or no record exist and send response No to device
				if(fotaInfo != null){
					// Get release date from DB
					DateTime dbRelaseDate = fotaInfo.getReleaseDate();
					// Get Software version from request
					String reqDev = getDeviceVersion(rawMessage);
					if ((fotaInfo.getDevicePartNumber().equals(
							fotaJsonData.get(DEVICE_PARTNUMBER)) && (Integer
							.valueOf(fotaInfo.getSoftVersion()) > Integer
							.valueOf(reqDev)))
							|| (reqDev.equals(fotaInfo.getSoftVersion()) && dbRelaseDate
									.isAfter(reqReleaseDate))) {

						int totalChunks = 0;
						//Generate Handle
						handleId = getHandleNumber();
						// Get Chunk Size from request
						String chunkStr = getChunk(rawMessage);
						// Decimal conversion
						int chunkSize = FOTAParseUtil.hex2decimal(chunkStr);
						// PartNumber:Chunk Size
						String storeChunk = fotaJsonData.get(DEVICE_PARTNUMBER)
								.concat(":").concat(fotaInfo.getSoftVersion()).concat(":").concat(String.valueOf(chunkSize));
						boolean crcValid = false;
						partNoHolder = null;
						for(String key : partNosBin.keySet())
						{
							if(key.contains(storeChunk))
							{
								partNoHolder = partNosBin.get(storeChunk);
								
								if(partNoHolder.getAbortFlag()==false &&  partNoHolder.getChunkSize() == chunkSize)
								{
									break;
								}
								else
								{
									partNoHolder = null;
								}
							}
						}
						if (partNoHolder != null) {
							crcValid = partNoHolder.checkCRC32(fotaInfo);
							if ( crcValid == true) {
								// Initially
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
								handleId = getHandleNumber();
								handleHolderBin.put(handleId, holder);
							} else {
								//Remove failed CRC spawned object
								//Initially 
								HandleHolder holder = new HandleHolder();
								//Get handle object based on handleId
								holder = handleHolderBin.get(handleId);
								//Frame key to get partNumber details
								String storeChunk1 = holder.getPartNo().concat(":").concat(holder.getSoftwareVersion()).concat(":").concat(String.valueOf(holder.getChunkSize()));
								//partNoHolder =  partNosBin.get(storeChunk1);
								partNosBin.remove(storeChunk1);
								
								//Send email notification for CRC validation failed
								sendCRCFailedNotification();
								//Abort case
								partNoHolder.setAbortFlag(true);
								
								
							}
						} else {
							partNoHolder = new PartNoHolder(chunkSize, fotaInfo);
							crcValid = partNoHolder.checkCRC32(fotaInfo);
							if ( crcValid == true)
							{
								partNoHolder.setChunkSize(chunkSize);
								partNoHolder.setPart_No(fotaJsonData
										.get(DEVICE_PARTNUMBER));
								partNoHolder.setVersion_No(fotaInfo
										.getSoftVersion());
								partNoHolder.setEffectiveDate(new DateTime());
								// PartNo with Chuck size
								partNosBin.put(storeChunk, partNoHolder);
								// Initially
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
								//Request Device software date
								holder.setDeviceSoftwareDateTime(reqReleaseDate);
								holder.setUpdatedSoftVersion(fotaInfo.getSoftVersion());
								holder.setCheckupdateDateTime(new DateTime());
								holder.setPreviousChunkTransStatus("CheckUpdate");
								//added new stmt
								holder.setSoftwareVersion(fotaInfo.getSoftVersion());
								handleId = getHandleNumber();
								//spawned object time capture to identify the ideal download time to clean for performance
								holder.setSpwanedObject(new DateTime());
								handleHolderBin.put(handleId, holder);
							} else {
								//Initially 
								HandleHolder holder = new HandleHolder();
								//Get handle object based on handleId
								holder = handleHolderBin.get(handleId);
								//Frame key to get partNumber details
								String storeChunk2 = holder.getPartNo().concat(":").concat(holder.getSoftwareVersion()).concat(":").concat(String.valueOf(holder.getChunkSize()));
								partNosBin.remove(storeChunk2);
								//Send email notification for CRC validation failed
								sendCRCFailedNotification();
								//Abort case
								partNoHolder.setAbortFlag(true);
							}
						}

						if (crcValid == false || partNoHolder.getAbortFlag()==true) {
							crsResultValue = FOTAParseUtil.asciiToHex("No");
							resultPair = getResponePairResult();
							crcPair = getResponePair3();
							String crsRaw = resultPair.concat(crsResultValue)
									.concat(crcPair);

							byte[] encodedCRC = java.util.Base64.getEncoder()
									.encode(DatatypeConverter
											.parseHexBinary(crsRaw));
							String encodedString = new String(encodedCRC);
							log.debug("encodedString: " + encodedString);
							String crcValue = calculateCRC(encodedString);

							finalResponseStr = resultPair
									.concat(crsResultValue).concat(crcPair)
									.concat(crcValue);
						} else {
							totalChunks = partNoHolder.getTotalChunk();
							// Response pair1
							resultPair = getResponePairResult();

							handlePair = getResponePair1();

							// Handle in raw format
							String handleIdRaw = FOTAParseUtil.hexToAscii(FOTAParseUtil.asciiToHex(FOTAParseUtil.toLittleEndian((handleId))));

							// Response pair2
							totalChunkPair = getResponePair2();

							// Total chunk in raw format
							String totalChunkRaw = getChunkRaw(totalChunks);

							// Response pair3 crc
							crcPair = getResponePair3();

							// CRC calculation
							String crcInput = resultPair.concat(crsResultValue)
									.concat(handlePair).concat(handleIdRaw)
									.concat(totalChunkPair)
									.concat(totalChunkRaw).concat(crcPair);

							byte[] encodedCRC = java.util.Base64.getEncoder()
									.encode(DatatypeConverter
											.parseHexBinary(crcInput));
							String encodedString = new String(encodedCRC);
							log.debug("encodedString: " + encodedString);

							String crcstr = calculateCRC(encodedString);
							// Final response String
							finalResponseStr = getAllResponseCheckUpdate(
									resultPair, crsResultValue, handlePair,
									handleIdRaw, totalChunkPair, totalChunkRaw,
									crcPair, crcstr);
							log.debug("finalResponseStr: " + finalResponseStr);
						}
					}else{
						crsResultValue = FOTAParseUtil.asciiToHex("No");
						resultPair = getResponePairResult();
						crcPair = getResponePair3();
						String crsRaw = resultPair.concat(crsResultValue).concat(
								crcPair);

						byte[] encodedCRC = java.util.Base64.getEncoder().encode(
								DatatypeConverter.parseHexBinary(crsRaw));
						String encodedString = new String(encodedCRC);
						log.debug("encodedString: " + encodedString);
						String crcValue = calculateCRC(encodedString);
						
						finalResponseStr = resultPair.concat(crsResultValue).concat(crcPair).concat(crcValue);
						}
				}else{
				crsResultValue = FOTAParseUtil.asciiToHex("No");
				resultPair = getResponePairResult();
				crcPair = getResponePair3();
				String crsRaw = resultPair.concat(crsResultValue).concat(
						crcPair);

				byte[] encodedCRC = java.util.Base64.getEncoder().encode(
						DatatypeConverter.parseHexBinary(crsRaw));
				String encodedString = new String(encodedCRC);
				log.debug("encodedString: " + encodedString);
				String crcValue = calculateCRC(encodedString);
				
				finalResponseStr = resultPair.concat(crsResultValue).concat(crcPair).concat(crcValue);
				}
			}else if(fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE2)){

					//Get handle from request
					handleId = getHandleFromRequest(rawMessage);
					//Initially 
					HandleHolder holder = new HandleHolder();
					//Get handle object based on handleId
					holder = handleHolderBin.get(handleId);
					//Frame key to get partNumber details
					String storeChunk = holder.getPartNo().concat(":").concat(holder.getSoftwareVersion()).concat(":").concat(String.valueOf(holder.getChunkSize()));
					partNoHolder =  partNosBin.get(storeChunk);
					
				if(partNoHolder.getAbortFlag() == false){
					
					if (fotaJsonData.get(PREV_REQ_STATUS).equals(INIT)) 
					{
						//Get current chunk count from handle holder object
						int chunkCount = Integer.parseInt(holder.getCurrentChunk());
						
						//Get the particular chunk from the based chunk count
						String zeroChunk = partNoHolder.getFileChunks().get(chunkCount);
						
						holder.setCurrentChunk(holder.getCurrentChunk());
						holder.setPreviousChunkTransStatus("INIT");
						holder.setDownloadStartDateTime(new DateTime());
						
						handleHolderBin.put(handleId, holder);
						//Zero the Chunk in raw format
						buffer = FOTAParseUtil.hexToAscii(FOTAParseUtil.asciiToHex(zeroChunk));
						log.debug("buffer Encoded:" + buffer);
						
						//Chunk size in hex byte
						bufferLen = (zeroChunk.length() / 2);
						log.debug("bufferLen:" + bufferLen);
						
					}else if (fotaJsonData.get(PREV_REQ_STATUS).equals(OK))
						{
							int chunkCount = Integer.parseInt(holder.getCurrentChunk())+1;
							
							String zeroChunk = partNoHolder.getFileChunks().get(chunkCount);
							
							holder.setCurrentChunk(String.valueOf(chunkCount));
							holder.setPreviousChunkTransStatus("OK");
							handleHolderBin.put(handleId, holder);
							
							//Zero the Chunk in raw format
							buffer = FOTAParseUtil.hexToAscii(FOTAParseUtil.asciiToHex(zeroChunk));
							log.debug("buffer Encoded:" + buffer);
							
							//Chunk size in hex byte
							bufferLen = (zeroChunk.length() / 2);
							log.debug("bufferLen:" + bufferLen);
							
					}else if (fotaJsonData.get(PREV_REQ_STATUS).equals(NOT_OK)) {
						
						int chunkCount = Integer.parseInt(holder.getCurrentChunk());
						String zeroChunk = partNoHolder.getFileChunks().get(chunkCount);
						
						holder.setCurrentChunk(String.valueOf(chunkCount));
						holder.setPreviousChunkTransStatus("OK");
						handleHolderBin.put(handleId, holder);
						
						//Zero the Chunk in raw format
						buffer = FOTAParseUtil.hexToAscii(FOTAParseUtil.asciiToHex(zeroChunk));
						log.debug("buffer Encoded:" + buffer);
						
						//Chunk size in hex byte
						bufferLen = (zeroChunk.length() / 2);
						log.debug("bufferLen:" + bufferLen);
						
					} 	
						
					// result pair1
					resultPair = getResponePairResult();
					
					//Init and ok send result is ok
					//crcResult = "OK";
					crsResultValue = FOTAParseUtil.asciiToHex(OK);
					
					//handlePair Init Pair1 HANDLE_EQ
					
					handlePair = getResponePair1();
					//handlePair = asciiToHex(HANDLE_EQ);
					
					//Handle in raw format(handle Value)
					String handleIdRaw = FOTAParseUtil.hexToAscii(FOTAParseUtil.asciiToHex(FOTAParseUtil.toLittleEndian((handleId))));
					
					//bufferLenPair Init Pair2(BUFFER_LEN_EQ)
					bufferLenPair = getInitResponsePair2();
					
					//String bufferLenRaw =  hexToAscii(asciiToHex(Integer.toHexString(bufferLen)));
					String bufferLenRaw =  getBufferLenTwoHexByte(bufferLen);
					
					//bufferPair Init Pair2 BUFFER_EQ
					bufferPair = getInitReponsePair3();
					
					//crcPair pair4 Init  crc
					crcPair = getResponePair3();
					
					String crsRaw = resultPair.concat(crsResultValue).concat(handlePair).concat(handleIdRaw).concat(bufferLenPair).concat(bufferLenRaw).concat(bufferPair).concat(buffer).concat(crcPair);
			
				byte[] encodedCRC = java.util.Base64.getEncoder().encode(DatatypeConverter.parseHexBinary(crsRaw));
				String encodedString = new String(encodedCRC);
				log.debug("encodedString: " + encodedString);
				
				String crcstr = calculateCRC(encodedString);
				
				// Final response String
				finalResponseStr = getInitOKResponseSendChunk(resultPair,crsResultValue,handlePair, handleIdRaw,
						bufferLenPair, bufferLenRaw, bufferPair, buffer,crcPair,crcstr,countInt);
				log.debug("finalResponseStr: " + finalResponseStr);
				}else{
					crsResultValue = FOTAParseUtil.asciiToHex("ABORT");
					resultPair = getResponePairResult();
					crcPair = getResponePair3();
					String crsRaw = resultPair.concat(crsResultValue).concat(
							crcPair);
					byte[] encodedCRC = java.util.Base64.getEncoder().encode(
							DatatypeConverter.parseHexBinary(crsRaw));
					String encodedString = new String(encodedCRC);
					log.debug("encodedString: " + encodedString);
					String crcValue = calculateCRC(encodedString);
					finalResponseStr = resultPair.concat(crsResultValue).concat(crcPair).concat(crcValue);
				}
			
			}else if (fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE3)) {
				if (fotaJsonData.get(RESULT).equals(OK)) {
					//Get handle from request
					handleId = getHandleFromRequest(rawMessage);
					log.debug("handleId from Request:" + handleId);
					
					//Initially 
					HandleHolder holder = new HandleHolder();
					holder = handleHolderBin.get(handleId);
					FOTADeviceFWareUpdate fotaDeviceFWareUpdate = new FOTADeviceFWareUpdate();
					fotaDeviceFWareUpdate.setFotaInfoId(holder.getFotaInfoId());
					fotaDeviceFWareUpdate.setDeviceSerialNumber(holder.getDeviceSerialNumber());
					fotaDeviceFWareUpdate.setDeviceSoftVersion(holder.getSoftwareVersion());
					fotaDeviceFWareUpdate.setUpdatedSoftVersion(holder.getUpdatedSoftVersion());
					fotaDeviceFWareUpdate.setDeviceSoftwareDateTime(holder.getDeviceSoftwareDateTime());
					fotaDeviceFWareUpdate.setCheckupdateDateTime(holder.getCheckupdateDateTime());
					fotaDeviceFWareUpdate.setDownloadStartDateTime(holder.getDownloadStartDateTime());
					fotaDeviceFWareUpdate.setDownloadEndDateTime(new DateTime());
					fotaDeviceFWareUpdate.setConnectionType(holder.getConnectionType());
					fotaDeviceFWareUpdate.setDownloadStatus("Success");
					
					fotaDeviceRepository.save(fotaDeviceFWareUpdate);
					// result pair1
					resultPair = getResponePairResult();
					//crcResult = "OK";
					crsResultValue = FOTAParseUtil.asciiToHex(OK);
					crcPair = getResponePair3();
					
					String crsRaw = resultPair.concat(crsResultValue).concat(crcPair);
					byte[] encodedCRC = java.util.Base64.getEncoder().encode(DatatypeConverter.parseHexBinary(crsRaw));
					String encodedString = new String(encodedCRC);
					log.debug("encodedString: " + encodedString);
					
					String crcValue = calculateCRC(encodedString);
					//Final String 
					finalResponseStr = resultPair.concat(crsResultValue).concat(crcPair).concat(crcValue);
				} else if (fotaJsonData.get(RESULT).equals(NOT_OK)) {
					//Get handle from request
					handleId = getHandleFromRequest(rawMessage);
					log.debug("handleId from Request:" + handleId);
					
					//Initially 
					HandleHolder holder = new HandleHolder();
					holder = handleHolderBin.get(handleId);
					FOTADeviceFWareUpdate fotaDeviceFWareUpdate = new FOTADeviceFWareUpdate();
					fotaDeviceFWareUpdate.setFotaInfoId(holder.getFotaInfoId());
					fotaDeviceFWareUpdate.setDeviceSerialNumber(holder.getDeviceSerialNumber());
					fotaDeviceFWareUpdate.setDeviceSoftVersion(holder.getSoftwareVersion());
					fotaDeviceFWareUpdate.setUpdatedSoftVersion(holder.getUpdatedSoftVersion());
					fotaDeviceFWareUpdate.setDeviceSoftwareDateTime(holder.getDeviceSoftwareDateTime());
					fotaDeviceFWareUpdate.setCheckupdateDateTime(holder.getCheckupdateDateTime());
					fotaDeviceFWareUpdate.setDownloadStartDateTime(holder.getDownloadStartDateTime());
					fotaDeviceFWareUpdate.setDownloadEndDateTime(new DateTime());
					fotaDeviceFWareUpdate.setConnectionType(holder.getConnectionType());
					fotaDeviceFWareUpdate.setDownloadStatus("Failure");
					
					fotaDeviceRepository.save(fotaDeviceFWareUpdate);
					
					// result pair1
					resultPair = getResponePairResult();
					//crcResult = "OK";
					crsResultValue = FOTAParseUtil.asciiToHex(NOT_OK);
					crcPair = getResponePair3();
					
					String crsRaw = resultPair.concat(crsResultValue).concat(crcPair);

					byte[] encodedCRC = java.util.Base64.getEncoder().encode(
							DatatypeConverter.parseHexBinary(crsRaw));
					String encodedString = new String(encodedCRC);
					log.debug("encodedString: " + encodedString);
					String crcValue = calculateCRC(encodedString);
					
					//Final String 
					finalResponseStr = resultPair.concat(crsResultValue).concat(crcPair).concat(crcValue);
				} else if (fotaJsonData.get(RESULT).equals(ABORTED)) {

					//Get handle from request
					handleId = getHandleFromRequest(rawMessage);
					log.debug("handleId from Request:" + handleId);
					
					//Initially 
					HandleHolder holder = new HandleHolder();
					holder = handleHolderBin.get(handleId);
					FOTADeviceFWareUpdate fotaDeviceFWareUpdate = new FOTADeviceFWareUpdate();
					fotaDeviceFWareUpdate.setFotaInfoId(holder.getFotaInfoId());
					fotaDeviceFWareUpdate.setDeviceSerialNumber(holder.getDeviceSerialNumber());
					fotaDeviceFWareUpdate.setDeviceSoftVersion(holder.getSoftwareVersion());
					fotaDeviceFWareUpdate.setUpdatedSoftVersion(holder.getUpdatedSoftVersion());
					fotaDeviceFWareUpdate.setDeviceSoftwareDateTime(holder.getDeviceSoftwareDateTime());
					fotaDeviceFWareUpdate.setCheckupdateDateTime(holder.getCheckupdateDateTime());
					fotaDeviceFWareUpdate.setDownloadStartDateTime(holder.getDownloadStartDateTime());
					fotaDeviceFWareUpdate.setDownloadEndDateTime(new DateTime());
					fotaDeviceFWareUpdate.setConnectionType(holder.getConnectionType());
					fotaDeviceFWareUpdate.setDownloadStatus("Aborted");
					
					fotaDeviceRepository.save(fotaDeviceFWareUpdate);
					
					// result pair1
					resultPair = getResponePairResult();
					//crcResult = "OK";
					crsResultValue = FOTAParseUtil.asciiToHex(OK);
					crcPair = getResponePair3();
					
					String crsRaw = resultPair.concat(crsResultValue).concat(crcPair);

					byte[] encodedCRC = java.util.Base64.getEncoder().encode(
							DatatypeConverter.parseHexBinary(crsRaw));
					String encodedString = new String(encodedCRC);
					log.debug("encodedString: " + encodedString);
					String crcValue = calculateCRC(encodedString);
					
					//Final String 
					finalResponseStr = resultPair.concat(crsResultValue).concat(crcPair).concat(crcValue);
				
					
				}
			}
		
		}else if(!validateCRC(rawMessage)){
			//crcResult = "NOT OK";
			crsResultValue = FOTAParseUtil.asciiToHex(NOT_OK);
			resultPair = getResponePairResult();
			crcPair = getResponePair3();
			String crsRaw = resultPair.concat(crsResultValue).concat(
					crcPair);

			byte[] encodedCRC = java.util.Base64.getEncoder().encode(
					DatatypeConverter.parseHexBinary(crsRaw));
			String encodedString = new String(encodedCRC);
			log.debug("encodedString: " + encodedString);
			String crcValue = calculateCRC(encodedString);
			
			finalResponseStr = resultPair.concat(crsResultValue).concat(crcPair).concat(crcValue);
		}
		
			byte[] encoded = java.util.Base64.getEncoder().encode(DatatypeConverter.parseHexBinary(finalResponseStr));
			String finalString1 = new String(encoded);
			log.error("finalString1: " + finalString1);
			return finalString1;
		
		
	}
	
	
	private void sendCRCFailedNotification() {
		List<Object[]> resultList = fotaRepositoryUtils.getFOATUsers();
		for (Object[] result : resultList) {
			mailService.sendFotaCRCFailedNotificationEmail((String) result[0],
					(String) result[1]);
		}
	}


	private String getDeviceVersion(String rawMessage) {

		byte[] getHandleByte = java.util.Base64.getDecoder().decode(rawMessage);
		int deviceIndex = returnMatch(getHandleByte, DEV_VER_RAW);
		log.error("str1: " + deviceIndex);
		StringBuilder deviceRes = new StringBuilder();
		String device1 = Integer.toHexString(getHandleByte[deviceIndex] & 0xFF);
		String device2 = Integer
				.toHexString(getHandleByte[deviceIndex + 1] & 0xFF);
		String device3 = Integer
				.toHexString(getHandleByte[deviceIndex + 2] & 0xFF);
		String device4 = Integer
				.toHexString(getHandleByte[deviceIndex + 3] & 0xFF);
				
		device1 =	("00"+ device1).substring(device1.length());
		device2 =	("00"+ device2).substring(device2.length());
		device3 =	("00"+ device3).substring(device3.length());
		device4 =	("00"+ device4).substring(device4.length());
		deviceRes.append(device1);
		deviceRes.append(device2);
		deviceRes.append(device3);
		deviceRes.append(device4);
		//written new code
		String deviceVer = FOTAParseUtil.toLittleEndian(deviceRes.toString());
		log.error("deviceVer: " + deviceVer);
		return deviceVer;
	}

	public boolean getFotaInfoByPartNumber(String partNumber) {
		boolean softDeleteFlag = false;
		boolean activePublishedFlag = false;
		boolean oldVersion = false;
		FOTAInfo fotaInfo = fotaRepository.FOTAByPartNumber(partNumber,softDeleteFlag,activePublishedFlag);
		if(fotaInfo != null){
			if(fotaInfo.getSoftDeleteFlag() == false && fotaInfo.getActivePublishedFlag() == false){
				oldVersion = true;
			}
		}
		return oldVersion;
	}

	private String calculateCRC(String encodedString) {
		 
		log.debug("Inside  calculateCRC : " ,encodedString);
		  
	    int nCheckSum = 0;
	    byte[] decoded = java.util.Base64.getDecoder().decode(encodedString);
	    
	    int nDecodeCount = 0;
	    for ( ; nDecodeCount <= (decoded.length-1); nDecodeCount++ )
	    {
	      int nValue = (decoded[nDecodeCount] & 0xFF);
	      nCheckSum += nValue;
	    }
	    
	    System.out.format("Inverted Value = %d [0X%x] \r\n" ,nCheckSum,nCheckSum);
	    nCheckSum = nCheckSum & 0xFFFF;
	    log.error("Total Value = " + nCheckSum);
	    nCheckSum = ((~nCheckSum)& 0xFFFF) + 1;
	    System.out.format("Checksum Value = %d [0X%x] \r\n" ,nCheckSum,nCheckSum);

	    String checksum_num =  Integer.toHexString(nCheckSum);
	    //String handleHexString = checksum_num.toString(16);
	    checksum_num = ("0000" + checksum_num).substring(checksum_num.length());
	    System.out.println("Checksum : " + checksum_num);
	    return FOTAParseUtil.toLittleEndian(checksum_num);
	  
	}

	private String getResponePairResult() {
		
		String getResponePairResult = FOTAParseUtil.asciiToHex(RESULT_EQ);
		log.error("getResponePairResult: " + getResponePairResult);
		//response.append("Yes");
		return getResponePairResult;
	}

	private boolean validateCRC(String rawMessage) {
		 
		log.error("Inside  calculateCRC : " ,rawMessage);
		  
	    int nCheckSum = 0;

	    byte[] decoded = java.util.Base64.getDecoder().decode(rawMessage);
	    
	    int nDecodeCount = 0;
	    for ( ; nDecodeCount < (decoded.length-2); nDecodeCount++ )
	    {
	      int nValue = (decoded[nDecodeCount] & 0xFF);
	      nCheckSum += nValue;
	    }
	    
	    
	    System.out.format("Inverted Value = %d [0X%x] \r\n" ,nCheckSum,nCheckSum);
	    
	   /* while ( nCheckSum >  65535 )
	    {
	      nCheckSum -= 65535;
	    }*/
	    nCheckSum = nCheckSum & 0xFFFF;
	    
	    int nMSB = decoded[nDecodeCount+1] & 0xFF;
	    int nLSB = decoded[nDecodeCount] & 0xFF;
	    
	    System.out.format("MSB = %d [0x%x]\r\n" ,nMSB, nMSB);
	    System.out.format("LSB = %d [0x%x]\r\n" ,nLSB, nLSB);
	    log.error("Total Value = " + nCheckSum);
	    nCheckSum = ((~nCheckSum)& 0xFFFF) + 1;
	    System.out.format("Checksum Value = %d [0X%x] \r\n" ,nCheckSum,nCheckSum);
	    
	    String msb_digit = Integer.toHexString(nMSB);
	    String lsb_digit = Integer.toHexString(nLSB);
	    String checksum_num =  Integer.toHexString(nCheckSum);
	    checksum_num = ("0000" + checksum_num).substring(checksum_num.length());
	    
	    if(msb_digit.length()<2)
	    	msb_digit = "0"+msb_digit;
	    if(lsb_digit.length()<2)
	    	lsb_digit = "0"+lsb_digit;
	    
	    System.out.println("MSB : " + msb_digit + " " +  "LSB : " + lsb_digit);
	    System.out.println("Checksum : " + checksum_num);
	    
	    if((msb_digit+lsb_digit).equalsIgnoreCase(checksum_num)){
	    	return true;
	    }else{
	    	log.error("CRC VALIDATION FAILED :"); 
	    	return false;
	    }
	}
	
	private String getBufferLenTwoHexByte(int bufferLen) {
		//Convert to hex
		String bufferLenHex =	Integer.toHexString(bufferLen);
		//convert in two byte format
		bufferLenHex = ("0000" + bufferLenHex).substring(bufferLenHex.length());
		//converting to little Endian 
		String bufferInLsb = FOTAParseUtil.hexToAscii(FOTAParseUtil.asciiToHex(FOTAParseUtil.toLittleEndian((bufferLenHex))));
		return bufferInLsb;
	}

	private String getInitOKResponseSendChunk(String resultPair,
			String crsResultValue, String handlePair, String handleIdRaw,
			String bufferLenPair, String bufferLenRaw, String bufferPair,
			String buffer, String crcPair, String crcstr, int countInt) {
		
		//Final String 
		//String finalString = responsePair1.concat(handleIdRaw).concat(responsePair2).concat(bufferLenRaw).concat(responsePair3).concat(buffer).concat(responsePair4).concat(crcRaw);
		String finalString = resultPair.concat(crsResultValue)
				.concat(handlePair).concat(handleIdRaw).concat(bufferLenPair)
				.concat(bufferLenRaw).concat(bufferPair).concat(buffer)
				.concat(crcPair).concat(crcstr);
		log.debug(" Chunk Number:"+countInt);
		log.debug(" CRC value:"+crcstr);
		return finalString;
	}


	
	
	private String getHandleFromRequest(String rawMessage) {

		byte[] getHandleByte = java.util.Base64.getDecoder().decode(rawMessage);
		int handleIndex = returnMatch(getHandleByte, HANDLE_RAW);
		log.error("str1: " + handleIndex);
		StringBuilder handleRes = new StringBuilder();
		// handleRes.
		String handle1 = Integer.toHexString(getHandleByte[handleIndex] & 0xFF);
		String handle2 = Integer
				.toHexString(getHandleByte[handleIndex + 1] & 0xFF);
		String handle3 = Integer
				.toHexString(getHandleByte[handleIndex + 2] & 0xFF);
		String handle4 = Integer
				.toHexString(getHandleByte[handleIndex + 3] & 0xFF);
				
		handle1 =	("00"+ handle1).substring(handle1.length());
		handle2 =	("00"+ handle2).substring(handle2.length());
		
		
		handle3 =	("00"+ handle3).substring(handle3.length());
		handle4 =	("00"+ handle4).substring(handle4.length());
		handleRes.append(handle1);
		handleRes.append(handle2);
		handleRes.append(handle3);
		handleRes.append(handle4);
		//written new code
		String handleId = FOTAParseUtil.toLittleEndian(handleRes.toString());
		log.error("handleId: " + handleId);
		return handleId;
	}


	private String getInitReponsePair3() {
		StringBuilder response = new StringBuilder();	
		response.append(AMPERSAND);
		response.append(BUFFER_EQ);
		String initResponsePair3 = FOTAParseUtil.asciiToHex(response.toString());
		return initResponsePair3;
	}


	private String getInitResponsePair2() {
		StringBuilder response = new StringBuilder();	
		response.append(AMPERSAND);
		response.append(BUFFER_LEN_EQ);
		String initResponsePair3 = FOTAParseUtil.asciiToHex(response.toString());
		return initResponsePair3;
	}


	private String getAllResponseCheckUpdate(String responsePair1Result,String responsePair1ResultValue, String responsePair1, String handleIdRaw,
			String responsePair2, String totalChunkRaw, String responsePair3,
			String crcRaw) {
		/*StringBuilder sb = new StringBuilder();
		sb.*/
		log.debug("totalChunkRaw"+totalChunkRaw);
		
		String finalString = responsePair1Result.concat(responsePair1ResultValue).concat(responsePair1).concat(handleIdRaw).concat(responsePair2)
				.concat(totalChunkRaw).concat(responsePair3).concat(crcRaw);
		return finalString;
	}

	private String getResponePair3() {
		StringBuilder response = new StringBuilder();
		response.append(AMPERSAND);
		response.append(CRC_EQ);
		String responePair3 = FOTAParseUtil.asciiToHex(response.toString());
		log.error("responePair3: " + responePair3);
		return responePair3;
	}


	private String getChunkRaw(int totalChunks) {
		BigInteger toHex = new BigInteger(String.valueOf(totalChunks),10);
	    String totalChunkHexString = toHex.toString(16);
	    totalChunkHexString = ("00000000" + totalChunkHexString).substring(totalChunkHexString.length());
		//converting to little Indian
	    String strTotalChunk = FOTAParseUtil.hexToAscii(FOTAParseUtil.asciiToHex(FOTAParseUtil.toLittleEndian((totalChunkHexString))));
		log.error("strTotalChunk: " + strTotalChunk);
		return strTotalChunk;
	}


	private String getResponePair2() {
		StringBuilder response = new StringBuilder();
		response.append(AMPERSAND);
		response.append(TOTAL_CHUNK);
		String responePair2 = FOTAParseUtil.asciiToHex(response.toString());
		log.error("responePair2: " + responePair2);
		return responePair2;
	}


	private String getResponePair1() {
		StringBuilder response = new StringBuilder();
		/*response.append(RESULT_EQ);
		response.append("Yes");*/
		response.append(AMPERSAND);
		response.append(HANDLE_EQ);
		String responePair1 = FOTAParseUtil.asciiToHex(response.toString());
		log.error("responePair1: " + responePair1);
		return responePair1;
	}

	//Generating unique handle id
	private String getHandleNumber() {
		Random random = new Random();
		int random1 = random.nextInt(10000);
		int random2 = random.nextInt(1000);
		int uniqueRandom = random1+random2;
        BigInteger toHex = new BigInteger(String.valueOf(uniqueRandom),10);	
        String handleHexString = toHex.toString(16);
        handleHexString = ("00000000" + handleHexString).substring(handleHexString.length());
        //String lsb = getLSBValue(handleHexString);
        String handleInlsb = (handleHexString);
        return handleInlsb;
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
	

	//DecodeRawMessage
	private String decodeRawMessage(String rawMessage) {
		String decoded_string = "";
		byte[] decoded = java.util.Base64.getDecoder().decode(rawMessage);
		String sout = "";
		for (int i = 0; i < decoded.length; i++) {
			int val = decoded[i] & 0xFF;
			sout = sout + val + " ";
		}
		log.error("Input Byte Array :" + sout);
		decoded_string = new String(decoded);
		log.error("Decoded value is " + decoded_string);
		return decoded_string;
	}

	/**
	 * savFotaInfoData
	 * @param fotaInfoDto
	 * @param baseUrl
	 * @return
	 * @throws ParseException
	 */
	@Transactional
	public FOTAInfo savFotaInfoData(FOTAInfoDto fotaInfoDto, String baseUrl)
			throws ParseException {
		//check is existing if yes update to inactive pending
		if(fotaInfoDto.getOldRecord() == true){
			FOTAInfo fotaInfo = fotaRepository.FOTAByPartNumber(fotaInfoDto.getDevicePartNumber(),false,false);
			if(Objects.nonNull(fotaInfo))
	  	  {
				fotaInfo.setSoftDeleteFlag(true);
				fotaInfo.setUploadDatetime(DateUtil.getCurrentDateAndTime());
				fotaRepository.save(fotaInfo);
	  	        log.debug("updated fotaInfo Details: with inactive pending {}", fotaInfo);
	  	  }
			
		}
		FOTAInfo fotaInfo = new FOTAInfo();
		fotaInfo.setDevicePartNumber(fotaInfoDto.getDevicePartNumber());
		fotaInfo.setSoftVersion(fotaInfoDto.getSoftVersion());
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyy");
		simpleDateFormat.setLenient(false);
		Date date2 = simpleDateFormat.parse(fotaInfoDto.getReleaseDate());
		System.out.println("Date String 2 is '" + date2);
		DateTime dt = new DateTime(date2);
		System.out.println("Dt is '" + dt);
		fotaInfo.setReleaseDate(dt);
		
		fotaInfo.setProductType(fotaInfoDto.getProductType());

		fotaInfo.setFilePath(fotaInfoDto.getFilePath());

		fotaInfo.setUploadUser(fotaInfoDto.getUploadUser());

		fotaInfo.setUploadDatetime(DateUtil.getCurrentDateAndTime());
		
		if(StringUtils.isNotEmpty(fotaInfoDto.getPublishedUser())){
			fotaInfo.setPublishedUser(fotaInfoDto.getPublishedUser());
			fotaInfo.setPublishedDateTime(DateUtil.getCurrentDateAndTime());
		}
		
		fotaInfo.setModelId(fotaInfoDto.getModelId());
		fotaInfo.setBoardId(fotaInfoDto.getBoardId());
		fotaInfo.setBedId(fotaInfoDto.getBedId());
		fotaInfo.setBootCompVer(fotaInfoDto.getBootCompVer());
		fotaInfo.setFillPattern(fotaInfoDto.getFillPattern());
		fotaInfo.setMCUSize(fotaInfoDto.getmCUSize());
		fotaInfo.setReleaseNumber(fotaInfoDto.getReleaseNumber());
		//added new attribute
		fotaInfo.setSoftDeleteFlag(false);
		if(StringUtils.isNotEmpty(fotaInfoDto.getRegion1StartAddress())){
			fotaInfo.setRegion1StartAddress(fotaInfoDto.getRegion1StartAddress());
		}
		if(StringUtils.isNotEmpty(fotaInfoDto.getRegion1EndAddress())){
			fotaInfo.setRegion1EndAddress(fotaInfoDto.getRegion1EndAddress());
		}
		if(StringUtils.isNotEmpty(fotaInfoDto.getRegion1CRCLocation())){
			fotaInfo.setRegion1CRCLocation(fotaInfoDto.getRegion1CRCLocation());
		}
		if(StringUtils.isNotEmpty(fotaInfoDto.getRegion2StartAddress())){
			fotaInfo.setRegion2StartAddress(fotaInfoDto.getRegion2StartAddress());
		}
		if(StringUtils.isNotEmpty(fotaInfoDto.getRegion2EndAddress())){
			fotaInfo.setRegion2EndAddress(fotaInfoDto.getRegion2EndAddress());
		}
		if(StringUtils.isNotEmpty(fotaInfoDto.getRegion2CRCLocation())){
			fotaInfo.setRegion2CRCLocation(fotaInfoDto.getRegion2CRCLocation());
		}
		
		fotaRepository.save(fotaInfo);
		log.debug("Created New Fota: {}", fotaInfo);
		//Email notification to approver
		sendNotification(baseUrl,"");
		
		return fotaInfo;
	}

		/**
	 * getFOTADeviceList
	 * @param status
	 * @param searchString
	 * @param sortBy
	 * @param isAscending
	 * @return
	 */
	@Transactional
	public List<FOTADeviceDto> getFOTADeviceList(String status, String searchString, String sortBy, boolean isAscending) {

		List<FOTADeviceDto> FOTADeviceDtoList = null;
		String queryString = new StringBuilder("'%").append(searchString)
				.append("%'").toString();
		
		if (status.equals(SUCCESS_LIST)) {

			List<Object[]> resultList = fotaRepositoryUtils.getSuccesList(status,queryString,sortBy,isAscending);
			
			FOTADeviceDtoList = setDeviceValues(resultList);
			
		} else if (status.equals(FAILURE_LIST)) {
			List<Object[]> resultList = fotaRepositoryUtils.getFailureList(status,queryString,sortBy,isAscending);
			
			FOTADeviceDtoList = setDeviceValues(resultList);

		} else if (status.equals(ABORTED_LIST)) {
			
			List<Object[]> resultList = fotaRepositoryUtils.getAbortList(status,queryString,sortBy,isAscending);
						
			FOTADeviceDtoList = setDeviceValues(resultList);

		} else if (status.equals(ALL)) {
			
			List<Object[]> resultList = fotaRepositoryUtils.getAllList(status,queryString,sortBy,isAscending);
			
			FOTADeviceDtoList = setDeviceValues(resultList);
		}
		
		if (sortBy.equals("downloadTime")) {
			if (isAscending) {
				Collections.sort(FOTADeviceDtoList,
						FOTADeviceDto.downLoadTimeDescComparator);
			} else {
				Collections.sort(FOTADeviceDtoList,
						FOTADeviceDto.downLoadTimeAscComparator);
			}

		}
		return FOTADeviceDtoList;
	}

	/**
	 * setDeviceValues
	 * @param FOTADeviceList
	 * @return
	 */
	private List<FOTADeviceDto> setDeviceValues(
			List<Object[]> FOTADeviceList) {
		List<FOTADeviceDto> FOTADeviceDtoList = new ArrayList<FOTADeviceDto>();
		
		for (Object[] fwareObj : FOTADeviceList) {
			//FOTAInfo fotaInfo = null;
			FOTADeviceDto fwareDtoObj = new FOTADeviceDto();
			fwareDtoObj.setDeviceSerialNumber((String)fwareObj[2]);
			fwareDtoObj.setConnectionType((String)fwareObj[3]);
			fwareDtoObj.setDeviceSoftVersion((String)fwareObj[4]);
			fwareDtoObj.setDeviceSoftwareDateTime(new DateTime(fwareObj[5]));
			fwareDtoObj.setUpdatedSoftVersion((String)fwareObj[6]);
			fwareDtoObj.setCheckupdateDateTime(new DateTime(fwareObj[7]));
			fwareDtoObj.setDownloadStartDateTime(new DateTime(fwareObj[8]));
			fwareDtoObj.setDownloadEndDateTime(new DateTime(fwareObj[9]));
			fwareDtoObj.setDownloadStatus((String)fwareObj[10]);
			fwareDtoObj.setProductType((String)fwareObj[12]);
			fwareDtoObj.setDevicePartNumber(Long.valueOf((String)fwareObj[11]));
			//Calculate Download Time
			String totalDownloadTime = getDownLoadTime(new DateTime(fwareObj[9]),new DateTime(fwareObj[8]));
			fwareDtoObj.setDownloadTime(totalDownloadTime);
			FOTADeviceDtoList.add(fwareDtoObj);
		}
		return FOTADeviceDtoList;
	}
	/**
	 * getDownLoadTime
	 * @param downloadEndDateTime
	 * @param downloadStartDateTime
	 * @return
	 */
	private String getDownLoadTime(DateTime downloadEndDateTime,
			DateTime downloadStartDateTime) {
		long elapsed = (downloadEndDateTime.getMillis())
				- (downloadStartDateTime.getMillis());

		int hours = (int) Math.floor(elapsed / 3600000);

		int minutes = (int) Math.floor((elapsed - hours * 3600000) / 60000);

		int seconds = (int) Math
				.floor((elapsed - hours * 3600000 - minutes * 60000) / 1000);

		
		String hr =	("00"+ String.valueOf(hours)).substring(String.valueOf(hours).length());
		
		String min =	("00"+ String.valueOf(minutes)).substring(String.valueOf(minutes).length());
		
		String sec =	("00"+ String.valueOf(seconds)).substring(String.valueOf(seconds).length());
		
		String totalDownloadTime = hr.concat(":")
				.concat(String.valueOf(min)).concat(":")
				.concat(String.valueOf(sec));

		return totalDownloadTime;
	}
	/**
	 * Get Firmware list
	 * @param status
	 * @param searchString
	 * @param sortBy
	 * @param isAscending
	 * @return
	 */
	@Transactional
	public List<FOTAInfo> FOTAList(String status, String searchString, String sortBy, boolean isAscending) {
		List<FOTAInfo> FOTAInfoList = null;
		
		if(status.equals("ActivePending")){
			FOTAInfoList = getSortPendingFirmwareList(searchString,sortBy,isAscending);
			
		}else if(status.equals("ActivePublished")){
			
			FOTAInfoList = getSortActivePublishdFirmwareList(searchString,sortBy,isAscending);
			
		}else if(status.equals("All")){
			FOTAInfoList = getSortAllFirmwareList(searchString,sortBy,isAscending);
			
		}
		return FOTAInfoList;
	}
	/**
	 * getSortAllFirmwareList
	 * @param searchString
	 * @param sortBy
	 * @param isAscending
	 * @return
	 */
	private List<FOTAInfo> getSortAllFirmwareList(String searchString,
			String sortBy, boolean isAscending) {
		List<FOTAInfo> FOTAInfoList = new ArrayList<FOTAInfo>();
		List<FOTAInfo> FOTAInfoListUpdate = null;
		FOTAInfoListUpdate = new ArrayList<FOTAInfo>();
		String queryString = new StringBuilder("%").append(searchString)
				.append("%").toString();
		FOTAInfoList = fotaRepository.getFOTAListByAllAndSearchStr(queryString);
		
		for(FOTAInfo info : FOTAInfoList){
			if(info.getSoftDeleteFlag() == false && info.getActivePublishedFlag() == false){
				info.setFOTAStatus("Active Pending");
			}
			else if(info.getSoftDeleteFlag() == false && info.getActivePublishedFlag() == true){
				info.setFOTAStatus("Active Published");
			}else if(info.getSoftDeleteFlag() == true && info.getActivePublishedFlag() == false){
				info.setFOTAStatus("Inactive Pending");
			}
			else if(info.getSoftDeleteFlag() == true && info.getActivePublishedFlag() == true){
				info.setFOTAStatus("Inactive Published");
			}
			FOTAInfoListUpdate.add(info);
		}
		FOTAInfoListUpdate = getSortingData(FOTAInfoListUpdate,sortBy,isAscending);
		return FOTAInfoListUpdate;
	}


	/**
	 * getSortActivePublishdFirmwareList
	 * @param searchString
	 * @param sortBy
	 * @param isAscending
	 * @return
	 */
	private List<FOTAInfo> getSortActivePublishdFirmwareList(
			String searchString, String sortBy, boolean isAscending) {
		List<FOTAInfo> FOTAInfoList = new ArrayList<FOTAInfo>();
		List<FOTAInfo> FOTAInfoListUpdate = null;
		FOTAInfoListUpdate = new ArrayList<FOTAInfo>();
		boolean softDeleteFlag = false;
		boolean activePublishedFlag = true;
		String queryString1 = new StringBuilder("%").append(searchString)
				.append("%").toString();
		
		FOTAInfoList = fotaRepository.getFOTAListByPublishedAndSearchStr(softDeleteFlag,activePublishedFlag,queryString1);
		for(FOTAInfo info : FOTAInfoList){
			if(info.getSoftDeleteFlag() == false && info.getActivePublishedFlag() == true){
				info.setFOTAStatus("Active Published");
			}else if(info.getSoftDeleteFlag() == false && info.getActivePublishedFlag() == true){
				info.setFOTAStatus("Active Published");
			}
			FOTAInfoListUpdate.add(info);
		}
		FOTAInfoListUpdate = getSortingData(FOTAInfoListUpdate,sortBy,isAscending);
		return FOTAInfoListUpdate;
	}

	/**
	 * getSortPendingFirmwareList
	 * @param searchString
	 * @param sortBy
	 * @param isAscending
	 * @return
	 */
	private List<FOTAInfo> getSortPendingFirmwareList(String searchString,
			String sortBy, boolean isAscending) {
		List<FOTAInfo> FOTAInfoList = new ArrayList<FOTAInfo>();
		List<FOTAInfo> FOTAInfoListUpdate = null;
		FOTAInfoListUpdate = new ArrayList<FOTAInfo>();
		String queryString = new StringBuilder("%").append(searchString)
				.append("%").toString();
		FOTAInfoList = fotaRepository.getFOTAListByPendingAndSearchStr(false,false,false,false,true,true,queryString);
		for(FOTAInfo info : FOTAInfoList){
			if(info.getSoftDeleteFlag() == false && info.getActivePublishedFlag() == false && info.getDeleteRequestFlag() == false ){
				info.setFOTAStatus("Active Pending");
			}else if(info.getSoftDeleteFlag() == false && info.getActivePublishedFlag() == true && info.getDeleteRequestFlag() == true ){
				info.setFOTAStatus("Delete Requested");
			}
			FOTAInfoListUpdate.add(info);
		}
		FOTAInfoListUpdate = getSortingData(FOTAInfoListUpdate,sortBy,isAscending);
				return FOTAInfoListUpdate;
	}

	/**
	 * Firmware sorting common method
	 * @param FOTAInfoList
	 * @param sortBy
	 * @param isAscending
	 * @return
	 */
	private List<FOTAInfo> getSortingData(List<FOTAInfo> FOTAInfoList, String sortBy, boolean isAscending) {

		if(sortBy.equals("") && isAscending == false){
			Collections.sort(FOTAInfoList,FOTAInfo.idDesc);
		}else if(sortBy.equals("partNumber")){
			if(isAscending){
				Collections.sort(FOTAInfoList,FOTAInfo.devicePartDescComparator);
			}else{
				Collections.sort(FOTAInfoList,FOTAInfo.devicePartAscComparator);
			}
		}else if(sortBy.equals("productName")){
			if(isAscending){
				Collections.sort(FOTAInfoList,FOTAInfo.productNameDescComparator);
			}else{
				Collections.sort(FOTAInfoList,FOTAInfo.productNameAscComparator);
			}
		}else if(sortBy.equals("softwareVersion")){
			if(isAscending){
				Collections.sort(FOTAInfoList,FOTAInfo.softVerDescComparator);
			}else{
				Collections.sort(FOTAInfoList,FOTAInfo.softVerAscComparator);
			}
		}else if(sortBy.equals("softwareDate")){
			if(isAscending){
				Collections.sort(FOTAInfoList,FOTAInfo.softDateDescComparator);
			}else{
				Collections.sort(FOTAInfoList,FOTAInfo.softDateAscComparator);
			}
		}else if(sortBy.equals("uploadBy")){
			if(isAscending){
				Collections.sort(FOTAInfoList,FOTAInfo.uploadByDescComparator);
			}else{
				Collections.sort(FOTAInfoList,FOTAInfo.uploadByAscComparator);
			}
		}else if(sortBy.equals("uploadDate")){
			if(isAscending){
				Collections.sort(FOTAInfoList,FOTAInfo.uploadDateDescComparator);
			}else{
				Collections.sort(FOTAInfoList,FOTAInfo.uploadDateAscComparator);
			}
		}else if(sortBy.equals("publishedBy")){
			if(isAscending){
				Collections.sort(FOTAInfoList,FOTAInfo.publishedByDescComparator);
			}else{
				Collections.sort(FOTAInfoList,FOTAInfo.publishedByAscComparator);
			}
		}else if(sortBy.equals("publishedDate")){
			if(isAscending){
				Collections.sort(FOTAInfoList,FOTAInfo.publishedDateDescComparator);
			}else{
				Collections.sort(FOTAInfoList,FOTAInfo.publishedDateAscComparator);
			}
		}else if(sortBy.equals("status")){
			if(isAscending){
				Collections.sort(FOTAInfoList,FOTAInfo.statusDescComparator);
			}else{
				Collections.sort(FOTAInfoList,FOTAInfo.statusAscComparator);
			}
		}
		
		return FOTAInfoList;
	}


	/**
	 * CRC 32 validation
	 * @param crc32Dt0
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public boolean CRC32Calculation(CRC32Dto crc32Dt0){
	    boolean eof = false;
	    boolean result = false;
	    int recordIdx = 0;
	    long upperAddress = 0;
	    long crcStartAddress  = 0;
	    long crcEndAddress = 0;
	    long crcLocationAddress = 0;
	    int crcValueInFile = 0;
	    long dataStartAddress = 0;
	    long crc2StartAddress = 0;
	    long crc2EndAddress = 0;
	    long crc2LocationAddress = 0;
	    int crc2ValueInFile = 0;
	    long data2StartAddress = 0;

	    ByteArrayOutputStream crcData = new ByteArrayOutputStream();
	    ByteArrayOutputStream crcData2 = new ByteArrayOutputStream();
	    InputStreamReader isr = null;
	    BufferedReader rdr =  null;

	    int record_length;
	    int record_address;
	    byte[] record_data;
	    
	    FileInputStream fs = null;
	    
	    try{
	    
		if (StringUtils.isNotEmpty(crc32Dt0.getRegion1StartAddress())) {
			crcStartAddress = Long.parseLong(crc32Dt0.getRegion1StartAddress(),16);
		}
		if (StringUtils.isNotEmpty(crc32Dt0.getRegion1EndAddress())) {
			crcEndAddress = Long.parseLong(crc32Dt0.getRegion1EndAddress(),16);
		}
		
		if (StringUtils.isNotEmpty(crc32Dt0.getRegion1CRCLocation())) {
			crcLocationAddress = Long.parseLong(crc32Dt0.getRegion1CRCLocation(),16);
		}
		if (StringUtils.isNotEmpty(crc32Dt0.getRegion2StartAddress())) {
			crc2StartAddress = Long.parseLong(crc32Dt0.getRegion2StartAddress(),16);
		}
		if (StringUtils.isNotEmpty(crc32Dt0.getRegion2EndAddress())) {
			crc2EndAddress = Long.parseLong(crc32Dt0.getRegion2EndAddress(),16);
		}
		
		if (StringUtils.isNotEmpty(crc32Dt0.getRegion2CRCLocation())) {
			crc2LocationAddress = Long.parseLong(crc32Dt0.getRegion2CRCLocation(),16);
		}
		
		fs = new FileInputStream(crc32Dt0.getFilePath());
	    isr = new InputStreamReader(fs);
	    rdr =  new BufferedReader(isr);
        eof = false;
        recordIdx = 1;
        upperAddress = 0;
        String recordStr;
        while ((recordStr = rdr.readLine()) != null) {
            if (eof) {
                try {
					throw new Exception("Data after eof (" + recordIdx + ")");
				} catch (Exception e) {
					e.printStackTrace();
				}
            }

            if (!recordStr.startsWith(":")) {
                try {
					throw new Exception("Invalid Intel HEX record (" + recordIdx + ")");
				} catch (Exception e) {
					e.printStackTrace();
				}
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
                try {
					throw new Exception("Invalid checksum (" + recordIdx + ")");
				} catch (Exception e) {
					e.printStackTrace();
				}
            }

            record_length = hexRecord[0];
            if ((record_length + 5) != hexRecord.length) {
                try {
					throw new Exception("Invalid record length (" + recordIdx + ")");
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            record_data = new byte[record_length];
            System.arraycopy(hexRecord, 4, record_data, 0, record_length);

            record_address = ((hexRecord[1] & 0xFF) << 8) + (hexRecord[2] & 0xFF);

            long addr = record_address | upperAddress;
            switch (hexRecord[3] & 0xFF) {
                case 0:
                	 long tmpAddr = addr;
                	 long tmp2Addr = addr;
                	  for(byte c: record_data)
                	  {
                		  if(tmpAddr >= crcLocationAddress && tmpAddr< (crcLocationAddress+4))
                		  {
                			   int diff = (int)(tmpAddr-crcLocationAddress);
                			  	switch(diff)
                			  	{
                			  	case 0:
                			  		crcValueInFile = ((int)c&0xFF);
                			  		break;
                			  	case 1:
                			  		crcValueInFile |= (((int)c&0xFF) << 8);
                			  		break;
                			  	case 2:
                			  		crcValueInFile |= (((int)c&0xFF) << 16);
                			  		break;
                			  	case 3:
                			  		crcValueInFile |= (((int)c&0xFF)<< 24);
                			  		break;
                			  	default:
                			  			
                			  	}
                		  }
                		  tmpAddr++;
                		  
                    	  if(addr>=crcStartAddress && addr<= crcEndAddress)
                    	  {
                    		  if(dataStartAddress==0)
                    		  {
                    			  dataStartAddress = addr;
                    		  }
                    		  crcData.write(c);
                    	  }

                		  if(tmp2Addr >= crc2LocationAddress && tmp2Addr< (crc2LocationAddress+4))
                		  {
                			   int diff = (int)(tmp2Addr-crc2LocationAddress);
                			  	switch(diff)
                			  	{
                			  	case 0:
                			  		crc2ValueInFile = ((int)c&0xFF);
                			  		break;
                			  	case 1:
                			  		crc2ValueInFile |= (((int)c&0xFF) << 8);
                			  		break;
                			  	case 2:
                			  		crc2ValueInFile |= (((int)c&0xFF) << 16);
                			  		break;
                			  	case 3:
                			  		crc2ValueInFile |= (((int)c&0xFF) << 24);
                			  		break;
                			  	default:
                			  			
                			  	}
                		  }
                		  tmp2Addr++;
                		  
                    	  if(addr>=crc2StartAddress && addr<= crc2EndAddress)
                    	  {
                    		  if(data2StartAddress==0)
                    		  {
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
                        upperAddress = ((record_data[0] & 0xFF) << 12) +( ((record_data[1] & 0xFF)) << 4);
                    } else {
                        try {
							throw new Exception("Invalid SEG record (" + recordIdx + ")");
						} catch (Exception e) {
							e.printStackTrace();
						}
                    }
                    break;                	
                case 4:
                    if (record_length == 2) {
                        upperAddress = ((record_data[0] & 0xFF) << 24) +( ((record_data[1] & 0xFF)) << 16);
                    } else {
                        try {
							throw new Exception("Invalid EXT_LIN record (" + recordIdx + ")");
						} catch (Exception e) {
							e.printStackTrace();
						}
                    }
                    break;
                default:
                    break;
            }
            recordIdx++;
        };
        
    // CRC Calculation Table initialize
    int crc;
    int i;
    if(crcStartAddress != 0){
        byte [] crcBytes = crcData.toByteArray();

        crc  = 0xFFFFFFFF;       // initial contents of LFBSR
        int poly = 0xEDB88320;   // reverse polynomial

        for (byte b : crcBytes) {
            int temp = (crc ^ b) & 0xff;

            // read 8 bits one at a time
            for (i = 0; i < 8; i++) {
                if ((temp & 1) == 1) temp = (temp >>> 1) ^ poly;
                else                 temp = (temp >>> 1);
            }
            crc = (crc >>> 8) ^ temp;
        }
      
        long tmpEndAddress = dataStartAddress + crcBytes.length;
        while(tmpEndAddress<=(crcEndAddress))
        {
            int temp = (crc ^ 0xFF) & 0xff;

        	for (i = 0; i < 8; i++) {
                if ((temp & 1) == 1) temp = (temp >>> 1) ^ poly;
                else                 temp = (temp >>> 1);
            }
            crc = (crc >>> 8) ^ temp;
            tmpEndAddress++;
        }

        // flip bits
        crc = crc ^ 0xffffffff;

		result = (crc == crcValueInFile);
		//crcData.close();
       log.debug("Calculated Region1CRC32: " + (String.format("0x%08X", crc)) + "In file :" + (String.format("0x%08X", (crcValueInFile))));
        
    }
    if(crc2StartAddress != 0 && result == true){
    	byte [] crc2Bytes = crcData2.toByteArray();

        crc  = 0xFFFFFFFF;       // initial contents of LFBSR
        int poly2 = 0xEDB88320;   // reverse polynomial

        for (byte b : crc2Bytes) {
            int temp = (crc ^ b) & 0xff;

            // read 8 bits one at a time
            for (i = 0; i < 8; i++) {
                if ((temp & 1) == 1) temp = (temp >>> 1) ^ poly2;
                else                 temp = (temp >>> 1);
            }
            crc = (crc >>> 8) ^ temp;
        }
      
        long tmp2EndAddress = data2StartAddress + crc2Bytes.length;
        while(tmp2EndAddress<=(crc2EndAddress))
        {
            int temp = (crc ^ 0xFF) & 0xff;

        	for (i = 0; i < 8; i++) {
                if ((temp & 1) == 1) temp = (temp >>> 1) ^ poly2;
                else                 temp = (temp >>> 1);
            }
            crc = (crc >>> 8) ^ temp;
            tmp2EndAddress++;
        }
        // flip bits
        crc = crc ^ 0xffffffff;
        log.debug("Calculated Region2CRC32: " + (String.format("0x%08X", crc)) + "In file :" + (String.format("0x%08X", (crc2ValueInFile))));
        result &= (crc == crc2ValueInFile);
        //crcData2.close();
    		}
	    }catch(IOException ex){
	    	ex.printStackTrace();
	    }
	    finally{
	    	try {
	    		rdr.close();
	            isr.close();
	            fs.close();
	    		crcData.close();
				crcData2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
	    }
		return result;
	}
	
	/**
	 * Get Firmware details to view 
	 * @param id
	 * @return
	 */
	@Transactional
	public FOTAInfo getFirmwareDetails(Long id) {
		FOTAInfo fotaInfo = null;
		fotaInfo = fotaRepository.findOneById(id);
		if (Objects.nonNull(fotaInfo)) {
			if (fotaInfo.getSoftDeleteFlag() == false
					&& fotaInfo.getActivePublishedFlag() == false) {
				fotaInfo.setFOTAStatus("Active Pending");
			} else if (fotaInfo.getSoftDeleteFlag() == false
					&& fotaInfo.getActivePublishedFlag() == true && fotaInfo.getDeleteRequestFlag() == false) {
				fotaInfo.setFOTAStatus("Active Published");
			}else if(fotaInfo.getSoftDeleteFlag() == false && fotaInfo.getActivePublishedFlag() == true && fotaInfo.getDeleteRequestFlag() == true ){
				fotaInfo.setFOTAStatus("Delete Requested");
			}else if((fotaInfo.getSoftDeleteFlag() == true && fotaInfo.getActivePublishedFlag() == true && fotaInfo.getDeleteRequestFlag() == true )||fotaInfo.getSoftDeleteFlag() == true && fotaInfo.getActivePublishedFlag() == true && fotaInfo.getDeleteRequestFlag() == false ){
				fotaInfo.setFOTAStatus("Inactive Published");
			}
			else if((fotaInfo.getSoftDeleteFlag() == true  && fotaInfo.getDeleteRequestFlag() == true )|| (fotaInfo.getSoftDeleteFlag() == true  && fotaInfo.getDeleteRequestFlag() == false )){
				fotaInfo.setFOTAStatus("Inactive Pending");
			}
		}
		return fotaInfo;
	}
	/**
	 * ValidateApprover key in CRC 32
	 * @param apprDto
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public boolean validateApproverCRC32(ApproverCRCDto apprDto)
			throws Exception {
		FOTAInfo fotaInfo = null;
		FOTAInfo fotaInfoExist = null;
		boolean result = false;
		
		if(apprDto.getIsValideCRC32()){
			//Get Active pending FOTA info to approve
			fotaInfo = fotaRepository.findOneById(apprDto.getFotaId());
			fotaInfoExist = fotaRepository.FOTAByPartNumber(fotaInfo.getDevicePartNumber(), false, true);
			if (Objects.nonNull(fotaInfoExist)) {
				fotaInfoExist.setSoftDeleteFlag(true);
				fotaRepository.save(fotaInfoExist);
				log.debug("FotaInfo Details: with Inactive published {}", fotaInfo);
				
				String storeChunk = fotaInfo.getDevicePartNumber().concat(":").concat(fotaInfo.getSoftVersion());
				if(partNoHolder !=null){
					for(String key : partNosBin.keySet()){
						
						if(key.contains(storeChunk)){
							partNoHolder = partNosBin.get(key);
							log.debug("key :"+key);
						}
					}
					partNoHolder.setAbortFlag(true);
					log.debug("Abort flag is set:"+partNoHolder.getAbortFlag());	
				}
			}
			if (Objects.nonNull(fotaInfo)) {
				fotaInfo.setActivePublishedFlag(true);
				fotaInfo.setPublishedDateTime(DateUtil.getCurrentDateAndTime());
				fotaInfo.setPublishedUser(apprDto.getPublishedUser());
				fotaRepository.save(fotaInfo);
				log.debug("FotaInfo Details: with active published {}", fotaInfo);
			}
			//After active published
			result = apprDto.getIsValideCRC32();
		}else{
			//Get Active pending FOTA info to approve
			fotaInfo = fotaRepository.findOneById(apprDto.getFotaId());
			
			//Approve key in CRC32 calculations
			result = validateApproverCRC32(fotaInfo,apprDto.getRegion1CRC(), apprDto.getRegion2CRC());
		}
		return result;
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
	private boolean validateApproverCRC32(FOTAInfo fotaInfo, String region1crc,
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

		if (StringUtils.isNotEmpty(fotaInfo.getRegion1StartAddress())) {
			crcStartAddress = Long.parseLong(fotaInfo.getRegion1StartAddress(),
					16);
		}
		if (StringUtils.isNotEmpty(fotaInfo.getRegion1EndAddress())) {
			crcEndAddress = Long.parseLong(fotaInfo.getRegion1EndAddress(), 16);
		}
		if (StringUtils.isNotEmpty(fotaInfo.getRegion2StartAddress())) {
			crc2StartAddress = Long.parseLong(
					fotaInfo.getRegion2StartAddress(), 16);
		}
		if (StringUtils.isNotEmpty(fotaInfo.getRegion2EndAddress())) {
			crc2EndAddress = Long
					.parseLong(fotaInfo.getRegion2EndAddress(), 16);
		}
		
		if (StringUtils.isNotEmpty(region1crc)) {
			crcValueInFile = Long.parseLong(FOTAParseUtil.toLittleEndian(region1crc),16);
		}
		if (StringUtils.isNotEmpty(region2crc)) {
			crc2ValueInFile = Long.parseLong(FOTAParseUtil.toLittleEndian(region2crc),16);
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
	 * firmwareDelete
	 * @param id
	 * @param userRole
	 * @param baseUrl
	 * @return
	 */
	@Transactional
	public FOTAInfo firmwareDelete(Long id, String userRole, String baseUrl) {
		FOTAInfo fotaInfo = null;
		fotaInfo = fotaRepository.findOneById(id);
		if (Objects.nonNull(fotaInfo)) {
			//If FOTA ADMIN delete & //If FOTA APPROVER delete
			if(userRole.equals("FOTA_ADMIN")){
				//Active pending delete & else Active published  delete request
				if (fotaInfo.getSoftDeleteFlag() == false
						&& fotaInfo.getActivePublishedFlag() == false) {
					fotaInfo.setSoftDeleteFlag(true);
					fotaInfo.setDeleteRequestFlag(true);
					fotaRepository.save(fotaInfo);
				}else if (fotaInfo.getSoftDeleteFlag() == false
						&& fotaInfo.getActivePublishedFlag() == true) {
					fotaInfo.setDeleteRequestFlag(true);
					fotaRepository.save(fotaInfo);
					//Send Notification to approver for delete request
					//Email notification to approver
					sendNotification(baseUrl, userRole);
				}
				
			}else if(userRole.equals("FOTA_APPROVER")){
				if (fotaInfo.getSoftDeleteFlag() == false
						&& fotaInfo.getActivePublishedFlag() == false) {
					fotaInfo.setSoftDeleteFlag(true);
					fotaInfo.setDeleteRequestFlag(true);
					fotaRepository.save(fotaInfo);
				}else if (fotaInfo.getSoftDeleteFlag() == false
						&& fotaInfo.getActivePublishedFlag() == true && fotaInfo.getDeleteRequestFlag() == false) {
					fotaInfo.setSoftDeleteFlag(true);
					fotaInfo.setDeleteRequestFlag(true);
					fotaRepository.save(fotaInfo);
				}else if (fotaInfo.getSoftDeleteFlag() == false
						&& fotaInfo.getActivePublishedFlag() == true
						&& fotaInfo.getDeleteRequestFlag() == true) {
					fotaInfo.setSoftDeleteFlag(true);
					fotaRepository.save(fotaInfo);
				}
			}
		}

		return fotaInfo;
	}
	/**
	 * Email notification to approver
	 * 
	 * @param baseUrl
	 * @param userRole
	 */
	private void sendNotification(String baseUrl, String userRole) {

		List<Object[]> resultList = fotaRepositoryUtils.getFOTAAprUsers();
		// Only for when FOTA Admin requests
		if (userRole.equals(FOTA_ADMIN)) {
			for (Object[] result : resultList) {
				mailService.sendFotaDeleteNotificationEmail((String) result[0],
						(String) result[1], baseUrl);
			}
		} else if (userRole.equals("")) {
			for (Object[] result : resultList) {
				mailService.sendFotaUploadNotificationEmail((String) result[0],
						(String) result[1], baseUrl);
			}
		}
	}

	/**
	 * getChunk
	 * @param rawMessage
	 * @return
	 */
	private String getChunk(String rawMessage) {

		byte[] getChunkByte = java.util.Base64.getDecoder().decode(rawMessage);
		int chunkByteIndex = returnMatch(getChunkByte, CHUNK_SIZE_RAW);
		log.error("chunkByteIndex: " + chunkByteIndex);
		int chunkSizeValue = getChunkByte[chunkByteIndex] & 0xFF;
		int chunkSizeValue1 = getChunkByte[chunkByteIndex + 1] & 0xFF;

		String chunkSize1 = Integer.toHexString(chunkSizeValue);
		String chunkSize2 = Integer.toHexString(chunkSizeValue1);

		chunkSize1 = ("00" + chunkSize1).substring(chunkSize1.length());
		chunkSize2 = ("00" + chunkSize2).substring(chunkSize2.length());

		StringBuilder sb = new StringBuilder();
		sb.append(chunkSize1);
		sb.append(chunkSize2);

		String littleEndianChunk = FOTAParseUtil.toLittleEndian(sb.toString());
		return littleEndianChunk;

	}

}
