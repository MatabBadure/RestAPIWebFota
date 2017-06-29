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
	
	

	/* DateTimeFormatter dobFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
     DateTimeFormatter deviceAssocdateFormat = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");*/
 
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
	public void createPatientProtocol(String typeKey,String operationType,String inPatientId,String inCreatedBy) throws HillromException{		
		timsRepository.createPatientProtocol(typeKey,operationType,inPatientId,inCreatedBy);
	}

  //Start of my code
	/**
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	public void managaPatientDevice(String operationType,String inPatientId,String inPatientoldDeviceSerialNumber,String inPatientNewDeviceSerialNumber,
			String inPatientBluetoothId,String inPatientHubId) throws HillromException{		
		
		timsRepository.managaPatientDevice(operationType, inPatientId, inPatientoldDeviceSerialNumber, inPatientNewDeviceSerialNumber,
				inPatientBluetoothId, inPatientHubId);
	}


	/**
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	
	
	public void managaPatientDeviceAssociation(String operationType,String inpatientPatientId,String inpatientDeviceType,String inpatientDeviceIsActive,
			String inPatientBluetoothId,String inpatientHillromId,String inpatientOldId,DateTimeFormatter inpatientTrainingDate,String inpatientDiagnosisCode1,
			String inpatientDiagnosisCode2,String inpatientDiagnosisCode3,String inpatientDiagnosisCode4,String inpatientGarmentType,
			String inpatientGarmentSize,String inpatientGarmentColor) throws HillromException{	
		
		timsRepository.managaPatientDeviceAssociation(operationType,inpatientPatientId,inpatientDeviceType,inpatientDeviceIsActive,inPatientBluetoothId,
				inpatientHillromId,inpatientOldId,inpatientTrainingDate,inpatientDiagnosisCode1,inpatientDiagnosisCode2,
				inpatientDiagnosisCode3,inpatientDiagnosisCode4,inpatientGarmentType,inpatientGarmentSize,inpatientGarmentColor);
	}

	
	/**This need to remove not required 
	 * 
	 * @param
	 * @return
	 * @throws HillromException
	 */
	
	public JSONObject managePatientUser(String operationTypeIndicator,String inhillRomId,String inPatientHubId,String inPatientBluetoothId,
			String inPatientDeviceSerialNumber,String inPatientTitle,String inPatientFirstName,String inPatientMiddleName,String inPatientLastName,
			String inPatientdob,String inPatientEmail,String inPatientZipCode,String inPatientPrimaryPhone,String inPatientMobilePhone,String inPatientGender,
			String inPatientlangKey,String inPatientAddress,String inPatientCity,String inPatientState,String inPatientTrainingDate,
			String inPatientPrimaryDiagnosis,String inPatientgarmentType,String inPatientGarmentSize,String inPatientGarmentColor) throws HillromException {	

		
		return timsUserRepository.managePatientUser(operationTypeIndicator, inhillRomId, inPatientHubId, inPatientBluetoothId, inPatientDeviceSerialNumber, inPatientTitle,
				inPatientFirstName, inPatientMiddleName, inPatientLastName, inPatientdob, inPatientEmail, inPatientZipCode, inPatientPrimaryPhone, 
				inPatientMobilePhone, inPatientGender, inPatientlangKey, inPatientAddress, inPatientCity, inPatientState, inPatientTrainingDate, 
				inPatientPrimaryDiagnosis, inPatientgarmentType, inPatientGarmentSize, inPatientGarmentColor);
		
		
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
	
	public boolean NeitherPatientNorDeviceExistCASE1_VEST(PatientInfoDTO patientInfoDTO){
		
		if((!isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))){
		
			//managePatientUser(CREATE)
			//managaPatientDevice(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			return true;
		}
		
		return false;
		
	}
	
	public boolean PatientExistsWithNODeviceCASE2_VEST(PatientInfoDTO patientInfoDTO){
		
		if((!isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isHillromIdExistInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
			

			//managaPatientDevice(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			
			return true;
		}
		
		return false;
		
		
	}
	
	public boolean PatientHasMonarchAddVisivestCASE3_VEST(PatientInfoDTO patientInfoDTO){
		
		if((!isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (isHillromIdExistInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) && (!isHillromIdHasVestDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
			

			//managaPatientDevice(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			
			return true;
			
		}
		
		return false;
		
	}
	
	public boolean PatientHasDifferentVisivestSwapCASE4_VEST(PatientInfoDTO patientInfoDTO){
		if((!isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (isHillromIdExistInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) && (isHillromIdHasVestDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
			

			//managaPatientDevice(UPDATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			
			return true;
			
			
		}
		
		return false;		
	}
	

	public boolean DeviceOwnedByShellCASE5_VEST(PatientInfoDTO patientInfoDTO){
		if((isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& isCurrentSerialNumberOwnedByShell(patientInfoDTO.getSerial_num()) ){
			

			//managePatientUser(UPDATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			
			return true;
		}
		
		return false;		
	}
	
	public boolean DeviceOwnedByDifferentPatientCASE6_VEST(PatientInfoDTO patientInfoDTO){
		if((isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByShell(patientInfoDTO.getSerial_num())) 
				&& (isCurrentSerialNumberOwnedByDifferentPatient(patientInfoDTO.getSerial_num() )) ){
			
			//managaPatientDevice(INACTIVATE)
			//managePatientUser(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			
			return true;
		}
		
		return false;		
	}
	
	public boolean DeviceIsOrphanPatientDoesNotExistCASE7_VEST(PatientInfoDTO patientInfoDTO){
		if((isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByShell(patientInfoDTO.getSerial_num())) 
				&& (!isCurrentSerialNumberOwnedByDifferentPatient(patientInfoDTO.getSerial_num() )) ){

			
			//managePatientUser(CREATE)
			//managaPatientDevice(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			
			return true;
		}
		
		return false;		
	}
	
	

	public boolean DeviceIsOrphanButPatientExistCASE8_VEST(PatientInfoDTO patientInfoDTO){
		if((isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByCurrentHillromId(patientInfoDTO.getSerial_num())) 
				&& (isOwnerExistsForCurrentSerialNumber(patientInfoDTO.getSerial_num() )) ){

			
			//managaPatientDevice(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
			
			return true;
		}
		
		return false;		
	}
	
	

	
	public boolean DeviceOwnedByDifferentPatientCASE9_VEST(PatientInfoDTO patientInfoDTO){
		if((isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))
				&& (!isCurrentSerialNumberOwnedByCurrentHillromId(patientInfoDTO.getSerial_num())) 
				&& (!isOwnerExistsForCurrentSerialNumber(patientInfoDTO.getSerial_num() )) ){

			

			//managaPatientDevice(INACTIVATE)

			
			return true;
		}
		
		return false;		
	}


	
	public boolean PatientHasDifferentVisivestSwapCASE10_VEST(PatientInfoDTO patientInfoDTO){
		
		if(DeviceOwnedByDifferentPatientCASE9_VEST(patientInfoDTO)){
		
			if( (isHillromIdExistInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) && (isHillromIdHasVestDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
				
	
				//managaPatientDevice(UPDATE)
				//managaPatientDeviceAssociation(CREATE)
				//createPatientProtocol()
				return true;
			}
			
			return false;
		}
		
		return false;
		
		
	}
	
	public boolean PatientHasMonarchAddVisivestCASE11_VEST(PatientInfoDTO patientInfoDTO){
		
		if(DeviceOwnedByDifferentPatientCASE9_VEST(patientInfoDTO)){
		
			if( (isHillromIdExistInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) && (!isHillromIdHasVestDeviceInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) ){
				
	
				//managaPatientDevice(CREATE)
				//managaPatientDeviceAssociation(CREATE)
				//createPatientProtocol()
				
				return true;
			}
			
			return false;
		
		}
		
		return false;
		
	}
	
	public boolean PatientExistsWithNODeviceCASE12_VEST(PatientInfoDTO patientInfoDTO){
		
		if(DeviceOwnedByDifferentPatientCASE9_VEST(patientInfoDTO)){
		
			if (!isHillromIdExistInPatientDeviceAssoc(patientInfoDTO.getTims_cust())) {
				
	
				//managaPatientDevice(CREATE)
				//managaPatientDeviceAssociation(CREATE)
				//createPatientProtocol()
				
				return true;
			}
			
			return false;
		
		}
		return false;	
	}

}




