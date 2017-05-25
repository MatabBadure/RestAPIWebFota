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


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Service
@Transactional
public class TimsInputReaderService {

	private final Logger log = LoggerFactory.getLogger(TimsInputReaderService.class);
	
	@Inject
	private TimsRepository timsRepository;
	
	//@Scheduled(cron="0/5 * * * * * ")
	public void readcsv() 
	{


	        String csvFile = "c:/temp/tims_patients.csv";
	        String line = "";
	        String cvsSplitBy = ",";
	        String Outdata = "";
	        String[] data = null;
	        DateTimeFormatter dobFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
	        DateTimeFormatter deviceAssocdateFormat = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");

	        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
	        	HashSet hs = new HashSet();

	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	               data = line.split(cvsSplitBy);
	               
	               PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		            String record = "";
		            for(int i=0;i<28;i++){
		            	record = i==27?record + data[i]:record + data[i]+",";
		            }
		            
		            //log.debug("Base64 Received Data for ingestion in receiveDataCharger : " + record);
		            
	            	patientInfoDTO.setId(data[0]);
	            	patientInfoDTO.setHillrom_id(data[1]);
	            	patientInfoDTO.setHub_id(data[2]);
	            	patientInfoDTO.setSerial_number(data[3]);
	            	patientInfoDTO.setBluetooth_id(data[4]);
	            	patientInfoDTO.setTitle(data[5]);
	            	patientInfoDTO.setFirst_name(data[6]);
	            	patientInfoDTO.setMiddle_name(data[7]);
	            	patientInfoDTO.setLast_name(data[8]);
	            	patientInfoDTO.setDob(data[9].equalsIgnoreCase("NULL")? null: DateTime.parse(data[9],dobFormat));
	            	patientInfoDTO.setEmail(data[10]);
	            	patientInfoDTO.setZipcode(data[11]);
	            	patientInfoDTO.setWeb_login_created(Boolean.getBoolean(data[12]));
	            	patientInfoDTO.setPrimary_phone(data[13]);
	            	patientInfoDTO.setMobile_phone(data[14]);
	            	patientInfoDTO.setGender(data[15]);
	            	patientInfoDTO.setLang_key(data[16]);
	            	patientInfoDTO.setExpired(Boolean.getBoolean(data[17]));
	            	patientInfoDTO.setExpired_date(data[18].equalsIgnoreCase("NULL")? null: DateTime.parse(data[18],deviceAssocdateFormat));
	            	patientInfoDTO.setAddress(data[19]);
	            	patientInfoDTO.setCity(data[20]);
	            	patientInfoDTO.setState(data[21]);
	            	patientInfoDTO.setDevice_assoc_date(data[22].equalsIgnoreCase("NULL")? null: DateTime.parse(data[22],deviceAssocdateFormat));
	            	patientInfoDTO.setTraining_date(data[23].equalsIgnoreCase("NULL")? null: DateTime.parse(data[23],deviceAssocdateFormat));
	            	patientInfoDTO.setPrimary_diagnosis(data[24]);
	            	patientInfoDTO.setGarment_type(data[25]);
	            	patientInfoDTO.setGarment_size(data[26]);
	            	patientInfoDTO.setGarment_color(data[27]);

		            hs.add(patientInfoDTO);

	            }
	            
	            log.debug("Final TIMS_Patients Table : " + hs);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	       
	} 






 

	
}
