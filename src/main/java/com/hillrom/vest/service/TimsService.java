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
	
	public boolean NeitherPatientNorDeviceExistVest(PatientInfoDTO patientInfoDTO){
		
		if((!isSerialNoExistInPatientdeviceAssoc(patientInfoDTO.getSerial_num())) && (!isHillromIdExistInPatientInfo(patientInfoDTO.getTims_cust()))){
		
			//managePatientUser(CREATE)
			//managaPatientDevice(CREATE)
			//managaPatientDeviceAssociation(CREATE)
			//createPatientProtocol()
		}
		
		return true;
		
	}
	

}




