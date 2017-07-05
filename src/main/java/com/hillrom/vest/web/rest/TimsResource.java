package com.hillrom.vest.web.rest;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List; 
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.minidev.json.JSONObject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.TimsUserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.AnnouncementsService;
import com.hillrom.vest.service.TimsInputReaderService;
import com.hillrom.vest.service.TimsService;
import com.hillrom.vest.web.rest.dto.AnnouncementsDTO;
import com.hillrom.vest.web.rest.dto.PatientInfoDTO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.hillrom.vest.config.Constants.LOG_DIRECTORY;
import static com.hillrom.vest.config.Constants.MATCH_STRING;


@RestController
@RequestMapping("/api")
public class TimsResource {

	@Inject
	private TimsService timsService;
	
	@Inject
	private TimsInputReaderService timsInputReaderService;

	@Inject
	private TimsUserRepository timsUserRepository;
	
	

	/**
     * GET  /listLogDirectory
     */
	@RequestMapping(value="/listLogDirectory", method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listLogDirectory(){

		JSONObject jsonObject = new JSONObject();
		try{
			
			List<String> returnVal = timsService.listLogDirectory(LOG_DIRECTORY, MATCH_STRING);
			
			List<Object> valueObj = new LinkedList<>();
            for(String grepValue : returnVal){
                HashMap<String, String> hmap = new HashMap<String, String>();
                    String[] grepVal = grepValue.split(",");
                    hmap.put("file",grepVal[0]);
                    hmap.put("status",grepVal[1]);
                    hmap.put("lastMod",grepVal[2]);
                    valueObj.add(hmap);
            }
		  jsonObject.put("fileDtls", valueObj);
		  jsonObject.put("timsMsg", "Record in protocol data temp table created successfully");		  
		  return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
	}
	
	/**
     * POST  /insertIntoProtocolDataTempTable
     */
	@RequestMapping(value="/insertIntoProtocolDataTempTable", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createpatientprotocolmonarch(){

		JSONObject jsonObject = new JSONObject();
		System.out.println("I am here in call of createpatientprotocolmonarch ");
		try{
				
				
			timsUserRepository.insertIntoProtocolDataTempTable("HR2015000002", "Normal", 2, "3", 5, 20, 10, 14, 1, 10, 1, "214");
			  jsonObject.put("timsMsg", "Record in protocol data temp table created successfully");
			  return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);			
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		
	}

	/**
     * POST  /createpatientprotocol
     */
	@RequestMapping(value="/createpatientprotocol", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createpatientprotocol(){

		JSONObject jsonObject = new JSONObject();
		System.out.println("I am here in call of createpatientprotocol ");
		
		try{
			  //timsService.createPatientProtocol("Normal","Insert","HR2017000606","App");
			  jsonObject.put("timsMsg", "createPatientProtocol stored procedure executed successfully");
			  return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);			
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		
	}
	
 //Start of my code	
	/**
     * POST  /managaPatientDevice
     */
	@RequestMapping(value="/managaPatientDevice", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> managaPatientDevice(){

		JSONObject jsonObject = new JSONObject();
		System.out.println("I am here in call of managaPatientDevice ");
		try{ 
			   //timsService.managePatientDevice("CREATE","HR2015000002","64-00132","64-00132",
				//	   "00:06:66:62:4B:06","QUALC00100017682");
			   
			   /* timsService.managaPatientDevice("INACTIVATE","HR2015000002","64-00132","64-00132",
					         "00:06:66:62:4B:06","QUALC00100017682");
				  timsService.managaPatientDevice("operationType","inPatientId","inPatientoldDeviceSerialNumber","inPatientNewDeviceSerialNumber",
			       "inPatientBluetoothId","inPatientHubId");
						  */
			  jsonObject.put("timsMsg", "managaPatientDevice stored procedure executed successfully");
			  return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);			
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		
	}
	
	/**
     * POST  /managaPatientDevice
     */
	@RequestMapping(value="/managaPatientDeviceAssociation", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> managaPatientDeviceAssociation(){
		
		DateTimeFormatter inpatientTrainingDate = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
		DateTime inpatientTrainingDate1= inpatientTrainingDate.parseDateTime("05/01/2017 11:22:11");
		
		JSONObject jsonObject = new JSONObject();
		System.out.println("I am here in call of managaPatientDeviceAssociation ");
		try{
			/* timsService.managaPatientDeviceAssociation("operationType","inpatientPatientId","inpatientDeviceType","inpatientDeviceIsActive",
														 "inPatientBluetoothId","inpatientHillromId","inpatientOldId",inpatientTrainingDate1,
														 "inpatientDiagnosisCode1","inpatientDiagnosisCode","inpatientDiagnosisCode3","inpatientDiagnosisCode4",
														 "inpatientGarmentType","inpatientGarmentSize","inpatientGarmentColor");
														 */
			  jsonObject.put("timsMsg", "managaPatientDeviceAssociation stored procedure executed successfully");
			  return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);			
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		
	}
	
	
	/**
     * POST  /managePatientUser
     */
	@RequestMapping(value="/managePatientUser", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> managePatientUser(){

		JSONObject jsonObject = new JSONObject();
		String outPatientId = "";
		String outUserId = "";
		try{	
			

			/*
			JSONObject jsonReturnObject = timsService.managePatientUser("CREATE",
					    					"99188",
					    					null,
					    					null,
					    					"RP991PP892", 
					    					"Jr",
					    					"Steve",
					    					"G",
					    					"Gumerman",
					    					"1943-12-01",
					    					"stevegg@gmail.com",
					    					"60148",
					    					"222-333-4444",
					    					"333-444-5555",
					    					"M",
					    					"En",
					    					"1300 First St",
					    					"Logan",
					    					"MA",
					    					"2015-12-03T23:12:12",
					    					null,
					    					null,
					    					null,
					    					null);*/
			  //jsonObject.put("returnValues",jsonReturnObject);
			  jsonObject.put("timsMsg", "managePatientUser stored procedure executed successfully");
			  
			  return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);			
		}catch(Exception ex){
			ex.printStackTrace();
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		
	}
	
	
		@RequestMapping(value="/managePatientDeviceMonarch", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<?> managePatientDeviceMonarch(){

			JSONObject jsonObject = new JSONObject();
			System.out.println("I am here in call of managePatientDeviceMonarch ");
			try{		  
				 // timsService.managePatientDeviceMonarch("CREATE",
				//		  									"HR2017000802",
				//		  									"RP003PP333",
				//		  									null);
				  jsonObject.put("timsMsg", "managePatientDeviceMonarch stored procedure executed successfully");
				  return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);			
			}catch(Exception ex){
				jsonObject.put("ERROR", ex.getMessage());
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		
		
		@RequestMapping(value = "/retrieveLogData/logs", method = RequestMethod.POST , produces = MediaType.TEXT_PLAIN_VALUE)
	    public ResponseEntity<?> retrieveLogData( @RequestBody String logfilePath ,HttpServletRequest request) {
			  
		      	String logFileContent = ""; 
		      	
		      	JSONObject jsonObject = new JSONObject();
		  		
		     try {
		   	  
		   	  try {
		   		  
		   		logFileContent = timsService.retrieveLogData(logfilePath);
					  return new ResponseEntity<>(logFileContent, HttpStatus.CREATED);
				} catch (HillromException e) {
					// TODO Auto-generated catch block
					
					return new ResponseEntity<>("ERROR"+e.getMessage() ,HttpStatus.BAD_REQUEST);
				}
		   	  	    	  
		   	  
		   	  	        
		     } catch (Exception ex) {
		   			    		
					return new ResponseEntity<>("ERROR"+ex.getMessage() ,HttpStatus.BAD_REQUEST);
		     }

		}
		
	//Use case specific implementation
			@RequestMapping(value="/patientExistsWithNoDevice", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
			public ResponseEntity<?> patientExistsWithNoDevice(){

				JSONObject jsonObject = new JSONObject();
				/*String serialNofromExcel= "abc";
				String hillromId="123";
				String currentHillRomID="XYZ";
				timsService.isSerialNoExistinPatientdeviceAssoc();
				timsService.istHillromIdExistinPatientInfo();
				timsService.isDeviceExistforCurrentHillRominPatDeviceAss();*/
				/*if(timsService.isSerialNoExistinPatientdeviceAssoc()==false && timsService.istHillromIdExistinPatientInfo()==true && timsService.isDeviceExistforCurrentHillRominPatDeviceAss()==false){
					timsService.comonProccallforPatientwithnoDeviceVestSwapandMonarcadd();
				}*/
				System.out.println("I am here in call of patientExistsWithNoDevice ");
				try{		  
					 /* timsService.managePatientDeviceMonarch("operationTypeIndicator","inPatientId","inPatientoldDeviceSerialNumber","inPatientNewDeviceSerialNumber");
					  jsonObject.put("timsMsg", "managaPatientUser stored procedure executed successfully");*/
					  return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);			
				}catch(Exception ex){
					jsonObject.put("ERROR", ex.getMessage());
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}	
			}
				@RequestMapping(value="/neitherPatientnorDeviceExist", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<?> neitherPatientnorDeviceExist(){

					JSONObject jsonObject = new JSONObject();
					/*String serialNofromExcel= "abc";
					String hillromId="123";
					String currentHillRomID="XYZ";
					timsService.isSerialNoExistinPatientdeviceAssoc();
					timsService.istHillromIdExistinPatientInfo();
					timsService.isDeviceExistforCurrentHillRominPatDeviceAss();*/
					/*if(timsService.isSerialNoExistinPatientdeviceAssoc()==false && timsService.isDeviceExistforCurrentHillRominPatDeviceAss()==false){
						timsService.comonProccallforPatientwithnoDeviceVestSwapandMonarcadd();
					}*/
					System.out.println("I am here in call of neitherPatientnorDeviceExist ");
					try{		  
						  
						/*timsService.managePatientDeviceMonarch("operationTypeIndicator","inPatientId","inPatientoldDeviceSerialNumber","inPatientNewDeviceSerialNumber");
						jsonObject.put("timsMsg", "managaPatientUser stored procedure executed successfully");  */
						return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);			
					}catch(Exception ex){
						jsonObject.put("ERROR", ex.getMessage());
						return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
					}
	}







	@RequestMapping(value="/executeTIMSJob", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> executeTIMSJob(){

		JSONObject jsonObject = new JSONObject();

		try{		  
			  timsInputReaderService.ExecuteTIMSJob();
			  jsonObject.put("timsMsg", "managaPatientUser stored procedure executed successfully");
			  return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);			
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
	}

}


