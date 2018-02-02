package com.hillrom.vest.service.FOTA;

import static com.hillrom.vest.config.FOTA.FOTAConstants.ABORTED;
import static com.hillrom.vest.config.FOTA.FOTAConstants.ABORTED_LIST;
import static com.hillrom.vest.config.FOTA.FOTAConstants.ALL;
import static com.hillrom.vest.config.FOTA.FOTAConstants.FAILURE_LIST;
import static com.hillrom.vest.config.FOTA.FOTAConstants.FOTA_ADMIN;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HANDLE_RAW;
import static com.hillrom.vest.config.FOTA.FOTAConstants.HEX;
import static com.hillrom.vest.config.FOTA.FOTAConstants.INPROGRESS_LIST;
import static com.hillrom.vest.config.FOTA.FOTAConstants.NOT_OK;
import static com.hillrom.vest.config.FOTA.FOTAConstants.No;
import static com.hillrom.vest.config.FOTA.FOTAConstants.OK;
import static com.hillrom.vest.config.FOTA.FOTAConstants.REQUEST_TYPE;
import static com.hillrom.vest.config.FOTA.FOTAConstants.REQUEST_TYPE1;
import static com.hillrom.vest.config.FOTA.FOTAConstants.REQUEST_TYPE12;
import static com.hillrom.vest.config.FOTA.FOTAConstants.REQUEST_TYPE2;
import static com.hillrom.vest.config.FOTA.FOTAConstants.REQUEST_TYPE3;
import static com.hillrom.vest.config.FOTA.FOTAConstants.RESULT;
import static com.hillrom.vest.config.FOTA.FOTAConstants.SUCCESS_LIST;
import static com.hillrom.vest.config.FOTA.FOTAConstants.YES;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.FOTA.FOTAInfo;
import com.hillrom.vest.repository.FOTA.FOTARepository;
import com.hillrom.vest.repository.FOTA.FOTARepositoryUtils;
import com.hillrom.vest.service.MailService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.FOTA.CommonFOTAUtil;
import com.hillrom.vest.service.util.FOTA.FOTAParseUtil;
import com.hillrom.vest.service.util.FOTA.FOTAServiceUtil;
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
	private FOTARepositoryUtils fotaRepositoryUtils;
	
	@Inject
	private FOTAServiceUtil utilService;
	
	@Inject
    private MailService mailService;
	
	@Inject
	private CommonFOTAUtil  coUtil;
	
	//Dynamic part number
	private static Map<String,PartNoHolder> partNosBin = Collections.synchronizedMap(new LinkedHashMap<>());
	private static Map<String,HandleHolder> handleHolderBin = Collections.synchronizedMap(new LinkedHashMap<>());

	@Transactional
	public String FOTAUpdate(String rawMessage){
		String finalStringBase64 = new String();
		String finalResponseStr = new String();
		try{
			String decoded_string = new String();
			String resultPair = new String();
			String crcPair = new String();
			String crsResultValue = new String();
			String handleId = new String();
			Map<String, String> fotaJsonData = new LinkedHashMap<>();
			// Decoding raw data
			decoded_string = decodeRawMessage(rawMessage);
			// Parsing into key value pair
			fotaJsonData = FOTAParseUtil
					.getFOTAJsonDataFromRawMessage(decoded_string);
			
			// Checking if request Type is 01 & //Checking if request Type is 02
			if(coUtil.validateCRC(rawMessage)){
				crsResultValue = coUtil.asciiToHex(YES);
				
				if (fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE1)) {
					//check Update request
					finalResponseStr = utilService.getRequestType1(rawMessage, crsResultValue, handleHolderBin, partNosBin, fotaJsonData);
					log.debug("finalResponseStr: " + finalResponseStr);
					
				}else if(fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE2)){
					//getChunk
					finalResponseStr = utilService.getRequestType2(rawMessage, handleHolderBin, partNosBin, fotaJsonData);
					log.debug("finalResponseStr: " + finalResponseStr);
					
				}else if (fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE3)) {
					//Software update
					finalResponseStr = getRequestType3(rawMessage, handleId,handleHolderBin,resultPair, crcPair, fotaJsonData);
					log.debug("finalResponseStr: " + finalResponseStr);
					
				}else if (fotaJsonData.get(REQUEST_TYPE).equals(REQUEST_TYPE12)) {
					//getChunkWithChunkNumber
					finalResponseStr = coUtil.getChunkWithChunkNumber(rawMessage,handleId,handleHolderBin,partNosBin);
					log.debug("finalResponseStr: " + finalResponseStr);
				}
			
			}else if(!coUtil.validateCRC(rawMessage)){
				//Added NOT OK Response
				finalResponseStr = coUtil.failedResponse(finalResponseStr,crsResultValue,NOT_OK,resultPair,crcPair);
				log.debug("finalResponseStr: " + finalResponseStr);
			}
		}catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		byte[] encoded = java.util.Base64.getEncoder().encode(DatatypeConverter.parseHexBinary(finalResponseStr));
		finalStringBase64 = new String(encoded);
		log.error("finalStringBase64: " +finalStringBase64);
		return finalStringBase64;
	}
	
	private String getRequestType3(String rawMessage, String crsResultValue,
			Map<String, HandleHolder> handleHolderBin, String handleId,String resultPair, Map<String, String> fotaJsonData) {
		String finalResponseStr = "";
		String crcPair = "";
		// Initially
		HandleHolder holder = new HandleHolder();
		// Get handle from request
		handleId = coUtil.getValuesFromRequest(rawMessage,HANDLE_RAW);
		log.debug("Send Chunk Handle Id ="+handleId);
		// Get handle object based on handleId
		holder = handleHolderBin.get(handleId);
		// Frame key to get partNumber details
		if (Objects.nonNull(holder)) {
			if (fotaJsonData.get(RESULT).equals(OK)) {
				//Added method to store device details
				finalResponseStr = requestType3Response(finalResponseStr,handleId,SUCCESS_LIST,rawMessage,resultPair,crsResultValue);
				//handleHolderBin.remove(handleId);
				
			} else if (fotaJsonData.get(RESULT).equals(NOT_OK)) {
				//Added method to store device details
				finalResponseStr = requestType3Response(finalResponseStr,handleId,FAILURE_LIST,rawMessage,resultPair,crsResultValue);
				//handleHolderBin.remove(handleId);

			} else if (fotaJsonData.get(RESULT).equals(ABORTED)) {
				//Added method to store device details
				finalResponseStr = requestType3Response(finalResponseStr,handleId,ABORTED_LIST,rawMessage,resultPair,crsResultValue);
				//handleHolderBin.remove(handleId);
			}
		} else {
			// Added No Response
			finalResponseStr = coUtil.failedResponse(finalResponseStr, crsResultValue,
					No, resultPair, crcPair);
			//return finalResponseStr;
		}
		return finalResponseStr;
	}

	public String requestType3Response(String finalResponseStr,
			String handleId, String status, String rawMessage,
			String resultPair, String crsResultValue) {

		//Save device details to DB
		utilService.saveDeviceDetails(handleId,status,rawMessage,handleHolderBin);
		// Result pair1
		resultPair = coUtil.getResponePairResult();

		crsResultValue = coUtil.asciiToHex(OK);
		String crcPair = coUtil.getResponePair3();
		
		String crsRaw = resultPair.concat(crsResultValue).concat(crcPair);
		byte[] encodedCRC = java.util.Base64.getEncoder().encode(DatatypeConverter.parseHexBinary(crsRaw));
		String encodedString = new String(encodedCRC);
		log.debug("encodedString: " + encodedString);
		
		String crcValue = coUtil.calculateCRC(encodedString);
		//Final String 
		finalResponseStr = resultPair.concat(crsResultValue).concat(crcPair).concat(crcValue);

		return finalResponseStr;
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

			List<Object[]> resultList = fotaRepositoryUtils.getDeviceListByStatus(status,queryString,sortBy,isAscending);
			
			FOTADeviceDtoList = utilService.setDeviceValues(resultList);
			
		} else if (status.equals(FAILURE_LIST)) {
			List<Object[]> resultList = fotaRepositoryUtils.getDeviceListByStatus(status,queryString,sortBy,isAscending);
			
			FOTADeviceDtoList = utilService.setDeviceValues(resultList);

		} else if (status.equals(ABORTED_LIST)) {
			
			List<Object[]> resultList = fotaRepositoryUtils.getDeviceListByStatus(status,queryString,sortBy,isAscending);
						
			FOTADeviceDtoList = utilService.setDeviceValues(resultList);

		}else if (status.equals(INPROGRESS_LIST)) {
			
			List<Object[]> resultList = fotaRepositoryUtils.getDeviceListByStatus(status,queryString,sortBy,isAscending);
						
			FOTADeviceDtoList = utilService.setDeviceValues(resultList);

		}else if (status.equals(ALL)) {
			
			List<Object[]> resultList = fotaRepositoryUtils.getAllList(status,queryString,sortBy,isAscending);
			
			FOTADeviceDtoList = utilService.setDeviceValues(resultList);
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
			FOTAInfoList = utilService.getSortAllFirmwareList(searchString,sortBy,isAscending);
			
		}
		return FOTAInfoList;
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
		List<FOTAInfo> FOTAInfoList = new ArrayList<>();
		List<FOTAInfo> FOTAInfoListUpdate = null;
		FOTAInfoListUpdate = new ArrayList<>();
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
		FOTAInfoListUpdate = utilService.getSortingData(FOTAInfoListUpdate,sortBy,isAscending);
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
		List<FOTAInfo> FOTAInfoList = new ArrayList<>();
		List<FOTAInfo> FOTAInfoListUpdate = null;
		FOTAInfoListUpdate = new ArrayList<>();
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
		FOTAInfoListUpdate = utilService.getSortingData(FOTAInfoListUpdate,sortBy,isAscending);
				return FOTAInfoListUpdate;
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
	    
		if (StringUtils.isNotEmpty(crc32Dt0.getRegion1StartAddress()) && crc32Dt0.getRegion1StartAddress().length() == 8) {
			crcStartAddress = Long.parseLong(crc32Dt0.getRegion1StartAddress(),16);
		}
		if (StringUtils.isNotEmpty(crc32Dt0.getRegion1EndAddress()) && crc32Dt0.getRegion1EndAddress().length() == 8) {
			crcEndAddress = Long.parseLong(crc32Dt0.getRegion1EndAddress(),16);
		}
		
		if (StringUtils.isNotEmpty(crc32Dt0.getRegion1CRCLocation()) && crc32Dt0.getRegion1CRCLocation().length() == 8) {
			crcLocationAddress = Long.parseLong(crc32Dt0.getRegion1CRCLocation(),16);
		}
		if (StringUtils.isNotEmpty(crc32Dt0.getRegion2StartAddress()) && crc32Dt0.getRegion2StartAddress().length() == 8) {
			crc2StartAddress = Long.parseLong(crc32Dt0.getRegion2StartAddress(),16);
		}
		if (StringUtils.isNotEmpty(crc32Dt0.getRegion2EndAddress()) && crc32Dt0.getRegion2EndAddress().length() == 8) {
			crc2EndAddress = Long.parseLong(crc32Dt0.getRegion2EndAddress(),16);
		}
		
		if (StringUtils.isNotEmpty(crc32Dt0.getRegion2CRCLocation()) && crc32Dt0.getRegion2CRCLocation().length() == 8) {
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

            //record_length = hexRecord[0];
            record_length = (int)(hexRecord[0]&0xFF);
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
	    		if(rdr != null){
	    			rdr.close();
	    		}
	    		if(isr != null){
	    			isr.close();
	    		}
	    		if(fs != null){
	    			fs.close();
	    		}
	    		if(crcData != null){
	    			crcData.close();
	    		}
	    		if(crcData2 != null){
	    			crcData2.close();
	    		}
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
				
				String partNo = fotaInfoExist.getDevicePartNumber();

				for(Map.Entry<String,PartNoHolder> entry : partNosBin.entrySet()){
					String key = entry.getKey();
					String[] str = key.split(":");
					if(str[0].equals(partNo)){
						PartNoHolder partNoHolder = new PartNoHolder();
						partNoHolder = partNosBin.get(key);
						partNoHolder.setAbortFlag(true);
						log.debug("key :"+key);
						partNosBin.put(key, partNoHolder);
					}
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
			result = coUtil.validateApprCRC32(fotaInfo,apprDto.getRegion1CRC(), apprDto.getRegion2CRC());
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

}
