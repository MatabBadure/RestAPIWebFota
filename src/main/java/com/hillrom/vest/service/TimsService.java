package com.hillrom.vest.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.StoredProcedureQuery;

import net.minidev.json.JSONObject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.monarch.repository.PatientMonarchDeviceRepository;
import com.hillrom.monarch.service.PatientVestDeviceMonarchService;
import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.domain.PatientDevicesAssoc;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceHistory;
import com.hillrom.vest.domain.PatientVestDeviceHistoryMonarch;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AnnouncementsPermissionRepository;
import com.hillrom.vest.repository.AnnouncementsRepository;
import com.hillrom.vest.repository.PatientDevicesAssocRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientVestDeviceRepository;
import com.hillrom.vest.repository.TimsUserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.AnnouncementsDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.PatientInfoDTO;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Service
@Transactional
public class TimsService {

	private final Logger log = LoggerFactory.getLogger("com.hillrom.vest.tims");
	
	TimsInputReaderService tims = new TimsInputReaderService();
	
	
	
	@Inject
	private TimsUserRepository timsUserRepository;
	
	@Inject
	private PatientDevicesAssocRepository patientDevicesAssocRepository;

	@Inject
	private PatientInfoService patientInfoService;

	@Inject
	private PatientInfoRepository patientInfoRepository;

	@Inject
	private PatientVestDeviceRepository patientVestDeviceRepository;
	
	@Inject
	private PatientMonarchDeviceRepository patientMonarchDeviceRepository;
	
	@Inject
	private PatientVestDeviceService patientVestDeviceService;
	
	@Inject
	private PatientVestDeviceMonarchService patientVestDeviceMonarchService;
	
	@Inject
	public EntityManager entityManager;
	
	
	
	 DateTimeFormatter dobFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
    /* DateTimeFormatter deviceAssocdateFormat = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");*/
 
	
		/**
		 * 
		 * @param
		 * @return
		 * @throws HillromException
		 */
		public void insertIntoProtocolDataTempTable(String patient_id,
                									String type,
									                int treatments_per_day,
									                String treatment_label,
									                int min_minutes_per_treatment,
									                int max_minutes_per_treatment,
									                int min_frequency,
									                int max_frequency,
									                int min_pressure,
									                int max_pressure,
									                int to_be_inserted,
									                String user_id) throws SQLException, HillromException{	
			try{
					timsUserRepository.insertIntoProtocolDataTempTable(patient_id,
													type,
									                treatments_per_day,
									                treatment_label,
									                min_minutes_per_treatment,
									                max_minutes_per_treatment,
									                min_frequency,
									                max_frequency,
									                min_pressure,
									                max_pressure,
									                to_be_inserted,
									                user_id);
			}
			catch(SQLException se)
			{
				throw se;
			}
			catch(Exception ex){
				throw new HillromException("Error While invoking Stored Procedure " , ex);
			}
		}
	 
	 /**
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	public void createPatientProtocolMonarch(PatientInfoDTO patientInfoDTO) throws SQLException, HillromException{	
		try{
				timsUserRepository.createPatientProtocolMonarch(patientInfoDTO.getProtocol_type_key(),
						 patientInfoDTO.getOperation_type(),
						 patientInfoDTO.getPatient_id(),
						 patientInfoDTO.getCreated_by());
		}
		catch(javax.persistence.RollbackException jr){
		//	log.debug("RollbackException");
		}
		catch(org.springframework.transaction.TransactionSystemException tse){
		//	log.debug("TransactionSystemException");
		}    
		catch(PersistenceException pe){
		//	log.debug("PersistenceException");
		}
		catch(SQLException se)
		{
			throw se;
		}
		catch(Exception ex){
			throw new HillromException("Error While invoking Stored Procedure " , ex);
		}
	}
	
	
	
	/**
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	public void createPatientProtocol(PatientInfoDTO patientInfoDTO) throws SQLException, HillromException{	
		try{
			timsUserRepository.createPatientProtocol(patientInfoDTO.getProtocol_type_key(),
												 patientInfoDTO.getOperation_type(),
												 patientInfoDTO.getPatient_id(),
												 patientInfoDTO.getCreated_by());
		}
		catch(javax.persistence.RollbackException jr){
		//	log.debug("RollbackException");
		}
		catch(org.springframework.transaction.TransactionSystemException tse){
		//	log.debug("TransactionSystemException");
		}    
		catch(PersistenceException pe){
		//	log.debug("PersistenceException");
		}
		catch(SQLException se)
		{
			throw se;
		}
		catch(Exception ex){
			throw new HillromException("Error While invoking Stored Procedure " , ex);
		}
	}

  //Start of my code
	/**
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	public void managePatientDevice(PatientInfoDTO patientInfoDTO) throws SQLException ,HillromException{		
		
		try{
			timsUserRepository.managePatientDevice(patientInfoDTO.getOperation_type(), 
												patientInfoDTO.getPatient_id(), 
												patientInfoDTO.getOld_serial_number(), 
												patientInfoDTO.getNew_serial_number(),
												patientInfoDTO.getBluetooth_id(), 
												patientInfoDTO.getHub_id(),
												patientInfoDTO.getCreated_by());
		}catch(SQLException se)
		{
			throw se;
		}
		catch(Exception ex){
			throw new HillromException("Error While invoking Stored Procedure " , ex);
		}
	}


	/**
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	
	
	public void managePatientDeviceAssociation(PatientInfoDTO patientInfoDTO) throws SQLException ,HillromException{	
		
		try{
			timsUserRepository.managePatientDeviceAssociation(patientInfoDTO.getOperation_type(),
														  patientInfoDTO.getPatient_id(),
														  patientInfoDTO.getDevice_type(),
														  patientInfoDTO.getIs_active(),
														  patientInfoDTO.getSerial_num(),
														  patientInfoDTO.getHub_id(),
														  patientInfoDTO.getBluetooth_id(),
														  patientInfoDTO.getTims_cust(),
														  patientInfoDTO.getOld_patient_id(),
														  patientInfoDTO.getTrain_dt(),
														  patientInfoDTO.getDx1(),
														  patientInfoDTO.getDx2(),
														  patientInfoDTO.getDx3(),
														  patientInfoDTO.getDx4(),
														  patientInfoDTO.getGarment_type(),
														  patientInfoDTO.getGarment_size(),
														  patientInfoDTO.getGarment_color(),
														  patientInfoDTO.getCreated_by());
		}
		catch(SQLException se)
		{
			throw se;
		}
		catch(Exception ex){
			throw new HillromException("Error While invoking Stored Procedure " , ex);
		}
	}

	
public void managePatientDeviceAssociationMonarch(PatientInfoDTO patientInfoDTO) throws SQLException ,HillromException{	
		
		try{
			timsUserRepository.managePatientDeviceAssociationMonarch(patientInfoDTO.getOperation_type(),
														  patientInfoDTO.getPatient_id(),
														  patientInfoDTO.getDevice_type(),
														  patientInfoDTO.getIs_active(),
														  patientInfoDTO.getSerial_num(),
														  patientInfoDTO.getHub_id(),
														  patientInfoDTO.getBluetooth_id(),
														  patientInfoDTO.getTims_cust(),
														  patientInfoDTO.getOld_patient_id(),
														  patientInfoDTO.getTrain_dt(),
														  patientInfoDTO.getDx1(),
														  patientInfoDTO.getDx2(),
														  patientInfoDTO.getDx3(),
														  patientInfoDTO.getDx4(),
														  patientInfoDTO.getGarment_type(),
														  patientInfoDTO.getGarment_size(),
														  patientInfoDTO.getGarment_color(),
														  patientInfoDTO.getCreated_by());
		}
		catch(SQLException se)
		{
			throw se;
		}
		catch(Exception ex){
			throw new HillromException("Error While invoking Stored Procedure " , ex);
		}
	}
	
	
	/**This need to remove not required 
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	
	public JSONObject managePatientUser(PatientInfoDTO patientInfoDTO) throws SQLException, Exception,HillromException {	

		try{
		
		return timsUserRepository.managePatientUser(patientInfoDTO.getOperation_type(),
													patientInfoDTO.getDevice_type(),
													patientInfoDTO.getTims_cust(), 
													patientInfoDTO.getHub_id(), 
													patientInfoDTO.getBluetooth_id(), 
													patientInfoDTO.getSerial_num(), 
													patientInfoDTO.getTitle(),
													patientInfoDTO.getFirst_nm(), 
													patientInfoDTO.getMiddle_nm(), 
													patientInfoDTO.getLast_nm(), 
													patientInfoDTO.getDob(),
													patientInfoDTO.getEmail(), 
													patientInfoDTO.getZip_cd(), 
													patientInfoDTO.getPrimary_phone(), 
													patientInfoDTO.getMobile_phone(), 
													patientInfoDTO.getGender(), 
													patientInfoDTO.getLang_key(), 
													patientInfoDTO.getAddress(), 
													patientInfoDTO.getCity(), 
													patientInfoDTO.getState(), 
													patientInfoDTO.getCreated_by(),
													null, // Following fields no longer being used from this table 
													null, 
													null, 
													null, 
													null);
		}
		catch(SQLException se)
		{
			throw se;
		}
		catch(Exception ex){
			throw new HillromException("Error While invoking Stored Procedure " , ex);
		}
		
		
	}
	
	
	
	public void managePatientDeviceMonarch(PatientInfoDTO patientInfoDTO) throws SQLException ,HillromException{		
		
		try{
			
			
			
			timsUserRepository.managePatientDeviceMonarch(patientInfoDTO.getOperation_type(), 
				patientInfoDTO.getPatient_id(), 
				patientInfoDTO.getOld_serial_number(), 
				patientInfoDTO.getNew_serial_number(),
				patientInfoDTO.getBluetooth_id(),
				patientInfoDTO.getHub_id(),
				patientInfoDTO.getCreated_by());
		}
		catch(SQLException se)
		{
			throw se;
		}
		catch(Exception ex){
			throw new HillromException("Error While invoking Stored Procedure " , ex);
		}
	}
	
	
	
	public String retrieveLogData(String logfilePath)throws HillromException
    {
	   
	   String content = "";
	   String line = "";
	   BufferedReader reader = null;
	   
	  try{
		reader = new BufferedReader(new FileReader(logfilePath));
		line = reader.readLine();
	   	  content +=  line;
	   	  while ((line = reader.readLine()) != null)
	   	  {
	   	     content += "\n" + line;
	   	    	    	     
	   	  }
	   	
	  }
	  catch(IOException e)
	  {
		log.error("Exception Occured"+e.getMessage());
	  }
	  finally
	  {
		  if(reader!=null)
		  {
	    try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		  }
	  return content;
		
    }
	
	public List<String> listLogDirectory(String logfilePath, String matchStr) throws HillromException {
		
		File folder = new File(logfilePath);
		File[] listOfFiles = folder.listFiles();
		List<String> returnLogFiles = new LinkedList<>();
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	try {
			    	//log.debug(file.getName());
	                Runtime rt = Runtime.getRuntime();
	                String[] cmd = { "/bin/sh", "-c", "grep -c '"+matchStr+"' '"+logfilePath+file.getName()+"' " };
	                Process proc = rt.exec(cmd);
	                BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	                String line;
	                while ((line = is.readLine()) != null) {
	                    returnLogFiles.add(file.getName()+","+file+","+(Integer.parseInt(line)>0?"Success":"Failure")+","+file.lastModified());
	                }			    	
			    	
		    	}catch(Exception ex){
		    		ex.printStackTrace();
		    	}
		    }
		}
		
		return returnLogFiles;

    }
	

	
	
	public String getDeviceTypeFromRecord(Map fileRecords, int position){
		PatientInfoDTO patientInfoDTO = (PatientInfoDTO) fileRecords.get(position);
		return patientInfoDTO.getDevice_type();
	}
	
	public boolean isSerialNoExistInPatientdeviceAssocVest(String serialNumber){
		
		
		if(patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(serialNumber,"VEST").isPresent()){
			return true;
		}
			
		
		return false;
	}
	
	public boolean isSerialNoExistInPatientdeviceAssocMonarch(String serialNumber){
		
		if(patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(serialNumber,"MONARCH").isPresent()){
			return true;
		}
		
		return false;
	}
	
	public boolean isHillromIdExistInPatientInfo(String hillromId){
		
		if(patientInfoService.findOneByHillromId(hillromId).isPresent()){
				return true;
		}
		
		return false;
	}
	
	public boolean isHillromIdExistInPatientDeviceAssocVest(String hillromId){
		
		if(patientDevicesAssocRepository.findByHillromIdAndDeviceType(hillromId,"VEST").isPresent()){
				return true;
		}
		
		return false;
	}
	
	public boolean isHillromIdExistInPatientDeviceAssocMonarch(String hillromId){
		
		if(patientDevicesAssocRepository.findByHillromIdAndDeviceType(hillromId,"MONARCH").isPresent()){
				return true;
		}
		
		return false;
	}
	
/*	public boolean isHillromIdHasVestDeviceInPatientDeviceAssoc(String hillromId){
		
		if((patientDevicesAssocRepository.findByHillromId(hillromId).isPresent()) 
			&& (patientDevicesAssocRepository.findByHillromId(hillromId).get().getDeviceType().equalsIgnoreCase("VEST"))){
		
				return true;
		}
		
		return false;
	} */
	
public boolean isHillromIdHasVestDeviceInPatientDeviceAssoc(String hillromId){
		
		if(patientDevicesAssocRepository.findByHillromIdAndDeviceType(hillromId,"VEST").isPresent()){
			
				return true;
		}
		
		return false;
	}
	
	public boolean isHillromIdHasMonarchDeviceInPatientDeviceAssoc(String hillromId){
		
		if(patientDevicesAssocRepository.findByHillromIdAndDeviceType(hillromId,"MONARCH").isPresent()){
			
				return true;
		}
		
		return false;
	}
	

	public boolean isCurrentSerialNumberOwnedByShellVest(String serialNumber){
		
		if((patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(serialNumber,"VEST").isPresent()) ) {
			if((patientInfoRepository.findOneById(patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(serialNumber,"VEST").get().getPatientId()).getFirstName().equalsIgnoreCase("Hill-Rom")) ){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isCurrentSerialNumberOwnedByShellMonarch(String serialNumber){
		
		if((patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(serialNumber,"MONARCH").isPresent()) ) {
				String patientId = patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(serialNumber,"MONARCH").get().getPatientId();	
				if((patientInfoRepository.findOneById(patientId).getFirstName().equalsIgnoreCase("Monarch")) && (patientInfoRepository.findOneById(patientId).getLastName().equalsIgnoreCase("Hill-Rom")) ){
					return true;
				}
		}
		
		return false;
	}
	
	public boolean isCurrentSerialNumberOwnedByDifferentPatientVest(String serialNumber,String hillromId){
		String another_patient_hillrom_id = "";
		try{
			another_patient_hillrom_id = patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(serialNumber,"VEST").get().getHillromId();
		}catch(Exception ex){
			another_patient_hillrom_id = "";
		}
	/*	log.debug("hillromId " + hillromId);
		log.debug("another_patient_hillrom_id " + another_patient_hillrom_id);*/
	
		if((another_patient_hillrom_id!=null)&& (!another_patient_hillrom_id.equalsIgnoreCase(""))) {
       	 if(!another_patient_hillrom_id.equalsIgnoreCase(hillromId)){
                     return true;
       	 }
       		 
        }
        else{
       	 return true;
        }
		
		return false;
	}
	
	public boolean isCurrentSerialNumberOwnedByDifferentPatientMonarch(String serialNumber,String hillromId){
		String another_patient_hillrom_id = "";
		try{
			another_patient_hillrom_id = patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(serialNumber,"MONARCH").get().getHillromId();
		}catch(Exception ex){
			another_patient_hillrom_id = "";
		}
	/*	log.debug("hillromId " + hillromId);
		log.debug("another_patient_hillrom_id " + another_patient_hillrom_id);*/
	
		if((another_patient_hillrom_id!=null)&& (!another_patient_hillrom_id.equalsIgnoreCase(""))) {
       	 if(!another_patient_hillrom_id.equalsIgnoreCase(hillromId)){
                     return true;
       	 }
       		 
        }
        else{
       	 return true;
        }
		
		return false;
	}
	


	public boolean isCurrentSerialNumberOwnedByCurrentHillromIdVest(String serialNumber,String hillromId){
		
		if(patientDevicesAssocRepository.findOneBySerialNumberAndHillromIdAndDeviceType(serialNumber,hillromId,"VEST").isPresent()){
				return true;		
		}
		return false;
	}
	
	public boolean isCurrentSerialNumberOwnedByCurrentHillromIdMonarch(String serialNumber,String hillromId){
		
		if(patientDevicesAssocRepository.findOneBySerialNumberAndHillromIdAndDeviceType(serialNumber,hillromId,"MONARCH").isPresent()) {
			return true;	
		}

		return false;
	}

	public boolean isOwnerExistsForCurrentSerialNumberVest(String serialNumber){
		
		if(patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(serialNumber,"VEST").isPresent()) {
				return true;
		}
		
		return false;
	}
	
	public boolean isOwnerExistsForCurrentSerialNumberMonarch(String serialNumber){
		
		if(patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(serialNumber,"MONARCH").isPresent()){ 
				return true;
		}
		
		return false;
	}
	
	
	// All Cases start below <ScenarioName>Vest
	//JIRA-ID HILL-2407 
	public boolean CASE1_NeitherPatientNorDeviceExist_VEST(PatientInfoDTO patientInfoDTO){
		
		
		if((!isSerialNoExistInPatientdeviceAssocVest(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))){

			try{
				
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				JSONObject returnValues =  managePatientUser(patientInfoDTO);
				patientInfoDTO.setPatient_id(returnValues.get("return_patient_id").toString());
				patientInfoDTO.setPatient_user_id(returnValues.get("return_user_id").toString());
				
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				managePatientDevice(patientInfoDTO);
							
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
				tims.processed_atleast_one = true;
			
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Created       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+ se.getMessage());
				
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			
			catch(Exception ex){
				tims.processed_atleast_one = true;
				
				log.debug("Created       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+ "Error occured while creating new patient with new device");
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}				
			
			
			log.debug("Created       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "New patient created with new device");

			return true;
		}
		
				
		return false;
		
	}
	//JIRA-ID HILL-2521
	public boolean CASE2_PatientExistsWithNODevice_VEST(PatientInfoDTO patientInfoDTO){
		
		
		
		if((!isSerialNoExistInPatientdeviceAssocVest(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isHillromIdExistInPatientDeviceAssocVest(patientInfoDTO.getTims_cust())) ){
			
        
			try{
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				managePatientDevice(patientInfoDTO);
				
				//patientInfoDTO.setOperation_type("CREATE");
				
				patientInfoDTO.setOperation_type("UPDATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
		//		log.debug("Patient Id "+patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());

				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
						filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
				tims.processed_atleast_one = true;
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+ se.getMessage());
				
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+ "Error occured while assigning the patient with new device");
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}	
			
			log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "patient assigned with new device");
			
			return true;
		}
		
		return false;
		
		
	}
	//JIRA-ID HILL-2414
	public boolean CASE3_PatientHasMonarchAddVisivest_VEST(PatientInfoDTO patientInfoDTO){
		
		
		if((!isSerialNoExistInPatientdeviceAssocVest(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
			&& (patientDevicesAssocRepository.findByHillromId(patientInfoDTO.getTims_cust()).isPresent()) 
			&& (!isHillromIdHasVestDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
		 

			try{
				
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());	
				managePatientDevice(patientInfoDTO);
				
						
				patientInfoDTO.setOperation_type("CREATE");
				
				managePatientDeviceAssociationMonarch(patientInfoDTO);
								
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
						filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
				tims.processed_atleast_one = true;
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Made Combo    " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+se.getMessage());

				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				
				log.debug("Made Combo    " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+"Error occured while creating combo patient");
		
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}	
						log.debug("Made Combo    " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "Patient updated as combo(Vest device is added)");
	
			return true;
			
		}
		
		return false;
		
	}

	//JIRA-ID -- HILL-2412
	@Transactional
	public boolean CASE4_PatientHasDifferentVisivestSwap_VEST(PatientInfoDTO patientInfoDTO){
		
		if((!isSerialNoExistInPatientdeviceAssocVest(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (isHillromIdExistInPatientDeviceAssocVest(patientInfoDTO.getTims_cust())) && (isHillromIdHasVestDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){


			try{
				
				patientInfoDTO.setOperation_type("UPDATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				patientInfoDTO.setOld_serial_number(patientDevicesAssocRepository.findByHillromIdAndDeviceType(patientInfoDTO.getTims_cust(), "VEST").get().getSerialNumber());
				patientInfoDTO.setNew_serial_number(patientInfoDTO.getSerial_num());
				
				managePatientDevice(patientInfoDTO);
				entityManager.refresh(patientDevicesAssocRepository.findByHillromIdAndDeviceType(patientInfoDTO.getTims_cust(), "VEST").get());

							
				patientInfoDTO.setOperation_type("UPDATE");
				managePatientDeviceAssociation(patientInfoDTO);
								
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
						filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
				tims.processed_atleast_one = true;
				
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				
				log.debug("Swapped      " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+se.getMessage());
						se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				log.debug("Swapped      " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+ "Error occured while swapping Vest device");
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}	
			
			log.debug("Swapped      " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "Patient Vest device swapped with new Vest device");
		
			return true;
				
		}
		
		return false;		
	}
	
    // JIRA-ID HILL-2409
	public boolean CASE5_DeviceOwnedByShell_VEST(PatientInfoDTO patientInfoDTO){
		
		if((isSerialNoExistInPatientdeviceAssocVest(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& isCurrentSerialNumberOwnedByShellVest(patientInfoDTO.getSerial_num()) ){

			try{
				
				patientInfoDTO.setOperation_type("UPDATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				JSONObject returnValues = managePatientUser(patientInfoDTO);
				patientInfoDTO.setPatient_id(returnValues.get("return_patient_id").toString());
				patientInfoDTO.setPatient_user_id(returnValues.get("return_user_id").toString());
								
				patientInfoDTO.setOperation_type("UPDATE");
				managePatientDeviceAssociation(patientInfoDTO);
			
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
				tims.processed_atleast_one = true;
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+se.getMessage());
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+"Error occured while updating Vest patient");
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
						
			log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "Vest patient updated");
			
			return true;
		}
		
		return false;		
	}
	//JIRA-ID -- HILL-2522
	public boolean CASE6_DeviceOwnedByDifferentPatient_VEST(PatientInfoDTO patientInfoDTO){
		
		if((isSerialNoExistInPatientdeviceAssocVest(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByShellVest(patientInfoDTO.getSerial_num())) 
				&& (isCurrentSerialNumberOwnedByDifferentPatientVest(patientInfoDTO.getSerial_num(), patientInfoDTO.getTims_cust())) ){
		
			try{
				String patient_id_of_serial_number_to_inactivate = patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(patientInfoDTO.getSerial_num(),"VEST").get().getPatientId();
				patientInfoDTO.setPatient_id(patient_id_of_serial_number_to_inactivate);
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				patientInfoDTO.setNew_serial_number(null);
				patientInfoDTO.setOperation_type("INACTIVATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				managePatientDevice(patientInfoDTO);

				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				JSONObject returnValues =   managePatientUser(patientInfoDTO);
				patientInfoDTO.setPatient_id(returnValues.get("return_patient_id").toString());
				patientInfoDTO.setPatient_user_id(returnValues.get("return_user_id").toString());
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDevice(patientInfoDTO);
				
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
				tims.processed_atleast_one = true;
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+se.getMessage());
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+"Error occured while assigning  device to the patient  ");
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
						
			log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "Device assigned to patient");		
			
			return true;
		}
		
		return false;		
	}
	//JIRA-ID -- HILL-2526
	public boolean CASE7_DeviceIsOrphanPatientDoesNotExist_VEST(PatientInfoDTO patientInfoDTO){
		
		if((isSerialNoExistInPatientdeviceAssocVest(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByShellVest(patientInfoDTO.getSerial_num())) 
				&& (!isCurrentSerialNumberOwnedByDifferentPatientVest(patientInfoDTO.getSerial_num(),patientInfoDTO.getTims_cust() )) ){

			try{
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				JSONObject returnValues = managePatientUser(patientInfoDTO);
				patientInfoDTO.setPatient_id(returnValues.get("return_patient_id").toString());
				patientInfoDTO.setPatient_user_id(returnValues.get("return_user_id").toString());
				
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				managePatientDevice(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
				tims.processed_atleast_one = true;
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+se.getMessage());
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+"Error occured while assigning device to the new patient");
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
						
			log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "Device assigned to new patient");	
			
			return true;
		}
		
		return false;		
	}
	
	
	//JIRA-ID -- HILL-2527
	public boolean CASE8_DeviceIsOrphanButPatientExist_VEST(PatientInfoDTO patientInfoDTO){
		
		if((isSerialNoExistInPatientdeviceAssocVest(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByCurrentHillromIdVest(patientInfoDTO.getSerial_num(),patientInfoDTO.getTims_cust())) 
				&& (!isOwnerExistsForCurrentSerialNumberVest(patientInfoDTO.getSerial_num() )) ){

			try{
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				managePatientDevice(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
						filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
				tims.processed_atleast_one = true;
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+se.getMessage());
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+"Error occured while assigning device to the patient ");
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
						
			log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "Device assigned to the patient");	
			
			return true;
		}
		
		return false;		
	}
	
	

	
	public boolean DeviceOwnedByDifferentPatient_VEST(PatientInfoDTO patientInfoDTO){
		
		if((isSerialNoExistInPatientdeviceAssocVest(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByCurrentHillromIdVest(patientInfoDTO.getSerial_num(),patientInfoDTO.getTims_cust())) 
				&& (isOwnerExistsForCurrentSerialNumberVest(patientInfoDTO.getSerial_num() )) ){

			
			try{
				patientInfoDTO.setOperation_type("INACTIVATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				String patient_id_of_serial_number_to_inactivate = patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(patientInfoDTO.getSerial_num(),"VEST").get().getPatientId();
				patientInfoDTO.setPatient_id(patient_id_of_serial_number_to_inactivate);				
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				patientInfoDTO.setNew_serial_number(null);
				managePatientDevice(patientInfoDTO);
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}
			

			
			return true;
		}
		
		return false;		
	}


	//JIRA-ID -- HILL-2523
	public boolean CASE9_PatientHasDifferentVisivestSwap_VEST(PatientInfoDTO patientInfoDTO){
		
		
		if(DeviceOwnedByDifferentPatient_VEST(patientInfoDTO)){
			
			if( (isHillromIdExistInPatientDeviceAssocVest(patientInfoDTO.getTims_cust())) && (isHillromIdHasVestDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
	

				try{
					patientInfoDTO.setOperation_type("UPDATE");
					patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
					patientInfoDTO.setOld_serial_number(patientDevicesAssocRepository.findByHillromIdAndDeviceType(patientInfoDTO.getTims_cust(), "VEST").get().getSerialNumber());
					patientInfoDTO.setNew_serial_number(patientInfoDTO.getSerial_num());
					patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
					managePatientDevice(patientInfoDTO);
					String Shell_Patient_PatientID =		patientDevicesAssocRepository.findOneBySerialNumberAndDeviceTypeInactive(patientInfoDTO.getSerial_num(), "VEST").get().getPatientId();
					entityManager.refresh(patientDevicesAssocRepository.findByHillromIdAndDeviceType(patientInfoDTO.getTims_cust(), "VEST").get());
							
			        PatientDevicesAssoc patientDevicesAssoc =	patientDevicesAssocRepository.findByHillromIdAndDeviceType(patientInfoDTO.getTims_cust(), "VEST").get();
			        if(patientInfoService.findOneById(Shell_Patient_PatientID).getFirstName().equalsIgnoreCase("Hill-Rom")){
				        patientDevicesAssoc.setSwappedDate( LocalDate.now());
				        patientDevicesAssoc.setSwappedPatientId(Shell_Patient_PatientID);
				        patientDevicesAssocRepository.save(patientDevicesAssoc);
				        
				        if(patientVestDeviceRepository.findOneByPatientIdAndSerialNumber(patientInfoDTO.getPatient_id(),patientInfoDTO.getSerial_num()).isPresent()){
				        	Optional<PatientVestDeviceHistory> inactiveNonShellHistory =  patientVestDeviceRepository.findOneByPatientIdAndSerialNumberAndStatusInActive(patientInfoDTO.getPatient_id(),patientInfoDTO.getOld_serial_number());
				        	Optional<PatientVestDeviceHistory> shellHistory =  patientVestDeviceRepository.findOneByPatientIdAndSerialNumber(Shell_Patient_PatientID,patientInfoDTO.getSerial_num());
				        	Optional<PatientVestDeviceHistory> nonShellHistory =  patientVestDeviceRepository.findOneByPatientIdAndSerialNumber(patientInfoDTO.getPatient_id(),patientInfoDTO.getSerial_num());
				        	Double shellHmr = 0.0; Double nonshellHmr = 0.0;
				        	if(shellHistory.isPresent())
				        		if(Objects.nonNull(shellHistory.get().getHmr()))
				        			shellHmr = shellHistory.get().getHmr();
				        	if(inactiveNonShellHistory.isPresent())
				        		if(Objects.nonNull(inactiveNonShellHistory.get().getHmr()))
				        			nonshellHmr = inactiveNonShellHistory.get().getHmr();
				        	nonShellHistory.get().setHmr(nonshellHmr + shellHmr);
				        	patientVestDeviceRepository.save(nonShellHistory.get());
				        }
			        }
			        
					
					
					/* patientInfoDTO.setOperation_type("CREATE");
					managePatientDeviceAssociation(patientInfoDTO);*/
					
					patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
					patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
							filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
				//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
					insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
					patientInfoDTO.setOperation_type("Insert");
					createPatientProtocol(patientInfoDTO);
					tims.processed_atleast_one = true;
				}
				catch(SQLException se)
				{
					tims.processed_atleast_one = true;
					
					log.debug("Swapped      " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
							+se.getMessage());
							se.printStackTrace();
					tims.failureFlag = true;
					return false;
				}
				catch(Exception ex){
					tims.processed_atleast_one = true;
					log.debug("Swapped      " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
							+ "Error occured while swapping Vest device");
					
					ex.printStackTrace();
					tims.failureFlag = true;
					return false;
				}	
				
				log.debug("Swapped      " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
						+ "Patient Vest device swapped with existed Vest device");
				
				return true;
			}
			
			if(CASE10_PatientHasMonarchAddVisivest_VEST(patientInfoDTO)){
				return true;
			}
			else if(CASE11_PatientExistsWithNODevice_VEST(patientInfoDTO)){
				return true;
			}else{
				return false;
			}
			

		}
		
		return false;
		
		
	}
	//JIRA-ID -- HILL-2524
	public boolean CASE10_PatientHasMonarchAddVisivest_VEST(PatientInfoDTO patientInfoDTO){
		
		
		

			
			if( (patientDevicesAssocRepository.findByHillromId(patientInfoDTO.getTims_cust()).isPresent()) 
					&& (!isHillromIdHasVestDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
				

				
				try{
					
				/*	patientInfoDTO.setOperation_type("UPDATE");
					patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
					managePatientUser(patientInfoDTO);*/
					patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
					
					patientInfoDTO.setOperation_type("CREATE");
					patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
					patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
					managePatientDevice(patientInfoDTO);
									
					patientInfoDTO.setOperation_type("CREATE");
					managePatientDeviceAssociationMonarch(patientInfoDTO);
										
					patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
					patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
							filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
				//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
					insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
					patientInfoDTO.setOperation_type("Insert");
					createPatientProtocol(patientInfoDTO);
					tims.processed_atleast_one = true;
					
				}
				catch(SQLException se)
				{
					tims.processed_atleast_one = true;
									log.debug("Made Combo    " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
							+se.getMessage());
					
					se.printStackTrace();
					tims.failureFlag = true;
					return false;
				}
				catch(Exception ex){
					tims.processed_atleast_one = true;
					log.debug("Made Combo    " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
							+"Error occured while creating combo patient");
												
                    ex.printStackTrace();
                    tims.failureFlag = true;
					return false;
				}
				
				log.debug("Made Combo    " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
						+ "Patient updated as combo(Vest device is added)");	
				return true;
			}
			
			return false;
		

		
	}
	//JIRA-ID -- HILL-2525
	public boolean CASE11_PatientExistsWithNODevice_VEST(PatientInfoDTO patientInfoDTO){
		
		
		

			
			if (!isHillromIdExistInPatientDeviceAssocVest(patientInfoDTO.getTims_cust())) {
				

				try{
					patientInfoDTO.setOperation_type("CREATE");
					patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
					patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
					
				
					PatientInfo patientInfo = patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get();
					
					patientInfoDTO.setPatient_id(patientInfo.getId());
					/*patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
					managePatientDevice(patientInfoDTO);*/
					
					patientVestDeviceService.updateDeviceHistoryForTims(patientInfo, patientInfoDTO.getSerial_num(),
																			patientInfoDTO.getBluetooth_id(), patientInfoDTO.getHub_id());
					
					patientInfoDTO.setOperation_type("UPDATE");
					managePatientDeviceAssociation(patientInfoDTO);
					
					String Shell_Patient_PatientID =		patientDevicesAssocRepository.findOneBySerialNumberAndDeviceTypeInactive(patientInfoDTO.getSerial_num(), "VEST").get().getPatientId();
					entityManager.refresh(patientDevicesAssocRepository.findByHillromIdAndDeviceType(patientInfoDTO.getTims_cust(), "VEST").get());
					PatientDevicesAssoc patientDevicesAssoc =	patientDevicesAssocRepository.findByHillromIdAndDeviceType(patientInfoDTO.getTims_cust(), "VEST").get();
			        patientDevicesAssoc.setSwappedDate( LocalDate.now());
			        patientDevicesAssoc.setSwappedPatientId(Shell_Patient_PatientID);
			        patientDevicesAssocRepository.save(patientDevicesAssoc);
					
					patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
					patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
							filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
					insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
					patientInfoDTO.setOperation_type("Insert");
					createPatientProtocol(patientInfoDTO);
					tims.processed_atleast_one = true;
				}
				catch(SQLException se)
				{
					tims.processed_atleast_one = true;
					log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
							+ se.getMessage());
					
					se.printStackTrace();
					tims.failureFlag = true;
					return false;
				}
				catch(Exception ex){
					tims.processed_atleast_one = true;
					
					log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
							+ "Error occured while assigning the patient with new device");
					
					ex.printStackTrace();
					tims.failureFlag = true;
					return false;
				}	
				
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
						+ "patient assigned with new device");
				return true;
				
			}
			
			return false;
		

	}
	

	
	// All Cases start below  <ScenarioName>Monarch
	//JIRA-ID HILL-2407 
	public boolean CASE1_NeitherPatientNorDeviceExist_MONARCH(PatientInfoDTO patientInfoDTO){
		
		
		
		if((!isSerialNoExistInPatientdeviceAssocMonarch(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))){

			try{
				
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				JSONObject returnValues = managePatientUser(patientInfoDTO);
				patientInfoDTO.setPatient_id(returnValues.get("return_patient_id").toString());
				patientInfoDTO.setPatient_user_id(returnValues.get("return_user_id").toString());
							
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
										
				managePatientDeviceMonarch(patientInfoDTO);
						
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
		
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocolMonarch(patientInfoDTO);
				tims.processed_atleast_one = true;
		
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				
				log.debug("Created       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+ se.getMessage());
			
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
							log.debug("Created       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+"Error occured while creating new patient with new device");
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}	
			log.debug("Created       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "New patient created with new device");
		
			return true;
		}
		
		return false;
		
	}
	//JIRA-ID HILL-2521
	public boolean CASE2_PatientExistsWithNODevice_MONARCH(PatientInfoDTO patientInfoDTO){
		
		
		
		if((!isSerialNoExistInPatientdeviceAssocMonarch(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isHillromIdExistInPatientDeviceAssocMonarch(patientInfoDTO.getTims_cust())) ){
			

			try{
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				managePatientDeviceMonarch(patientInfoDTO);
						
			//	patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setOperation_type("UPDATE");
				managePatientDeviceAssociation(patientInfoDTO);
						
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
						filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocolMonarch(patientInfoDTO);
				tims.processed_atleast_one = true;
			}catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+ se.getMessage());
				
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+ "Error occured while assigning the patient with new device");
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}	
			
			log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "patient assigned with new device");
			
			
			return true;
		}
		
		return false;
		
		
	}
	
	//JIRA-ID -- HILL-2496
	public boolean CASE3_PatientHasVisivestAddMonarch_MONARCH(PatientInfoDTO patientInfoDTO){
		
		
		
		if((!isSerialNoExistInPatientdeviceAssocMonarch(patientInfoDTO.getSerial_num())) 
				&& (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&&(patientDevicesAssocRepository.findByHillromId(patientInfoDTO.getTims_cust()).isPresent())
				&& (!isHillromIdHasMonarchDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
			

			try{				
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
							
				managePatientDeviceMonarch(patientInfoDTO);
								
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
								
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
						filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id()+new Random().nextInt(6));
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocolMonarch(patientInfoDTO);
				tims.processed_atleast_one = true;
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Made Combo    " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+se.getMessage());
						
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				log.debug("Made Combo    " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+"Error occured while creating combo patient");
					
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}	
			
			log.debug("Made Combo    " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "Patient updated as combo(Monarch device is added)");
			
			return true;
			
		}
		
		return false;
		
	}
	//JIRA-ID -- HILL-2412
	public boolean CASE4_PatientHasDifferentMonarchSwap_MONARCH(PatientInfoDTO patientInfoDTO){
		
		
		
		if((!isSerialNoExistInPatientdeviceAssocMonarch(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (isHillromIdExistInPatientDeviceAssocMonarch(patientInfoDTO.getTims_cust())) && (isHillromIdHasMonarchDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
			

			try{				
				patientInfoDTO.setOperation_type("UPDATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				patientInfoDTO.setOld_serial_number(patientDevicesAssocRepository.findByHillromIdAndDeviceType(patientInfoDTO.getTims_cust(), "MONARCH").get().getSerialNumber());
				patientInfoDTO.setNew_serial_number(patientInfoDTO.getSerial_num());
				
				managePatientDeviceMonarch(patientInfoDTO);
				entityManager.refresh(patientDevicesAssocRepository.findByHillromIdAndDeviceType(patientInfoDTO.getTims_cust(), "MONARCH").get());

								
				patientInfoDTO.setOperation_type("UPDATE");
				managePatientDeviceAssociationMonarch(patientInfoDTO);
								
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
						filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocolMonarch(patientInfoDTO);
				tims.processed_atleast_one = true;
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Swapped      " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+se.getMessage());
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				log.debug("Swapped      " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+ "Error occured while swapping Monarch device");
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}		
			
			log.debug("Swapped      " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "Patient Monarch device swapped with new Monarch device");
			
			return true;
			
			
		}
		
		return false;		
	}
	
	// JIRA-ID HILL-2409
	public boolean CASE5_DeviceOwnedByShell_MONARCH(PatientInfoDTO patientInfoDTO){
		
		
		
		if((isSerialNoExistInPatientdeviceAssocMonarch(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& isCurrentSerialNumberOwnedByShellMonarch(patientInfoDTO.getSerial_num()) ){
			

			

			try{
				patientInfoDTO.setOperation_type("UPDATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				JSONObject returnValues = managePatientUser(patientInfoDTO);
				patientInfoDTO.setPatient_id(returnValues.get("return_patient_id").toString());
				patientInfoDTO.setPatient_user_id(returnValues.get("return_user_id").toString());
								
				patientInfoDTO.setOperation_type("UPDATE");
				managePatientDeviceAssociationMonarch(patientInfoDTO);
								
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocolMonarch(patientInfoDTO);
				tims.processed_atleast_one = true;
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+se.getMessage());
				
					se.printStackTrace();
					tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+"Error occured while updating Monarch patient");
				
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "Monarch patient updated");
									
			return true;
		}
		
		return false;		
	}
	//JIRA-ID -- HILL-2522
	public boolean CASE6_DeviceOwnedByDifferentPatient_MONARCH(PatientInfoDTO patientInfoDTO){
		
		
		
		if((isSerialNoExistInPatientdeviceAssocMonarch(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByShellMonarch(patientInfoDTO.getSerial_num())) 
				&& (isCurrentSerialNumberOwnedByDifferentPatientMonarch(patientInfoDTO.getSerial_num(),patientInfoDTO.getTims_cust() )) ){

			try{
				patientInfoDTO.setOperation_type("INACTIVATE");
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				patientInfoDTO.setNew_serial_number(null);
				String patientIdOfSrNumToInactive = patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(patientInfoDTO.getSerial_num(),"MONARCH").get().getPatientId();
				patientInfoDTO.setPatient_id(patientIdOfSrNumToInactive);
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				
				managePatientDeviceMonarch(patientInfoDTO);

				patientInfoDTO.setOperation_type("CREATE");
				JSONObject returnValues = managePatientUser(patientInfoDTO);
				patientInfoDTO.setPatient_id(returnValues.get("return_patient_id").toString());
				patientInfoDTO.setPatient_user_id(returnValues.get("return_user_id").toString());
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
             	patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceMonarch(patientInfoDTO);   //if it's required please uncomment
				
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocolMonarch(patientInfoDTO);
				tims.processed_atleast_one = true;
			}
			catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+se.getMessage());
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+"Error occured while assigning  device to the patient  ");
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
						
			log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "Device assigned to patient");		
					
			
			return true;
		}
		
		return false;		
	}
	//JIRA-ID -- HILL-2526
	public boolean CASE7_DeviceIsOrphanPatientDoesNotExist_MONARCH(PatientInfoDTO patientInfoDTO){
		
		
		
		if((isSerialNoExistInPatientdeviceAssocMonarch(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByShellMonarch(patientInfoDTO.getSerial_num())) 
				&& (!isCurrentSerialNumberOwnedByDifferentPatientMonarch(patientInfoDTO.getSerial_num(),patientInfoDTO.getTims_cust() )) ){


			try{
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				JSONObject returnValues = managePatientUser(patientInfoDTO);
				patientInfoDTO.setPatient_id(returnValues.get("return_patient_id").toString());
				patientInfoDTO.setPatient_user_id(returnValues.get("return_user_id").toString());
				
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				
				managePatientDeviceMonarch(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocolMonarch(patientInfoDTO);
				tims.processed_atleast_one = true;
			}catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+se.getMessage());
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+"Error occured while assigning device to the new patient");
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
						
			log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "Device assigned to new patient");	
			
			return true;
		}
		
		return false;		
	}
	
	
	//JIRA-ID -- HILL-2527
	public boolean CASE8_DeviceIsOrphanButPatientExist_MONARCH(PatientInfoDTO patientInfoDTO){
		
		
		
		if((isSerialNoExistInPatientdeviceAssocMonarch(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByCurrentHillromIdMonarch(patientInfoDTO.getSerial_num(),patientInfoDTO.getTims_cust())) 
				&& (!isOwnerExistsForCurrentSerialNumberMonarch(patientInfoDTO.getSerial_num() )) ){


			try{
				patientInfoDTO.setOperation_type("CREATE");
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				
				managePatientDeviceMonarch(patientInfoDTO);
			
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
				patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
				patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
						filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
			//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
				insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocolMonarch(patientInfoDTO);
				tims.processed_atleast_one = true;
			}catch(SQLException se)
			{
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+se.getMessage());
				se.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
			catch(Exception ex){
				tims.processed_atleast_one = true;
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
						+"Error occured while assigning device to the patient ");
				
				ex.printStackTrace();
				tims.failureFlag = true;
				return false;
			}
						
			log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
					+ "Device assigned to the patient");	
			
			return true;
		}
		
		return false;		
	}
	
	

	
	public boolean DeviceOwnedByDifferentPatient_MONARCH(PatientInfoDTO patientInfoDTO){
		
		
		
		if((isSerialNoExistInPatientdeviceAssocMonarch(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByCurrentHillromIdMonarch(patientInfoDTO.getSerial_num(),patientInfoDTO.getTims_cust())) 
				&& (isOwnerExistsForCurrentSerialNumberMonarch(patientInfoDTO.getSerial_num() )) ){
				
			try{
				patientInfoDTO.setOperation_type("INACTIVATE");
				patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
				patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
				String patient_id_of_serial_number_to_inactivate = patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(patientInfoDTO.getSerial_num(),"MONARCH").get().getPatientId();
				patientInfoDTO.setPatient_id(patient_id_of_serial_number_to_inactivate);
				patientInfoDTO.setNew_serial_number(null);
				managePatientDeviceMonarch(patientInfoDTO);
				tims.processed_atleast_one = true;
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}
				
						
			return true;
		}
		
		return false;		
	}


	//JIRA-ID -- HILL-2519
	public boolean CASE9_PatientHasDifferentMonarchSwap_MONARCH(PatientInfoDTO patientInfoDTO){
		
		
		
		if(DeviceOwnedByDifferentPatient_MONARCH(patientInfoDTO)){
		
			if( (patientDevicesAssocRepository.findByHillromId(patientInfoDTO.getTims_cust()).isPresent())
					
			&& (isHillromIdHasMonarchDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
				

				

				try{
					patientInfoDTO.setOperation_type("UPDATE");
					patientInfoDTO.setOld_serial_number(patientDevicesAssocRepository.findByHillromIdAndDeviceType(patientInfoDTO.getTims_cust(), "MONARCH").get().getSerialNumber());
					patientInfoDTO.setNew_serial_number(patientInfoDTO.getSerial_num());
					patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
					patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
								
					managePatientDeviceMonarch(patientInfoDTO);
					String Shell_Patient_PatientID =		patientDevicesAssocRepository.findOneBySerialNumberAndDeviceTypeInactive(patientInfoDTO.getSerial_num(), "MONARCH").get().getPatientId();
					entityManager.refresh(patientDevicesAssocRepository.findByHillromIdAndDeviceType(patientInfoDTO.getTims_cust(), "MONARCH").get());
							
			        PatientDevicesAssoc patientDevicesAssoc =	patientDevicesAssocRepository.findByHillromIdAndDeviceType(patientInfoDTO.getTims_cust(), "MONARCH").get();
			        if(patientInfoService.findOneById(Shell_Patient_PatientID).getFirstName().equalsIgnoreCase("Monarch") && 
			        		patientInfoService.findOneById(Shell_Patient_PatientID).getLastName().equalsIgnoreCase("Hill-Rom")){
				        patientDevicesAssoc.setSwappedDate( LocalDate.now());
				        patientDevicesAssoc.setSwappedPatientId(Shell_Patient_PatientID);
				        patientDevicesAssocRepository.save(patientDevicesAssoc);
				        
				        if(patientMonarchDeviceRepository.findOneByPatientIdAndSerialNumber(patientInfoDTO.getPatient_id(),patientInfoDTO.getSerial_num()).isPresent()){
				        	Optional<PatientVestDeviceHistoryMonarch> inactiveNonShellHistory =  patientMonarchDeviceRepository.findOneByPatientIdAndSerialNumberAndStatusInActive(patientInfoDTO.getPatient_id(),patientInfoDTO.getOld_serial_number());
				        	Optional<PatientVestDeviceHistoryMonarch> nonShellHistory =  patientMonarchDeviceRepository.findOneByPatientIdAndSerialNumber(patientInfoDTO.getPatient_id(),patientInfoDTO.getSerial_num());
				        	Optional<PatientVestDeviceHistoryMonarch> shellHistory =  patientMonarchDeviceRepository.findOneByPatientIdAndSerialNumber(Shell_Patient_PatientID,patientInfoDTO.getSerial_num());
				        	Double shellHmr = 0.0; Double nonshellHmr = 0.0;
				        	if(shellHistory.isPresent())
				        		if(Objects.nonNull(shellHistory.get().getHmr()))
				        			shellHmr = shellHistory.get().getHmr();
				        	if(inactiveNonShellHistory.isPresent())
				        		if(Objects.nonNull(inactiveNonShellHistory.get().getHmr()))
				        			nonshellHmr = inactiveNonShellHistory.get().getHmr();
				        	nonShellHistory.get().setHmr(nonshellHmr + shellHmr);
				        	patientMonarchDeviceRepository.save(nonShellHistory.get());
				        }
			        }
			        
					
					/*patientInfoDTO.setOperation_type("CREATE");
					managePatientDeviceAssociation(patientInfoDTO);*/
					
					patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
					patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
							filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
				//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
					insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
					patientInfoDTO.setOperation_type("Insert");
					createPatientProtocolMonarch(patientInfoDTO);
					tims.processed_atleast_one = true;
				}catch(SQLException se)
				{
					tims.processed_atleast_one = true;
					
					log.debug("Swapped      " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
							+se.getMessage());
							se.printStackTrace();
					tims.failureFlag = true;
					return false;
				}
				catch(Exception ex){
					tims.processed_atleast_one = true;
					log.debug("Swapped      " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
							+ "Error occured while swapping Monarch device");
					
					ex.printStackTrace();
					tims.failureFlag = true;
					return false;
				}	
				
				log.debug("Swapped      " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
						+ "Patient Monarch device swapped with Existed Monarch device");
				
				return true;
			}
			
			if(CASE10_PatientHasVisivestAddMonarch_MONARCH(patientInfoDTO)){
				return true;
			}
			else if(CASE11_PatientExistsWithNODevice_MONARCH(patientInfoDTO)){
				return true;
			}else{
				return false;
			}
			
					
		}
		
		return false;
		
		
	}
	//JIRA-ID -- HILL-2520
	public boolean CASE10_PatientHasVisivestAddMonarch_MONARCH(PatientInfoDTO patientInfoDTO){
		
		
		

		if( (patientDevicesAssocRepository.findByHillromId(patientInfoDTO.getTims_cust()).isPresent()) 
				
			/*	if( (isHillromIdExistInPatientDeviceAssocMonarch(patientInfoDTO.getTims_cust())) */&& (!isHillromIdHasMonarchDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
				

				
				try{
				/*	patientInfoDTO.setOperation_type("UPDATE");
					patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
					managePatientUser(patientInfoDTO);*/
					patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
											 
					patientInfoDTO.setOperation_type("CREATE");
					patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
					patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
						
					
					managePatientDeviceMonarch(patientInfoDTO);
					
					patientInfoDTO.setOperation_type("CREATE");
					managePatientDeviceAssociation(patientInfoDTO);
					
					patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
					patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
							filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
				//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
					insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
					patientInfoDTO.setOperation_type("Insert");
					createPatientProtocolMonarch(patientInfoDTO);
					tims.processed_atleast_one = true;
				}
				catch(SQLException se)
				{
					tims.processed_atleast_one = true;
					log.debug("Made Combo    " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
							+se.getMessage());
		
					se.printStackTrace();
					tims.failureFlag = true;
					return false;
				}
				catch(Exception ex){
					tims.processed_atleast_one = true;
					log.debug("Made Combo    " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
							+"Error occured while creating combo patient");
			
					
					ex.printStackTrace();
					tims.failureFlag = true;
					return false;
				}
			
				log.debug("Made Combo    " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
						+ "Patient updated as combo(Monarch device is added)");
		
				return true;
			}
			
			return false;
		

		
	}
	//JIRA-ID -- HILL-2525
	public boolean CASE11_PatientExistsWithNODevice_MONARCH(PatientInfoDTO patientInfoDTO){
		
		
		

		
			if (!isHillromIdExistInPatientDeviceAssocMonarch(patientInfoDTO.getTims_cust())) {
				

				try{
					patientInfoDTO.setOperation_type("CREATE");
					patientInfoDTO.setOld_serial_number(patientInfoDTO.getSerial_num());
					patientInfoDTO.setCreated_by(Constants.CREATED_BY_TIMS);
					
					
					PatientInfo patientInfo = patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get();					
					patientInfoDTO.setPatient_id(patientInfo.getId());					
					
					/*patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());					
					managePatientDeviceMonarch(patientInfoDTO);*/
					
					patientVestDeviceMonarchService.updateDeviceHistoryForTims(patientInfo, patientInfoDTO.getSerial_num(),
							patientInfoDTO.getBluetooth_id(), patientInfoDTO.getHub_id());
					
				
					patientInfoDTO.setOperation_type("UPDATE");
					managePatientDeviceAssociationMonarch(patientInfoDTO);
					
					String Shell_Patient_PatientID =		patientDevicesAssocRepository.findOneBySerialNumberAndDeviceTypeInactive(patientInfoDTO.getSerial_num(), "MONARCH").get().getPatientId();
					entityManager.refresh(patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(patientInfoDTO.getSerial_num(), "MONARCH").get());
							
			        PatientDevicesAssoc patientDevicesAssoc =	patientDevicesAssocRepository.findOneBySerialNumberAndDeviceType(patientInfoDTO.getSerial_num(), "MONARCH").get();
			        patientDevicesAssoc.setSwappedDate( LocalDate.now());
			        patientDevicesAssoc.setSwappedPatientId(Shell_Patient_PatientID);
			        patientDevicesAssocRepository.save(patientDevicesAssoc);
					
					
						
					
					
					
					
					patientInfoDTO.setPatient_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getId());
					patientInfoDTO.setPatient_user_id(patientInfoService.findOneByHillromId(patientInfoDTO.getTims_cust()).get().getUserPatientAssoc().stream().
							filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser().getId().toString());
				//	insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,10,14,1,10,1,patientInfoDTO.getPatient_user_id());
					insertIntoProtocolDataTempTable(patientInfoDTO.getPatient_id(),"Normal",2,null,5,20,5,20,1,10,1,patientInfoDTO.getPatient_user_id());
					patientInfoDTO.setOperation_type("Insert");
					createPatientProtocolMonarch(patientInfoDTO);
					tims.processed_atleast_one = true;
				}
				catch(SQLException se)
				{
					tims.processed_atleast_one = true;
					log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
							+ se.getMessage());
					
					se.printStackTrace();
					tims.failureFlag = true;
					return false;
				}
				catch(Exception ex){
					tims.processed_atleast_one = true;
					
					log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Failure"+ "        "
							+ "Error occured while assigning the patient with new device");
					
					ex.printStackTrace();
					tims.failureFlag = true;
					return false;
				}	
				
				log.debug("Updated       " +patientInfoDTO.getTims_cust()+ "        " +patientInfoDTO.getSerial_num()+ "        "+"Success"+ "        "
						+ "patient assigned with new device");
				
				return true;
			}
			
			return false;
		

	}
	

	
	
	}






