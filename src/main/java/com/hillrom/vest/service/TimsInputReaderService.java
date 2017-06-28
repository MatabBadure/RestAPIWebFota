package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AnnouncementsPermissionRepository;
import com.hillrom.vest.repository.AnnouncementsRepository;
import com.hillrom.vest.repository.TimsRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.AnnouncementsDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.PatientInfoDTO;

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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Service
@Transactional
public class TimsInputReaderService {

	private final Logger log = LoggerFactory.getLogger("com.hillrom.vest.tims");
	
	@Inject
	private TimsRepository timsRepository;
	
	@Scheduled(cron="0/5 * * * * * ")
	public void readcsv() 
	{


	        String csvFile = "c:/temp/flat file.csv";
	        log.debug("Started reading flat file : " + csvFile);
	        String line = "";
	        String cvsSplitBy = ",";
	        String Outdata = "";
	        String[] data = null;
	        DateFormat sourceFormat = new SimpleDateFormat("MM/dd/yyyy");
	        DateTimeFormatter dobFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
	        DateTimeFormatter deviceAssocdateFormat = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");

	        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
	        	HashSet hs = new HashSet();
	        	
	        	boolean header = true;

	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	               data = line.split(cvsSplitBy);
	               
	               PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		            String record = "";
		            for(int i=0;i<18;i++){
		            	try{
		            		data[i] = Objects.nonNull(data[i]) ? data[i] : "";
		            		record = i==17?record + data[i]:record + data[i]+",";
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
			            patientInfoDTO.setGarment_cd(data[5]);
			            patientInfoDTO.setGarment_type(data[6]);
			            patientInfoDTO.setGarment_size(data[7]);
			            patientInfoDTO.setGarment_color(data[8]);
			            patientInfoDTO.setFirst_nm(data[9]);
			            patientInfoDTO.setLast_nm(data[10]);
			            patientInfoDTO.setZip_cd(data[11]);
			            patientInfoDTO.setTrain_dt(data[12].equalsIgnoreCase("")? null: LocalDate.parse(data[12],dobFormat));
			            patientInfoDTO.setDob(data[13].equalsIgnoreCase("")? null: LocalDate.parse(data[13],dobFormat));
			            if(data.length >= 15){
			            	patientInfoDTO.setDx1(data[14]);
			            }else{
			            	patientInfoDTO.setDx1(null);
			            }
			            if(data.length >= 16){
			            	patientInfoDTO.setDx2(data[15]);
			            }else{
			            	patientInfoDTO.setDx2(null);
			            }
			            if(data.length >= 17){
			            	patientInfoDTO.setDx3(data[16]);
			            }else{
			            	patientInfoDTO.setDx3(null);
			            }
			            if(data.length >= 18){
			            	patientInfoDTO.setDx4(data[17]);
			            }else{
			            	patientInfoDTO.setDx4(null);
			            }
			        	
	
	
			            hs.add(patientInfoDTO);
		            }
		            
		            header = false;
		            
		            
	            }
	            
	            log.debug("Excel File contents in HashSet : " + hs);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	       
	} 






 

	
}
