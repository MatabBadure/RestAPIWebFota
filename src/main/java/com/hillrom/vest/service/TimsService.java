package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
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

import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AnnouncementsPermissionRepository;
import com.hillrom.vest.repository.AnnouncementsRepository;
import com.hillrom.vest.repository.PatientDevicesAssocRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientVestDeviceRepository;
import com.hillrom.vest.repository.TimsRepository;
import com.hillrom.vest.repository.TimsUserRepository;
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
	
	@Inject
	private TimsRepository timsRepository;
	
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
	
	

	 DateTimeFormatter dobFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
    /* DateTimeFormatter deviceAssocdateFormat = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");*/
 
	/**
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	public void createPatientProtocolMonarch(String typeKey,String operationType,String inPatientId,String inCreatedBy) throws HillromException{		
		timsRepository.createPatientProtocolMonarch(typeKey,operationType,inPatientId,inCreatedBy);
	}
	
	/**
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	public void createPatientProtocol(PatientInfoDTO patientInfoDTO) throws HillromException{		
			timsRepository.createPatientProtocol(patientInfoDTO.getProtocol_type_key(),
												 patientInfoDTO.getOperation_type(),
												 patientInfoDTO.getPatient_id(),
												 patientInfoDTO.getCreated_by());
	}

  //Start of my code
	/**
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	public void managePatientDevice(PatientInfoDTO patientInfoDTO) throws HillromException{		
		
		timsRepository.managePatientDevice(patientInfoDTO.getOperation_type(), 
											patientInfoDTO.getPatient_id(), 
											patientInfoDTO.getOld_serial_number(), 
											patientInfoDTO.getNew_serial_number(),
											patientInfoDTO.getBluetooth_id(), 
											patientInfoDTO.getHub_id());
	}


	/**
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	
	
	public void managePatientDeviceAssociation(PatientInfoDTO patientInfoDTO) throws HillromException{	
		
		timsRepository.managePatientDeviceAssociation(patientInfoDTO.getOperation_type(),
													  patientInfoDTO.getPatient_id(),
													  patientInfoDTO.getDevice_type(),
													  patientInfoDTO.getIs_active(),
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
													  patientInfoDTO.getGarment_color());
	}

	
	/**This need to remove not required 
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	
	public JSONObject managePatientUser(PatientInfoDTO patientInfoDTO) throws HillromException {	

		// Question : Bluetooth Id and garment code,title in xls ?
		
		return timsUserRepository.managePatientUser(patientInfoDTO.getOperation_type(), 
													patientInfoDTO.getTims_cust(), 
													patientInfoDTO.getHub_id(), 
													patientInfoDTO.getBluetooth_id(), 
													patientInfoDTO.getSerial_num(), 
													patientInfoDTO.getTitle(),
													patientInfoDTO.getFirst_nm(), 
													patientInfoDTO.getMiddle_nm(), 
													patientInfoDTO.getLast_nm(), 
													patientInfoDTO.getDob().toString(dobFormat), 
													patientInfoDTO.getEmail(), 
													patientInfoDTO.getZip_cd(), 
													patientInfoDTO.getPrimary_phone(), 
													patientInfoDTO.getMobile_phone(), 
													patientInfoDTO.getGender(), 
													patientInfoDTO.getLang_key(), 
													patientInfoDTO.getAddress(), 
													patientInfoDTO.getCity(), 
													patientInfoDTO.getState(), 
													null, // Following fields no longer being used from this table 
													null, 
													null, 
													null, 
													null);
		
		
	}
	
	
	
	public void managePatientDeviceMonarch(String operationTypeIndicator,String inPatientId,String inPatientoldDeviceSerialNumber,String inPatientNewDeviceSerialNumber) throws HillromException{		
		
		timsRepository.managePatientDeviceMonarch(operationTypeIndicator, 
												  inPatientId, 
												  Objects.isNull(inPatientoldDeviceSerialNumber)?"":inPatientoldDeviceSerialNumber, 
												  Objects.isNull(inPatientNewDeviceSerialNumber)?"":inPatientNewDeviceSerialNumber);
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
	

	
	
	public String getDeviceTypeFromRecord(Map fileRecords, int position){
		PatientInfoDTO patientInfoDTO = (PatientInfoDTO) fileRecords.get(position);
		return patientInfoDTO.getDevice_type();
	}
	
	public boolean isSerialNoExistInPatientdeviceAssoc(String serialNumber){
		
		if(Objects.nonNull(patientDevicesAssocRepository.findOneBySerialNumber(serialNumber)))
			return true;
		
		return false;
	}
	
	public boolean isHillromIdExistInPatientInfo(String hillromId){
		
		if(Objects.nonNull(patientInfoService.findOneByHillromId(hillromId)))
				return true;
		
		return false;
	}
	
	public boolean isHillromIdExistInPatientDeviceAssoc(String hillromId){
		
		if(Objects.nonNull(patientDevicesAssocRepository.findByHillromId(hillromId)))
				return true;
		
		return false;
	}
	
	public boolean isHillromIdHasVestDeviceInPatientDeviceAssoc(String hillromId){
		
		if((Objects.nonNull(patientDevicesAssocRepository.findByHillromId(hillromId))) 
			&& (patientDevicesAssocRepository.findByHillromId(hillromId).get().getDeviceType().equalsIgnoreCase("VEST")))
				return true;
		
		return false;
	}
	

	public boolean isCurrentSerialNumberOwnedByShell(String serialNumber){
		
		if((Objects.nonNull(patientInfoRepository.findOneBySerialNumber(serialNumber))) && (patientInfoRepository.findOneBySerialNumber(serialNumber).get().getFirstName().equalsIgnoreCase("Hill-Rom"))) 
				return true;
		
		return false;
	}
	
	public boolean isCurrentSerialNumberOwnedByDifferentPatient(String serialNumber){
		
		if((Objects.nonNull(patientVestDeviceRepository.findBySerialNumber(serialNumber))) && 
				(!patientDevicesAssocRepository.findOneBySerialNumber(serialNumber).get().getPatientId().equalsIgnoreCase
						(patientVestDeviceRepository.findOneBySerialNumberAndStatusActive(serialNumber).get(0).getPatient().getId()))) 
				return true;
		
		return false;
	}

	public boolean isCurrentSerialNumberOwnedByCurrentHillromId(String serialNumber){
		
		if((Objects.nonNull(patientVestDeviceRepository.findBySerialNumber(serialNumber))) && 
				(patientDevicesAssocRepository.findOneBySerialNumber(serialNumber).get().getHillromId().equalsIgnoreCase
						(patientVestDeviceRepository.findOneBySerialNumberAndStatusActive(serialNumber).get(0).getPatient().getHillromId()))) 
				return true;
		
		return false;
	}

	public boolean isOwnerExistsForCurrentSerialNumber(String serialNumber){
		
		if(Objects.nonNull(patientVestDeviceRepository.findOneBySerialNumberAndStatusActive(serialNumber))) 
				return true;
		
		return false;
	}
	
	
	// All Cases start below <ScenarioName>Vest or <ScenarioName>Monarch
	
	public boolean CASE1_NeitherPatientNorDeviceExist_VEST(PatientInfoDTO patientInfoDTO){
		
		if((!isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))){
		
			//managePatientUser(CREATE)
			//managaPatientDevice(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			try{
				patientInfoDTO.setOperation_type("CREATE");
				managePatientUser(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDevice(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
				patientInfoDTO.setProtocol_type_key("Normal");
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}				
			
			return true;
		}
		
		return false;
		
	}
	
	public boolean CASE2_PatientExistsWithNODevice_VEST(PatientInfoDTO patientInfoDTO){
		
		if((!isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isHillromIdExistInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
			

			//managaPatientDevice(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			try{
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDevice(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
				patientInfoDTO.setProtocol_type_key("Normal");
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}	
			
			return true;
		}
		
		return false;
		
		
	}
	
	public boolean CASE3_PatientHasMonarchAddVisivest_VEST(PatientInfoDTO patientInfoDTO){
		
		if((!isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (isHillromIdExistInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) && (!isHillromIdHasVestDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
			

			//managaPatientDevice(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			try{
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDevice(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
				patientInfoDTO.setProtocol_type_key("Normal");
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}	
			
			return true;
			
		}
		
		return false;
		
	}
	
	public boolean CASE4_PatientHasDifferentVisivestSwap_VEST(PatientInfoDTO patientInfoDTO){
		if((!isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (isHillromIdExistInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) && (isHillromIdHasVestDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
			

			//managaPatientDevice(UPDATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			try{
				patientInfoDTO.setOperation_type("UPDATE");
				managePatientDevice(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
				patientInfoDTO.setProtocol_type_key("Normal");
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}		
			
			return true;
			
			
		}
		
		return false;		
	}
	

	public boolean CASE5_DeviceOwnedByShell_VEST(PatientInfoDTO patientInfoDTO){
		if((isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& isCurrentSerialNumberOwnedByShell(patientInfoDTO.getSerial_num()) ){
			

			//managePatientUser(UPDATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			

			try{
				patientInfoDTO.setOperation_type("UPDATE");
				managePatientUser(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
				patientInfoDTO.setProtocol_type_key("Normal");
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}
			
			return true;
		}
		
		return false;		
	}
	
	public boolean CASE6_DeviceOwnedByDifferentPatient_VEST(PatientInfoDTO patientInfoDTO){
		if((isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByShell(patientInfoDTO.getSerial_num())) 
				&& (isCurrentSerialNumberOwnedByDifferentPatient(patientInfoDTO.getSerial_num() )) ){
			
			//managaPatientDevice(INACTIVATE)
			//managePatientUser(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			try{
				patientInfoDTO.setOperation_type("INACTIVATE");
				managePatientDevice(patientInfoDTO);

				patientInfoDTO.setOperation_type("CREATE");
				managePatientUser(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
				patientInfoDTO.setProtocol_type_key("Normal");
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}			
			
			return true;
		}
		
		return false;		
	}
	
	public boolean CASE7_DeviceIsOrphanPatientDoesNotExist_VEST(PatientInfoDTO patientInfoDTO){
		if((isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByShell(patientInfoDTO.getSerial_num())) 
				&& (!isCurrentSerialNumberOwnedByDifferentPatient(patientInfoDTO.getSerial_num() )) ){

			
			//managePatientUser(CREATE)
			//managaPatientDevice(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			try{
				patientInfoDTO.setOperation_type("CREATE");
				managePatientUser(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDevice(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
				patientInfoDTO.setProtocol_type_key("Normal");
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}	
			
			return true;
		}
		
		return false;		
	}
	
	

	public boolean CASE8_DeviceIsOrphanButPatientExist_VEST(PatientInfoDTO patientInfoDTO){
		if((isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByCurrentHillromId(patientInfoDTO.getSerial_num())) 
				&& (isOwnerExistsForCurrentSerialNumber(patientInfoDTO.getSerial_num() )) ){

			
			//managaPatientDevice(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			try{
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDevice(patientInfoDTO);
				
				patientInfoDTO.setOperation_type("CREATE");
				managePatientDeviceAssociation(patientInfoDTO);
				
				patientInfoDTO.setProtocol_type_key("Normal");
				patientInfoDTO.setOperation_type("Insert");
				createPatientProtocol(patientInfoDTO);
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}
			
			return true;
		}
		
		return false;		
	}
	
	

	
	public boolean DeviceOwnedByDifferentPatient_VEST(PatientInfoDTO patientInfoDTO){
		if((isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByCurrentHillromId(patientInfoDTO.getSerial_num())) 
				&& (!isOwnerExistsForCurrentSerialNumber(patientInfoDTO.getSerial_num() )) ){

			//managePatientDevice(INACTIVATE)
			
			try{
				patientInfoDTO.setOperation_type("INACTIVATE");
				managePatientDevice(patientInfoDTO);
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}
			

			
			return true;
		}
		
		return false;		
	}


	
	public boolean CASE9_PatientHasDifferentVisivestSwap_VEST(PatientInfoDTO patientInfoDTO){
		
		if(DeviceOwnedByDifferentPatient_VEST(patientInfoDTO)){
		
			if( (isHillromIdExistInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) && (isHillromIdHasVestDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
				
	
				//managePatientDevice(UPDATE)
				//managePatientDeviceAssociation(CREATE)
				//createPatientProtocol()
				
				//Question : How do you know whether to pass a Normal or Custom protocol key ?
				try{
					patientInfoDTO.setOperation_type("UPDATE");
					managePatientDevice(patientInfoDTO);
					
					patientInfoDTO.setOperation_type("CREATE");
					managePatientDeviceAssociation(patientInfoDTO);
					
					patientInfoDTO.setProtocol_type_key("Normal");
					patientInfoDTO.setOperation_type("Insert");
					createPatientProtocol(patientInfoDTO);
				}catch(Exception ex){
					ex.printStackTrace();
					return false;
				}
				
				return true;
			}
			
			return false;
		}
		
		return false;
		
		
	}
	
	public boolean CASE10_PatientHasMonarchAddVisivest_VEST(PatientInfoDTO patientInfoDTO){
		
		if(DeviceOwnedByDifferentPatient_VEST(patientInfoDTO)){
		
			if( (isHillromIdExistInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) && (!isHillromIdHasVestDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
				
	
				//managePatientDevice(CREATE)
				//managePatientDeviceAssociation(CREATE)
				//createPatientProtocol()
				
				try{
					patientInfoDTO.setOperation_type("CREATE");
					managePatientDevice(patientInfoDTO);
					
					patientInfoDTO.setOperation_type("CREATE");
					managePatientDeviceAssociation(patientInfoDTO);
					
					patientInfoDTO.setProtocol_type_key("Normal");
					patientInfoDTO.setOperation_type("Insert");
					createPatientProtocol(patientInfoDTO);
				}catch(Exception ex){
					ex.printStackTrace();
					return false;
				}
				
				return true;
			}
			
			return false;
		
		}
		
		return false;
		
	}
	
	public boolean CASE11_PatientExistsWithNODevice_VEST(PatientInfoDTO patientInfoDTO){
		
		if(DeviceOwnedByDifferentPatient_VEST(patientInfoDTO)){
		
			if (!isHillromIdExistInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) {
				
	
				//managePatientDevice(CREATE)
				//managePatientDeviceAssociation(CREATE)
				//createPatientProtocol()
				try{
					patientInfoDTO.setOperation_type("CREATE");
					managePatientDevice(patientInfoDTO);
					
					patientInfoDTO.setOperation_type("CREATE");
					managePatientDeviceAssociation(patientInfoDTO);
					
					patientInfoDTO.setProtocol_type_key("Normal");
					patientInfoDTO.setOperation_type("Insert");
					createPatientProtocol(patientInfoDTO);
				}catch(Exception ex){
					ex.printStackTrace();
					return false;
				}
				
				return true;
			}
			
			return false;
		
		}
		return false;	
	}

}




