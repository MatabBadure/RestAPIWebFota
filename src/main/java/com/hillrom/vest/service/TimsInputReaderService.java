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
import java.sql.SQLException;
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
	
	public static boolean processed_atleast_one = false;
	
	public String logFileName;
	public static boolean failureFlag = false;
	public static boolean mandatoryFieldFlag = true;
	public static boolean monarchBluetoothFlag = true;
	public static boolean  serialNumberFlag = true;
	public static boolean  CSVFileFlag = true;
	@Inject
	private TimsService timsService;
	
	@Inject
	private MailService mailService;
	@Scheduled(cron="00 30 08 * * * ")
	public void ExecuteTIMSJob() throws Exception
	{
		
		logFileName  = "timslogFile." + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());	
		try{
    MDC.put("logFileName", logFileName);

		log.debug("Status           TIMS Id        Serial Number        Result        Remarks");
		this.mandatoryFieldFlag = true;
		this.monarchBluetoothFlag = true;
		this.serialNumberFlag = true;
		
		Map<Integer, PatientInfoDTO> fileRecords = readcsv();
		//Map<Integer, ProtocolDataTempDTO> protocolfileRecords =readProtocolcsv();
		
	    boolean failureflag = true;
	    this.failureFlag = false;
		this.processed_atleast_one = false;
		for (Map.Entry<Integer, PatientInfoDTO> entry : fileRecords.entrySet()) {
		    Integer position = entry.getKey();
		    PatientInfoDTO record = entry.getValue();
		
		  if(record.getDevice_type().equalsIgnoreCase("VEST")){
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
		    	timsService.CASE12_PatientHasMonarchMergeExistingVisivest_VEST(record);
		    //	timsService.CASE13_ExistedSerialNumberandDifferentHillromID_VEST(record);
		    	
		    }

		    if(record.getDevice_type().equalsIgnoreCase("MONARCH")){
		    	
		    	/*If the new monarch device added is  one without connectvity then ensure that 
		    	you dont create a combo patient in TIMS visiview code.*/
		 	if(record.getBluetooth_id()!=null && (!record.getBluetooth_id().isEmpty()))
		    	{
			    	
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
			    	timsService.CASE12_PatientHasVisivestMergeExistingMonarch_MONARCH(record);
			    //	timsService.CASE13_ExistedSerialNumberandDifferentHillromID_MONARCH(record);
		    	}else{
		    		monarchBluetoothFlag = false;
		    		log.debug("Created       " +record.getTims_cust()+ "        " +record.getSerial_num()+ "        "+"Failure"+ "        "
							+ "Bluetooth Id / Connectivity Id is not present");
		    	}
		    }
		}
		if(!CSVFileFlag)
		{
			throw new Exception("any exceoption error.");
		}
		if(!serialNumberFlag)
		{
			throw new Exception("any exceoption error.");
		}
        if(!monarchBluetoothFlag){
			
			throw new Exception("any exceoption error.");
			
		}
		if(!mandatoryFieldFlag){
			
			throw new Exception("any exceoption error.");
			
		}
		if(failureFlag){
			throw new Exception("any exceoption error.");
		}
		if(processed_atleast_one){
			log.debug(" ");
			log.debug("All Records Executed Successfully");
		}
		
		if(!processed_atleast_one){
			log.debug("Success        NA               NA             Success           The csv file has already been executed or unable to process any of the records.");
			log.debug(" ");
			log.debug("All Records Executed Successfully");
			//throw new Exception("The csv file has already been executed or unable to process any of the records.");
		}
		
		}catch(Exception ex){
			mailService.sendTIMSLog(logFileName);
			ex.printStackTrace();
			throw new HillromException("Error in TIMS Script Execution " , ex);
		}
		
	}

	
	public Map readcsv() 
	{        
		      String csvFile = Constants.TIMS_CSV_FILE_PATH + "flatfile.csv";
		      File flatFile = new File(csvFile);
		      if(!flatFile.exists()) { 
		    	  log.debug("Failure        NA               NA             Failure           The csv file is not present ");
		    	  CSVFileFlag = false;
		          
		      }
		  //  log.debug("Started reading flat file : " + csvFile);
	        String line = "";
	        String cvsSplitBy = ",";
	        String Outdata = "";
	        String[] data = null;
	        
	  /*      DateFormat sourceFormat = new SimpleDateFormat("MM/dd/yyyy");
	        DateTimeFormatter dobFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
	        DateTimeFormatter deviceAssocdateFormat = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss"); */

	        
	        DateFormat sourceFormat = new SimpleDateFormat("yyyy-mm-dd");
	        DateTimeFormatter dobFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
	        DateTimeFormatter deviceAssocdateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

	        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
	        	Map<Integer, PatientInfoDTO> fileRecords = new HashMap<Integer, PatientInfoDTO>();
	        	
	        	boolean header = true;
            	
	        	int k = 1; // record position in file
            	
	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	               data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); //line.split(cvsSplitBy);
	               
	               PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		            String record = "";
		            for(int i=0;i<28;i++){
		            	try{
		            		data[i] = Objects.nonNull(data[i]) ? data[i] : "";
		            		
		            		record = i==27?record + data[i]:record + data[i]+",";
		            	}catch(ArrayIndexOutOfBoundsException ex){

		            	}
		            	
		            }
		            
		            
		            
		            String s = "";
		            for(int j=0;j<data.length;j++){
		            	s = s + "\t" + data[j];
		            }
		        //    log.debug("Excel File Read as : " + s);

		            
		            
		            if(!header){	            	
		            	if(data.length >= 3 && data[2].equalsIgnoreCase("")){
		            		log.debug("Created       " +"  NA  " + "        " +data[3]+ "       "+"Failure"+ "        "
									+ "Tims Id is not present");
		            		mandatoryFieldFlag = false;
		            	}else if(data.length >= 4 && data[3].equalsIgnoreCase("")){
		            		log.debug("Created       " +data[2]+ "        " +"  NA  "+ "          "+"Failure"+ "        "
									+ "Serial number is not present");
		            		mandatoryFieldFlag = false;
		            	}
		            	else if(data.length >= 13 && data[12].equalsIgnoreCase("")){
		            		log.debug("Created       " +data[2]+ "        " +data[3]+ "        "+"Failure"+ "        "
									+ "First name is not present");
		            		mandatoryFieldFlag = false;
		            	}
		            	else if(data.length >= 15 && data[14].equalsIgnoreCase("")){
		            		log.debug("Created       " +data[2]+ "        " +data[3]+ "        "+"Failure"+ "        "
									+ "Last name is not present");
		            		mandatoryFieldFlag = false;
		            	}else if(data.length >= 18 && data[17].equalsIgnoreCase("")){
		            		log.debug("Created       " +data[2]+ "        " +data[3]+ "        "+"Failure"+ "        "
									+ "Zip code is not present");
		            		mandatoryFieldFlag = false;
		            	}else if(data.length >= 22 && data[21].equalsIgnoreCase("")){
		            		log.debug("Created       " +data[2]+ "        " +data[3]+ "        "+"Failure"+ "        "
									+ "DOB is not present");
		            		mandatoryFieldFlag = false;
		            	}else{ 
		            	patientInfoDTO.setIndex(data[0]);
			            patientInfoDTO.setDevice_type(data[1]);
			            patientInfoDTO.setTims_cust(data[2]);
			            patientInfoDTO.setSerial_num(data[3]);
			            patientInfoDTO.setShip_dt(data[4].equalsIgnoreCase("")? null: LocalDate.parse(data[4],dobFormat));
			            patientInfoDTO.setHub_id(data[5]);
			            patientInfoDTO.setBluetooth_id(data[6]);
			            patientInfoDTO.setGarment_cd(data[7]);
			            patientInfoDTO.setGarment_type(data[8]);
			            patientInfoDTO.setGarment_size(data[9]);
			            patientInfoDTO.setGarment_color(data[10]);
			            patientInfoDTO.setTitle(data[11]);
			            patientInfoDTO.setFirst_nm(data[12]);
			            patientInfoDTO.setMiddle_nm(data[13]);
			            patientInfoDTO.setLast_nm(data[14]);			         
			            patientInfoDTO.setEmail(data[15].trim().isEmpty()? null: data[15]);
			            if(!data[16].isEmpty() && data[16]!=null){
				            if(data[16].charAt(0)=='"'&&data[16].charAt(data[16].length()-1)=='"')
				            {
				            	data[16] = data[16].substring(1, data[16].length() - 1);
				            }
			            }
			            else
			            {
			            	data[16] = data[16];
			            }
			            


			            patientInfoDTO.setAddress(data[16]);
			           /*if(data.length >= 18 && data[17].equalsIgnoreCase("")){
			            	 patientInfoDTO.setZip_cd(null);
			            }else{
			            	patientInfoDTO.setZip_cd(data[17]);
			            }*/
			            patientInfoDTO.setZip_cd(data[17]);
			            patientInfoDTO.setPrimary_phone(data[18]);
			            patientInfoDTO.setMobile_phone(data[19]);
			            patientInfoDTO.setTrain_dt(data[20].equalsIgnoreCase("")? null: LocalDate.parse(data[20],deviceAssocdateFormat));
			            //patientInfoDTO.setTrain_dt(data[19].equalsIgnoreCase("")? null: LocalDate.parse(data[19],dobFormat));
			           /*if(data.length >= 22 && data[21].equalsIgnoreCase("")){
			            	 patientInfoDTO.setDob(null);
			            }else{
			            	patientInfoDTO.setDob(data[21]);
			            }*/
			            patientInfoDTO.setDob(data[21]);
			            if(data.length >= 23){
			            	patientInfoDTO.setGender(data[22]);
			            }else{
			            	patientInfoDTO.setGender(null);
			            }
			            if(data.length >= 24 && data[23].equalsIgnoreCase("")){
			            	patientInfoDTO.setLang_key(null);
			            }else{
			            	patientInfoDTO.setLang_key(data[23]);
			            	
			            }
			            if(data.length >= 25){
			            	patientInfoDTO.setDx1(data[24]);
			            }else{
			            	patientInfoDTO.setDx1(null);
			            }
			            if(data.length >= 26){
			            	patientInfoDTO.setDx2(data[25]);
			            }else{
			            	patientInfoDTO.setDx2(null);
			            }
			            if(data.length >= 27){
			            	patientInfoDTO.setDx3(data[26]);
			            }else{
			            	patientInfoDTO.setDx3(null);
			            }
			            if(data.length >= 28){
			            	patientInfoDTO.setDx4(data[27]);
			            }else{
			            	patientInfoDTO.setDx4(null);
			            }
			        	
	
	
			            fileRecords.put(k++, patientInfoDTO);
		           }
		            } 
		            header = false;
		            
		            
	            }
	            
	          //  log.debug("Excel File contents in HashSet : " + fileRecords);
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
