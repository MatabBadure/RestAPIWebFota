package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AnnouncementsPermissionRepository;
import com.hillrom.vest.repository.AnnouncementsRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.AnnouncementsDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.PatientInfoDTO;
import com.hillrom.vest.web.rest.dto.ProtocolDataTempDTO;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.http.HttpStatus;

import com.hillrom.vest.config.Constants;

import java.util.Map;

@Service
@Transactional
public class TimsInputReaderService {

	private final Logger log = LoggerFactory.getLogger("com.hillrom.vest.tims");
	
	
	@Inject
	private TimsService timsService;
	
	//@Scheduled(cron="0/5 * * * * * ")
	public void ExecuteTIMSJob() 
	{
		
		MDC.put("logFileName", "timslogFile." + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
		
		Map<Integer, PatientInfoDTO> fileRecords = readcsv();
		//Map<Integer, ProtocolDataTempDTO> protocolfileRecords =readProtocolcsv();
		
		log.debug("Starting to process records ");
		for (Map.Entry<Integer, PatientInfoDTO> entry : fileRecords.entrySet()) {
		    Integer position = entry.getKey();
		    PatientInfoDTO record = entry.getValue();
		    log.debug("Processing record position : "+position);
		    if(record.getDevice_type().equalsIgnoreCase("VEST")){
		    	log.debug("Inside VEST loop ");
		    	timsService.CASE1_NeitherPatientNorDeviceExist_VEST(record);
		    	//timsService.CASE2_PatientExistsWithNODevice_VEST(record);
		    	timsService.CASE3_PatientHasMonarchAddVisivest_VEST(record);
		    	timsService.CASE4_PatientHasDifferentVisivestSwap_VEST(record);
		    	timsService.CASE5_DeviceOwnedByShell_VEST(record);
		    	//timsService.CASE6_DeviceOwnedByDifferentPatient_VEST(record);
		    	//timsService.CASE7_DeviceIsOrphanPatientDoesNotExist_VEST(record);
		    	//timsService.CASE8_DeviceIsOrphanButPatientExist_VEST(record);
		    	//timsService.CASE9_PatientHasDifferentVisivestSwap_VEST(record);
		    	timsService.CASE10_PatientHasMonarchAddVisivest_VEST(record);
		    	//timsService.CASE11_PatientExistsWithNODevice_VEST(record);
		    	timsService.CASE12_PatientHasVisivestMergeExistingMonarch_MONARCH_VEST(record);
		    	
		    }

		    if(record.getDevice_type().equalsIgnoreCase("MONARCH")){
		    	log.debug("Inside MONARCH loop ");
		    	timsService.CASE1_NeitherPatientNorDeviceExist_MONARCH(record);
		    	//timsService.CASE2_PatientExistsWithNODevice_MONARCH(record);
		    	timsService.CASE3_PatientHasVisivestAddMonarch_MONARCH(record);
		    	timsService.CASE4_PatientHasDifferentMonarchSwap_MONARCH(record);
		    	timsService.CASE5_DeviceOwnedByShell_MONARCH(record);
		    	//timsService.CASE6_DeviceOwnedByDifferentPatient_MONARCH(record);
		    	//timsService.CASE7_DeviceIsOrphanPatientDoesNotExist_MONARCH(record);
		    	//timsService.CASE8_DeviceIsOrphanButPatientExist_MONARCH(record);
		    	//timsService.CASE9_PatientHasDifferentMonarchSwap_MONARCH(record);
		    	timsService.CASE10_PatientHasVisivestAddMonarch_MONARCH(record);
		    	//timsService.CASE11_PatientExistsWithNODevice_MONARCH(record);
		    	timsService.CASE12_PatientHasVisivestMergeExistingMonarch_MONARCH_VEST(record);
		    	
		    }
		}
		
	}

	
	public Map readcsv() 
	{


	        String csvFile = Constants.TIMS_CSV_FILE_PATH + "flat file.csv";
		    log.debug("Started reading flat file : " + csvFile);
	        String line = "";
	        String cvsSplitBy = ",";
	        String Outdata = "";
	        String[] data = null;
	        DateFormat sourceFormat = new SimpleDateFormat("MM/dd/yyyy");
	        DateTimeFormatter dobFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
	        DateTimeFormatter deviceAssocdateFormat = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");

	        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
	        	Map<Integer, PatientInfoDTO> fileRecords = new HashMap<Integer, PatientInfoDTO>();
	        	
	        	boolean header = true;
            	
	        	int k = 1; // record position in file
            	
	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	               data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); //line.split(cvsSplitBy);
	               
	               PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		            String record = "";
		            for(int i=0;i<27;i++){
		            	try{
		            		data[i] = Objects.nonNull(data[i]) ? data[i] : "";
		            		
		            		record = i==26?record + data[i]:record + data[i]+",";
		            	}catch(ArrayIndexOutOfBoundsException ex){

		            	}
		            	
		            }
		            
		            
		            
		            String s = "";
		            for(int j=0;j<data.length;j++){
		            	s = s + "\t" + data[j];
		            }
		            log.debug("Excel File Read as : " + s);

		            
		            
		            if(!header){

			            patientInfoDTO.setDevice_type(data[0]);
			            patientInfoDTO.setTims_cust(data[1]);
			            patientInfoDTO.setSerial_num(data[2]);
			            patientInfoDTO.setShip_dt(data[3].equalsIgnoreCase("")? null: LocalDate.parse(data[3],dobFormat));
			            patientInfoDTO.setHub_id(data[4]);
			            patientInfoDTO.setBluetooth_id(data[5]);
			            patientInfoDTO.setGarment_cd(data[6]);
			            patientInfoDTO.setGarment_type(data[7]);
			            patientInfoDTO.setGarment_size(data[8]);
			            patientInfoDTO.setGarment_color(data[9]);
			            patientInfoDTO.setTitle(data[10]);
			            patientInfoDTO.setFirst_nm(data[11]);
			            patientInfoDTO.setMiddle_nm(data[12]);
			            patientInfoDTO.setLast_nm(data[13]);
			            patientInfoDTO.setEmail(data[14]);
			            if(data[15].charAt(0)=='"'&&data[15].charAt(data[15].length()-1)=='"')
			            {
			            	data[15] = data[15].substring(1, data[15].length() - 1);
			            }
			            else
			            {
			            	data[15] = data[15];
			            }


			            patientInfoDTO.setAddress(data[15]);
			            patientInfoDTO.setZip_cd(data[16]);
			            patientInfoDTO.setPrimary_phone(data[17]);
			            patientInfoDTO.setMobile_phone(data[18]);
			            patientInfoDTO.setTrain_dt(data[19].equalsIgnoreCase("")? null: LocalDate.parse(data[19],dobFormat));
			            patientInfoDTO.setDob(data[20].equalsIgnoreCase("")? null: LocalDate.parse(data[20],dobFormat));
			            if(data.length >= 22){
			            	patientInfoDTO.setGender(data[21]);
			            }else{
			            	patientInfoDTO.setGender(null);
			            }
			            if(data.length >= 23){
			            	patientInfoDTO.setLang_key(data[22]);
			            }else{
			            	patientInfoDTO.setLang_key(null);
			            }
			            if(data.length >= 24){
			            	patientInfoDTO.setDx1(data[23]);
			            }else{
			            	patientInfoDTO.setDx1(null);
			            }
			            if(data.length >= 25){
			            	patientInfoDTO.setDx2(data[24]);
			            }else{
			            	patientInfoDTO.setDx2(null);
			            }
			            if(data.length >= 26){
			            	patientInfoDTO.setDx3(data[25]);
			            }else{
			            	patientInfoDTO.setDx3(null);
			            }
			            if(data.length >= 27){
			            	patientInfoDTO.setDx4(data[26]);
			            }else{
			            	patientInfoDTO.setDx4(null);
			            }
			        	
	
	
			            fileRecords.put(k++, patientInfoDTO);
		            }
		            
		            header = false;
		            
		            
	            }
	            
	            log.debug("Excel File contents in HashSet : " + fileRecords);
	            return fileRecords;
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        }
	        
	        

	       
	} 


	public Map readProtocolcsv() 
	{


	        String csvFile = Constants.TIMS_CSV_FILE_PATH + "protocol_data.csv";
	        log.debug("Started reading protocol data flat file : " + csvFile);
	        String line = "";
	        String cvsSplitBy = ",";
	        String Outdata = "";
	        String[] data = null;
	        DateFormat sourceFormat = new SimpleDateFormat("MM/dd/yyyy");
	        DateTimeFormatter dobFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
	        DateTimeFormatter deviceAssocdateFormat = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");

	        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
	        	Map<Integer, ProtocolDataTempDTO> fileRecords = new HashMap<Integer, ProtocolDataTempDTO>();
	        	
	        	boolean header = true;
            	
	        	int k = 1; // record position in file
            	
	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	               data = line.split(cvsSplitBy);
	               
	               ProtocolDataTempDTO protocolDataTempDTO = new ProtocolDataTempDTO();
		            String record = "";
		            for(int i=0;i<12;i++){
		            	try{
		            		data[i] = Objects.nonNull(data[i]) ? data[i] : "";
		            		record = i==11?record + data[i]:record + data[i]+",";
		            	}catch(ArrayIndexOutOfBoundsException ex){

		            	}
		            	
		            }
		            
		            
		            
		            String s = "";
		            for(int j=0;j<data.length;j++){
		            	s = s + "\t" + data[j];
		            }
		            log.debug("Excel File Read as : " + s);

		            
		            
		            if(!header){

		            	protocolDataTempDTO.setPatient_id(data[0]);
		            	protocolDataTempDTO.setType(data[1]);
		            	protocolDataTempDTO.setTreatmentsPerDay(Integer.parseInt(data[2]));
		            	protocolDataTempDTO.setTreatmentLabel(data[3]);
		            	protocolDataTempDTO.setMinMinutesPerTreatment(data[4].trim().length()==0?0:Integer.parseInt(data[4]));
		            	protocolDataTempDTO.setMaxMinutesPerTreatment(Integer.parseInt(data[5]));
		            	protocolDataTempDTO.setMinFrequency(Integer.parseInt(data[6]));
		            	protocolDataTempDTO.setMaxFrequency(Integer.parseInt(data[7]));
		            	protocolDataTempDTO.setMinPressure(Integer.parseInt(data[8]));
		            	protocolDataTempDTO.setMaxPressure(Integer.parseInt(data[9]));
		            	protocolDataTempDTO.setTo_be_inserted(Integer.parseInt(data[10]));
		            	protocolDataTempDTO.setId(data[11]);
		            	
			        	
	
	
			            fileRecords.put(k++, protocolDataTempDTO);
		            }
		            
		            header = false;
		            
		            
	            }
	            
	            log.debug("Excel File contents in HashMap : " + fileRecords);
	            return fileRecords;
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        }
	        
	        

	       
	} 




 

	
}
