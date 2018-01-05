package com.hillrom.vest.service.util.FOTA;

import static com.hillrom.vest.config.FOTA.FOTAConstants.ABORT;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HANDLE_RAW;
import static com.hillrom.vest.config.FOTA.FOTAConstants.INIT;
import static com.hillrom.vest.config.FOTA.FOTAConstants.NOT_OK;
import static com.hillrom.vest.config.FOTA.FOTAConstants.OK;
import static com.hillrom.vest.config.FOTA.FOTAConstants.PREV_REQ_STATUS;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_PARTNUMBER;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_SN;
import static com.hillrom.vest.config.FOTA.FOTAConstants.No;
import static com.hillrom.vest.config.FOTA.FOTAConstants.SOFT_VER_DATE;
import static com.hillrom.vest.config.FOTA.FOTAConstants.INPROGRESS_LIST;

import java.math.BigInteger;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

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
import com.hillrom.vest.web.rest.FOTA.dto.FOTADeviceDto;
import com.hillrom.vest.web.rest.FOTA.dto.HandleHolder;
import com.hillrom.vest.web.rest.FOTA.dto.PartNoHolder;

@Service
@Transactional
public class FOTAServiceUtil {
	private static final Logger log = LoggerFactory
			.getLogger(FOTAServiceUtil.class);
	@Inject
	private FOTADeviceRepository fotaDeviceRepository;
	
	@Inject
	private FOTARepositoryUtils fotaRepositoryUtils;

	@Inject
	private FOTARepository fotaRepository;
	
	@Inject
	private MailService mailService;
	
	@Inject
	private CommonFOTAUtil coUtil;
	
	private String resultPair = new String();
	private String handlePair = new String();
	private String totalChunkPair = new String();
	private String crcPair = new String();
	private String finalResponseStr = new String();
	//private String crsResultValue = new String();
	private String handleId = new String();
	private String bufferLenPair = new String();
	private String bufferPair = new String();
	private int bufferLen = 0;
	private String buffer = null;

	public void saveDeviceDetails(String handleId, String status,
			String rawMessage, Map<String, HandleHolder> handleHolderBin) {
		try {
			// Get handle from request
			handleId = coUtil.getValuesFromRequest(rawMessage, HANDLE_RAW);
			log.debug("handleId from Request:" + handleId);
			// Initially
			HandleHolder holder = new HandleHolder();
			holder = handleHolderBin.get(handleId);
			FOTADeviceFWareUpdate fotaDeviceFWareUpdate = new FOTADeviceFWareUpdate();
			// Get FOTADevice
			fotaDeviceFWareUpdate = fotaDeviceRepository
					.getFOTADeviceFWwareDetailsByDevSN(holder
							.getDeviceSerialNumber(),INPROGRESS_LIST);
			if (fotaDeviceFWareUpdate != null) {
				fotaDeviceFWareUpdate.setDownloadStartDateTime(holder
						.getDownloadStartDateTime());
				fotaDeviceFWareUpdate.setDownloadEndDateTime(new DateTime());
				fotaDeviceFWareUpdate.setDownloadStatus(status);
				fotaDeviceRepository.save(fotaDeviceFWareUpdate);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void saveInprogressDeviceDetails(HandleHolder holder) {
		try {
			
			FOTADeviceFWareUpdate fotaDeviceFWareUpdate = new FOTADeviceFWareUpdate();
			fotaDeviceFWareUpdate.setFotaInfoId(holder.getFotaInfoId());
			fotaDeviceFWareUpdate.setDeviceSerialNumber(holder
					.getDeviceSerialNumber());
			fotaDeviceFWareUpdate.setDeviceSoftVersion(holder
					.getSoftwareVersion());
			fotaDeviceFWareUpdate.setUpdatedSoftVersion(holder
					.getUpdatedSoftVersion());
			fotaDeviceFWareUpdate.setDeviceSoftwareDateTime(holder
					.getDeviceSoftwareDateTime());
			fotaDeviceFWareUpdate.setCheckupdateDateTime(holder
					.getCheckupdateDateTime());
			fotaDeviceFWareUpdate.setConnectionType(holder.getConnectionType());
			fotaDeviceFWareUpdate.setDownloadStatus("In progress");
			fotaDeviceFWareUpdate.setDownloadStartDateTime(holder.getDownloadStartDateTime());
			
			fotaDeviceRepository.save(fotaDeviceFWareUpdate);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public String getRequestType2(String rawMessage, Map<String, HandleHolder> handleHolderBin, Map<String, PartNoHolder> partNosBin, Map<String, String> fotaJsonData) {
		
		try{
			String crsResultValue = new String();
			//Get handle from request
			handleId = coUtil.getValuesFromRequest(rawMessage,HANDLE_RAW);
			log.debug("Send Chunk Handle Id ="+handleId);
			//Initially 
			HandleHolder holder = new HandleHolder();
			PartNoHolder partNoHolder = new PartNoHolder();
			//Get handle object based on handleId
			holder = handleHolderBin.get(handleId);
			//Frame key to get partNumber details
			String storePartNoKey = holder.getPartNo().concat(":").concat(holder.getUpdatedSoftVersion()).concat(":").concat(String.valueOf(holder.getChunkSize()));
			partNoHolder =  partNosBin.get(storePartNoKey);
			
		if(partNoHolder.getAbortFlag() == false){
			if (fotaJsonData.get(PREV_REQ_STATUS).equals(INIT)) 
			{
				//Get current chunk count from handle holder object
				int chunkCount = 0;
				
				//Get the particular chunk from the based chunk count
				String zeroChunk = partNoHolder.getFileChunks().get(chunkCount);
				
				holder.setCurrentChunk(String.valueOf(chunkCount));
				holder.setPreviousChunkTransStatus(INIT);
				holder.setDownloadStartDateTime(new DateTime());

				handleHolderBin.put(handleId, holder);
				
				log.debug("Init Send  Chunk Handle Id ="+handleId);
				log.debug("Init Counter ="+holder.getCurrentChunk());
				log.debug("Init ChunkSize ="+holder.getChunkSize());
				log.debug("Send chunk Framed Part No key:"+storePartNoKey+"Part No Obj="+partNoHolder);
				log.debug("Send chunk value in Hex Str:"+zeroChunk);
				//Zero the Chunk in raw format
				buffer = coUtil.hexToAscii(coUtil.asciiToHex(zeroChunk));
				log.debug("buffer Encoded:" + buffer);
				
				//Chunk size in hex byte
				bufferLen = zeroChunk.length() / 2;
				log.debug("Init bufferLen:" + bufferLen);
				log.debug("Init Count:" + chunkCount);
				
			}else if (fotaJsonData.get(PREV_REQ_STATUS).equals(OK))
				{
					int chunkCount = Integer.parseInt(holder.getCurrentChunk())+1;
					
					String zeroChunk = partNoHolder.getFileChunks().get(chunkCount);
					
					holder.setCurrentChunk(String.valueOf(chunkCount));
					holder.setPreviousChunkTransStatus(OK);
					handleHolderBin.put(handleId, holder);
					
					log.debug("Ok Send  Chunk Handle Id ="+handleId);
					log.debug("Ok Counter ="+holder.getCurrentChunk());
					log.debug("OK ChunkSize ="+holder.getChunkSize());
					log.debug("Send chunk Framed Part No key:"+storePartNoKey+"Part No Obj="+partNoHolder);
					log.debug("OK chunk value in Hex Str:"+zeroChunk);
					//Zero the Chunk in raw format
					buffer = coUtil.hexToAscii(coUtil.asciiToHex(zeroChunk));
					log.debug("buffer Encoded:" + buffer);
					
					//Chunk size in hex byte
					bufferLen = zeroChunk.length() / 2;
					log.debug("Ok BufferLen:" + bufferLen);
					log.debug("OK Count:" + chunkCount);
					
			}else if (fotaJsonData.get(PREV_REQ_STATUS).equals(NOT_OK)) {
				
				int chunkCount = Integer.parseInt(holder.getCurrentChunk());
				String zeroChunk = partNoHolder.getFileChunks().get(chunkCount);
				
				holder.setCurrentChunk(String.valueOf(chunkCount));
				holder.setPreviousChunkTransStatus(OK);
				handleHolderBin.put(handleId, holder);
				
				log.debug("Not Ok Send  Chunk Handle Id ="+handleId);
				log.debug("Not Ok Counter ="+holder.getCurrentChunk());
				log.debug("Not OK ChunkSize ="+holder.getChunkSize());
				log.debug("Send chunk Framed Part No key:"+storePartNoKey+"Part No Obj="+partNoHolder);
				log.debug("Send chunk value if not ok in Hex Str:"+zeroChunk);
				//Zero the Chunk in raw format
				buffer = coUtil.hexToAscii(coUtil.asciiToHex(zeroChunk));
				log.debug("buffer Encoded:" + buffer);
				
				//Chunk size in hex byte
				bufferLen = zeroChunk.length() / 2;
				log.debug("bufferLen:" + bufferLen);
				log.debug("OK Count:" + chunkCount);
				
			} 	
			// result pair1
			resultPair = coUtil.getResponePairResult();
			
			//Init and ok send result is ok
			crsResultValue = coUtil.asciiToHex(OK);
			
			//handlePair Init Pair1 HANDLE_EQ
			handlePair = coUtil.getResponePair1();
			
			//Handle in raw format(handle Value)
			String handleIdRaw = coUtil.hexToAscii(coUtil.asciiToHex(coUtil.toLittleEndian(handleId)));
			
			bufferLenPair = coUtil.getInitResponsePair2();
			
			String bufferLenRaw =  coUtil.getBufferLen4HexByte(bufferLen);
			
			//bufferPair Init Pair2 BUFFER_EQ
			bufferPair = coUtil.getInitReponsePair3();
			
			//crcPair pair4 Init  crc
			crcPair = coUtil.getResponePair3();
			
			String crsRaw = resultPair.concat(crsResultValue).concat(handlePair).concat(handleIdRaw).concat(bufferLenPair).concat(bufferLenRaw).concat(bufferPair).concat(buffer).concat(crcPair);

		byte[] encodedCRC = java.util.Base64.getEncoder().encode(DatatypeConverter.parseHexBinary(crsRaw));
		String encodedString = new String(encodedCRC);
		log.debug("encodedString: " + encodedString);
		
		String crcstr = coUtil.calculateCRC(encodedString);
		// Final response String
		finalResponseStr = getInitOKResponseSendChunk(resultPair,crsResultValue,handlePair, handleIdRaw,
				bufferLenPair, bufferLenRaw, bufferPair, buffer,crcPair,crcstr);
		log.debug("finalResponseStr: " + finalResponseStr);
		}else{
			//Added ABORT Response
			finalResponseStr = coUtil.failedResponse(finalResponseStr,crsResultValue,ABORT,resultPair,crcPair);
			log.debug("finalResponseStr: " + finalResponseStr);
			
		}

		}catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return finalResponseStr;
	}

	private String getInitOKResponseSendChunk(String resultPair,
			String crsResultValue, String handlePair, String handleIdRaw,
			String bufferLenPair, String bufferLenRaw, String bufferPair,
			String buffer, String crcPair, String crcstr) {
		
		//Final String 
		String finalString = resultPair.concat(crsResultValue)
				.concat(handlePair).concat(handleIdRaw).concat(bufferLenPair)
				.concat(bufferLenRaw).concat(bufferPair).concat(buffer)
				.concat(crcPair).concat(crcstr);
		log.debug(" CRC value:"+crcstr);
		return finalString;
	}

	public String getRequestType1(String rawMessage,
			String crsResultValue, Map<String, HandleHolder> handleHolderBin,
			Map<String, PartNoHolder> partNosBin,
			Map<String, String> fotaJsonData) {
		try{
			boolean softDeleteFlag = false;
			boolean activePublishedFlag = true;
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
				String reqDev = coUtil.getDeviceVersion(rawMessage);
				if ((fotaInfo.getDevicePartNumber().equals(
						fotaJsonData.get(DEVICE_PARTNUMBER)) && (Integer
						.valueOf(fotaInfo.getSoftVersion()) > Integer
						.valueOf(reqDev)))
						|| (reqDev.equals(fotaInfo.getSoftVersion()) && dbRelaseDate
								.isAfter(reqReleaseDate))) {

					int totalChunks;
					//Generate Handle commented FOTA CR
					// Get Chunk Size from request
					String chunkStr = coUtil.getChunkSize(rawMessage);
					// Decimal conversion
					int chunkSize = coUtil.hex2decimal(chunkStr);
					// PartNumber:Version:Chunk Size
					String storePartNoKey = fotaJsonData.get(DEVICE_PARTNUMBER)
							.concat(":").concat(fotaInfo.getSoftVersion()).concat(":").concat(String.valueOf(chunkSize));
					log.debug("Check upadte Framed Part No key:"+storePartNoKey);
					boolean crcValid = false;
					log.debug("PartNoSize:"+partNosBin.keySet().size());
					PartNoHolder partNoHolder = null;
					for(String key : partNosBin.keySet())
					{
						if(key.contains(storePartNoKey))
						{	
							partNoHolder = new PartNoHolder();
							partNoHolder = partNosBin.get(storePartNoKey);
							log.debug("Part Number Key already exist for same chunk Size="+storePartNoKey);
							if(partNoHolder.getAbortFlag() == false &&  partNoHolder.getChunkSize() == chunkSize)
							{
								break;
							}else{
								partNoHolder = null;
							}
						}
					}
					if (partNoHolder != null) {
						crcValid = partNoHolder.checkCRC32(fotaInfo);
						if ( crcValid == true) {
							// Initially
							HandleHolder holder = coUtil.getHandleHolderValuesFromPartNo(partNoHolder,fotaJsonData,fotaInfo,reqDev,reqReleaseDate);

							//Get Old Handle Id
							handleId = coUtil.getOldHandle(handleHolderBin,fotaJsonData
									.get(DEVICE_SN),partNoHolder);
							if(handleId == null){
								handleId = getHandleNumber();
								//Save device details to DB
								//saveInprogressDeviceDetails(holder);
								holder.setHandleId(handleId);
								handleHolderBin.put(handleId, holder);

							}
							//holder.setHandleId(handleId);
							handleHolderBin.put(handleId, holder);
							/*handleId = getHandleNumber();
							handleHolderBin.put(handleId, holder);*/
							log.debug("New handleId="+handleId+": Same SoftwareVersion="+fotaInfo.getSoftVersion()+":same chunksize="+partNoHolder.getChunkSize());
						} else {
							//Send email notification for CRC validation failed
							sendCRCFailedNotification();
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
							
							// PartNo with Chuck size
							partNosBin.put(storePartNoKey, partNoHolder);
							log.debug("New Part Number Key for different chunk Size="+storePartNoKey);
							// Initially
							//HandleHolder holder = coUtil.getHandleHolderValuesForNewPartNo(chunkSize,fotaJsonData,fotaInfo,reqDev,reqReleaseDate);
							// Initially
							HandleHolder holder = coUtil.getHandleHolderValuesFromPartNo(partNoHolder,fotaJsonData,fotaInfo,reqDev,reqReleaseDate);				
							//Get Old Handle Id
							handleId = coUtil.getOldHandle(handleHolderBin,fotaJsonData
									.get(DEVICE_SN), partNoHolder);
							if(handleId == null){
								handleId = getHandleNumber();
								//Save device details to DB
								//saveInprogressDeviceDetails(holder);
								holder.setHandleId(handleId);
								handleHolderBin.put(handleId, holder);
							}
							
							/*handleId = getHandleNumber();

							handleHolderBin.put(handleId, holder);*/
							handleHolderBin.put(handleId, holder);
							//To capture chunk size
							log.debug("New handleId="+handleId+":New software version="+fotaInfo.getSoftVersion()+":New chunksize="+partNoHolder.getChunkSize());
						} else {
							//Send email notification for CRC validation failed
							sendCRCFailedNotification();
						}
					}

					if (crcValid == false || partNoHolder.getAbortFlag() == true) {
						//Added No Response
						finalResponseStr = coUtil.failedResponse(finalResponseStr,crsResultValue,No,resultPair,crcPair);
						
					} else {
						totalChunks = partNoHolder.getTotalChunk();
						// Response pair1
						resultPair = coUtil.getResponePairResult();

						handlePair = coUtil.getResponePair1();

						// Handle in raw format
						String handleIdRaw = coUtil.hexToAscii(coUtil.asciiToHex(coUtil.toLittleEndian(handleId)));

						// Response pair2
						totalChunkPair = coUtil.getResponePair2();

						// Total chunk in raw format
						String totalChunkRaw = coUtil.getChunkRaw(totalChunks);

						// Response pair3 crc
						crcPair = coUtil.getResponePair3();

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

						String crcstr = coUtil.calculateCRC(encodedString);
						// Final response String
						finalResponseStr = getAllResponseCheckUpdate(
								resultPair, crsResultValue, handlePair,
								handleIdRaw, totalChunkPair, totalChunkRaw,
								crcPair, crcstr);
						log.debug("finalResponseStr: " + finalResponseStr);
					}
				}else{
					//Added No Response
					finalResponseStr = coUtil.failedResponse(finalResponseStr,crsResultValue,No,resultPair,crcPair);
					}
			}else{
				//Added No Response
				finalResponseStr = coUtil.failedResponse(finalResponseStr,crsResultValue,No,resultPair,crcPair);
			}
		
		}catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return finalResponseStr;
	}
	
	private void sendCRCFailedNotification() {
		List<Object[]> resultList = fotaRepositoryUtils.getFOATUsers();
		for (Object[] result : resultList) {
			mailService.sendFotaCRCFailedNotificationEmail((String) result[0],
					(String) result[1]);
		}
	}
	
	private String getAllResponseCheckUpdate(String responsePair1Result,String responsePair1ResultValue, String responsePair1, String handleIdRaw,
			String responsePair2, String totalChunkRaw, String responsePair3,
			String crcRaw) {
		log.debug("totalChunkRaw"+totalChunkRaw);
		
		String finalString = responsePair1Result.concat(responsePair1ResultValue).concat(responsePair1).concat(handleIdRaw).concat(responsePair2)
				.concat(totalChunkRaw).concat(responsePair3).concat(crcRaw);
		return finalString;
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

	/**
	 * setDeviceValues
	 * @param FOTADeviceList
	 * @return
	 */
	public List<FOTADeviceDto> setDeviceValues(
			List<Object[]> FOTADeviceList) {
		List<FOTADeviceDto> FOTADeviceDtoList = new ArrayList<>();
		
		for (Object[] fwareObj : FOTADeviceList) {
			FOTADeviceDto fwareDtoObj = new FOTADeviceDto();
			fwareDtoObj.setDeviceSerialNumber((String)fwareObj[2]);
			fwareDtoObj.setConnectionType((String)fwareObj[3]);
			fwareDtoObj.setDeviceSoftVersion((String)fwareObj[4]);
			fwareDtoObj.setDeviceSoftwareDateTime(new DateTime(fwareObj[5]));
			fwareDtoObj.setUpdatedSoftVersion((String)fwareObj[6]);
			fwareDtoObj.setCheckupdateDateTime(new DateTime(fwareObj[7]));
			log.debug("download start time",fwareObj[8]);
			log.debug("download End time",fwareObj[9]);
			if(Objects.nonNull(fwareObj[8])){
				fwareDtoObj.setDownloadStartDateTime(new DateTime(fwareObj[8]));	
			}
			if(Objects.nonNull(fwareObj[9])){
				fwareDtoObj.setDownloadEndDateTime(new DateTime(fwareObj[9]));
			}
			fwareDtoObj.setDownloadStatus((String)fwareObj[10]);
			fwareDtoObj.setProductType((String)fwareObj[12]);
			fwareDtoObj.setDevicePartNumber(Long.valueOf((String)fwareObj[11]));
			//Calculate Download Time
			if(Objects.nonNull(fwareObj[8]) && Objects.nonNull(fwareObj[9])){
				String totalDownloadTime = coUtil.getDownLoadTime(new DateTime(fwareObj[9]),new DateTime(fwareObj[8]));
				fwareDtoObj.setDownloadTime(totalDownloadTime);
			}else{
				fwareDtoObj.setDownloadTime("");
			}
			
			FOTADeviceDtoList.add(fwareDtoObj);
		}
		return FOTADeviceDtoList;
	}
	/**
	 * getSortAllFirmwareList
	 * @param searchString
	 * @param sortBy
	 * @param isAscending
	 * @return
	 */
	public List<FOTAInfo> getSortAllFirmwareList(String searchString,
			String sortBy, boolean isAscending) {
		List<FOTAInfo> FOTAInfoList = new ArrayList<>();
		List<FOTAInfo> FOTAInfoListUpdate = null;
		FOTAInfoListUpdate = new ArrayList<>();
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
	 * Firmware sorting common method
	 * @param FOTAInfoList
	 * @param sortBy
	 * @param isAscending
	 * @return
	 */
	public List<FOTAInfo> getSortingData(List<FOTAInfo> FOTAInfoList, String sortBy, boolean isAscending) {

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

}
