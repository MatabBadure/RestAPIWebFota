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

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.domain.PatientDevicesAssoc;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AnnouncementsPermissionRepository;
import com.hillrom.vest.repository.AnnouncementsRepository;
import com.hillrom.vest.repository.PatientDevicesAssocRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientVestDeviceRepository;
import com.hillrom.vest.repository.TimsUserRepository;
import com.hillrom.vest.repository.monarch.PatientMonarchDeviceRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.monarch.PatientVestDeviceMonarchService;
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
public class TimsSPProcessorService {

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
	

	public void manage_patient_device_assoc(PatientInfoDTO patientInfoDTO){
		if(patientInfoDTO.getOperation_type().equalsIgnoreCase("CREATE")){
			List<PatientDevicesAssoc> deviceAssocList = patientDevicesAssocRepository.findByPatientIdActiveInactive(patientInfoDTO.getPatient_id());
			
			if(patientInfoDTO.getDevice_type().equalsIgnoreCase("MONARCH") && deviceAssocList.get(0).getHillromId() != null){
				Optional<PatientDevicesAssoc> deviceAssocHillromIdVest = patientDevicesAssocRepository.findByHillromIdAndDeviceTypeActiveInactive(patientInfoDTO.getTims_cust(),"VEST");
				if(deviceAssocHillromIdVest.get().getHillromId() != null){
					
					PatientDevicesAssoc updateableDeviceAssoc = deviceAssocHillromIdVest.get();
					updateableDeviceAssoc.setPatientType("CD");
					patientDevicesAssocRepository.save(updateableDeviceAssoc);

					Optional<PatientDevicesAssoc> deviceAssocSerialNumMonarch = patientDevicesAssocRepository.findOneBySerialNumberAndDeviceTypeActiveInactive(patientInfoDTO.getSerial_num(),"MONARCH");
					if(!(deviceAssocSerialNumMonarch.get().getPatientId().equalsIgnoreCase(patientInfoDTO.getPatient_id())) && (deviceAssocSerialNumMonarch.get().getSerialNumber().equalsIgnoreCase(patientInfoDTO.getSerial_num())) && (deviceAssocSerialNumMonarch.get().getDeviceType().equalsIgnoreCase("MONARCH"))){
						if(deviceAssocSerialNumMonarch.get().getHillromId().equalsIgnoreCase("") || deviceAssocSerialNumMonarch.get().getHillromId() == null){
							PatientInfo updateablePatientInfo = patientInfoService.findOneById(deviceAssocList.get(0).getPatientId());
							updateablePatientInfo.setExpired(true);
							updateablePatientInfo.setExpiredDate(new DateTime());
							patientInfoRepository.save(updateablePatientInfo);

							updateableDeviceAssoc = patientDevicesAssocRepository.findOneBySerialNumberAndHillromIdAndDeviceTypeAndPatientType(patientInfoDTO.getSerial_num(),null,"MONARCH","SD").get();
//							
							updateableDeviceAssoc.setPatientId(patientInfoDTO.getPatient_id());
							updateableDeviceAssoc.setHillromId(patientInfoDTO.getTims_cust());
							updateableDeviceAssoc.setPatientType("CD");
							updateableDeviceAssoc.setModifiedDate(new LocalDate());
							updateableDeviceAssoc.setOldPatientId(deviceAssocList.get(0).getOldPatientId());
							updateableDeviceAssoc.setHubId(patientInfoDTO.getHub_id());
//							UPDATE PATIENT_DEVICES_ASSOC PVDA SET 
//							`patient_id` = pat_patient_id,
//							`hillrom_id` =  pat_hillrom_id,
//							`patient_type` ='CD', 
//							`modified_date` = today_date,
//							`old_patient_id` = temp_patient_info_id,
//							`hub_id` = pat_hub_id,
//							`bluetooth_id` = pat_bluetooth_id,
//							`diagnosis1` = pat_diagnosis_code1,
//							`diagnosis2` = pat_diagnosis_code2,
//							`diagnosis3` = pat_diagnosis_code3,
//							`diagnosis4` = pat_diagnosis_code4,
//							`garment_type` = pat_garment_type,
//							`garment_size` = pat_garment_size,
//							`garment_color` = pat_garment_color
//							where PVDA.`serial_number` = pat_device_serial_number  
//							AND  (PVDA.`hillrom_id` = '' OR PVDA.`hillrom_id` IS NULL) AND PVDA. `patient_type` = 'SD' 
//							AND PVDA.`device_type` = 'MONARCH' ;
						}else{
							
//						     -- we need update the old patient as SD and mark the old vest as inactive
//							 UPDATE PATIENT_DEVICES_ASSOC PVDA SET 
//							`patient_type` ='SD', 
//							`modified_date` = today_date,
//							`old_patient_id` = temp_patient_info_id,
//							`is_active` = false
//							where PVDA.`patient_id` = temp_patient_info_id; 
								
							if(deviceAssocSerialNumMonarch.get().getDeviceType().equalsIgnoreCase("CD")){
								
								
//								-- update the old vest as SD	
//								 UPDATE PATIENT_DEVICES_ASSOC PVDA SET 
//								`patient_type` ='SD', 
//								`modified_date` = today_date,
//								`old_patient_id` = temp_patient_info_id
//								where PVDA.`patient_id` = temp_patient_info_id 
//								AND PVDA.`device_type` = 'VEST' ;

							}
//								-- INsert the new for new patient device associated CD Monarch
//								INSERT INTO `PATIENT_DEVICES_ASSOC`
//								(`patient_id`, `device_type`, `is_active`, `serial_number`,`hub_id`,`bluetooth_id`, `hillrom_id`, `patient_type`, `created_date`, `modified_date`,
//								`old_patient_id`,`training_date`,`diagnosis1`,`diagnosis2`,`diagnosis3`,`diagnosis4`,`garment_type`,`garment_size`,`garment_color`)
//								VALUES	(pat_patient_id,pat_device_type,1,pat_device_serial_number,pat_hub_id,pat_bluetooth_id,pat_hillrom_id,'CD',today_date,today_date,pat_old_id,pat_training_date,pat_diagnosis_code1,pat_diagnosis_code2,pat_diagnosis_code3,pat_diagnosis_code4,pat_garment_type,pat_garment_size,pat_garment_color);

						}
					}else{
//						INSERT INTO `PATIENT_DEVICES_ASSOC`
//						(`patient_id`, `device_type`, `is_active`, `serial_number`,`hub_id`,`bluetooth_id`, `hillrom_id`, `patient_type`, `created_date`, `modified_date`,
//						`old_patient_id`,`training_date`,`diagnosis1`,`diagnosis2`,`diagnosis3`,`diagnosis4`,`garment_type`,`garment_size`,`garment_color`)
//						VALUES	(pat_patient_id,pat_device_type,1,pat_device_serial_number,pat_hub_id,pat_bluetooth_id,pat_hillrom_id,'CD',today_date,today_date,pat_old_id,pat_training_date,pat_diagnosis_code1,pat_diagnosis_code2,pat_diagnosis_code3,pat_diagnosis_code4,pat_garment_type,pat_garment_size,pat_garment_color);

					}
				}else{
					
//					INSERT INTO `PATIENT_DEVICES_ASSOC`
//					(`patient_id`, `device_type`, `is_active`, `serial_number`,`hub_id`,`bluetooth_id`, `hillrom_id`, `patient_type`, `created_date`, `modified_date`,
//					`old_patient_id`,`training_date`,`diagnosis1`,`diagnosis2`,`diagnosis3`,`diagnosis4`,`garment_type`,`garment_size`,`garment_color`)
//					VALUES
//					(pat_patient_id,pat_device_type,1,pat_device_serial_number,pat_hub_id,pat_bluetooth_id,pat_hillrom_id,device_patient_type,today_date,null,pat_old_id,pat_training_date,pat_diagnosis_code1,pat_diagnosis_code2,pat_diagnosis_code3,pat_diagnosis_code4,pat_garment_type,pat_garment_size,pat_garment_color);

				}
			}else{
				
//				INSERT INTO `PATIENT_DEVICES_ASSOC`
//				(`patient_id`, `device_type`, `is_active`, `serial_number`,`hub_id`,`bluetooth_id`, `hillrom_id`, `patient_type`, `created_date`, `modified_date`,
//				`old_patient_id`,`training_date`,`diagnosis1`,`diagnosis2`,`diagnosis3`,`diagnosis4`,`garment_type`,`garment_size`,`garment_color`)
//				VALUES
//				(pat_patient_id,pat_device_type,1,pat_device_serial_number,pat_hub_id,pat_bluetooth_id,pat_hillrom_id,device_patient_type,today_date,null,pat_old_id,pat_training_date,
//				pat_diagnosis_code1,pat_diagnosis_code2,pat_diagnosis_code3,pat_diagnosis_code4,pat_garment_type,pat_garment_size,pat_garment_color);
			}
			
		}else if(patientInfoDTO.getOperation_type().equalsIgnoreCase("UPDATE")){
			List<PatientDevicesAssoc> deviceAssocListInUpdate = patientDevicesAssocRepository.findByPatientIdActiveInactive(patientInfoDTO.getPatient_id());
			if((deviceAssocListInUpdate.get(0).getSerialNumber().equalsIgnoreCase(patientInfoDTO.getSerial_num())) && (deviceAssocListInUpdate.get(0).getPatientType().equalsIgnoreCase("SD")) && (deviceAssocListInUpdate.get(0).getDeviceType().equalsIgnoreCase("MONARCH")) && (deviceAssocListInUpdate.get(0).getHillromId() == null)){
				Optional<PatientDevicesAssoc> deviceAssocHillromIdDeviceTypeSD =  patientDevicesAssocRepository.findByHillromIdAndDeviceTypeAndPatientType(patientInfoDTO.getTims_cust(),"VEST","SD");
				if(deviceAssocListInUpdate.get(0).getPatientId() != null){
//					UPDATE PATIENT_DEVICES_ASSOC PVDA SET 
//					`patient_id` = vest_device_patient_id,
//					`hillrom_id` = vest_device_hillrom_id ,
//					`patient_type` ='CD', 
//					`hub_id` = pat_hub_id,
//					`bluetooth_id` = pat_bluetooth_id,
//					`modified_date` = today_date,
//					`old_patient_id` = pat_patient_id,
//					`training_date` = pat_training_date,
//					`diagnosis1` = pat_diagnosis_code1,
//					`diagnosis2` = pat_diagnosis_code2,
//					`diagnosis3` = pat_diagnosis_code3,
//					`diagnosis4` = pat_diagnosis_code4,
//					`garment_type` = pat_garment_type,
//					`garment_size` = pat_garment_size,
//					`garment_color` = pat_garment_color
//					where PVDA.`serial_number` = pat_device_serial_number  
//					AND  (PVDA.`hillrom_id` = '' OR PVDA.`hillrom_id` IS NULL) AND PVDA. `patient_type` = device_patient_type 
//					AND PVDA.`device_type` = 'MONARCH' ;
//					
//
//					UPDATE `PATIENT_INFO` SET
//					`expired` = 1,
//					`expired_date` = today_date
//					WHERE `id` = pat_patient_id;
				}
			}else{
				if((deviceAssocListInUpdate.get(0).getPatientId() != null) && (deviceAssocListInUpdate.get(0).getSerialNumber() != null)){

//					UPDATE `PATIENT_DEVICES_ASSOC` pvda SET
//					`patient_id` = pat_patient_id,
//					`device_type` = pat_device_type,
//					`is_active` = 1,
//					`serial_number` = pat_device_serial_number,
//					`hub_id` = pat_hub_id,
//					`bluetooth_id` = pat_bluetooth_id,
//					`hillrom_id` = pat_hillrom_id,
//					`patient_type` = device_patient_type,
//					`old_patient_id` = pat_old_id,
//					`training_date` = pat_training_date,
//					`diagnosis1` = pat_diagnosis_code1,
//					`diagnosis2` = pat_diagnosis_code2,
//					`diagnosis3` = pat_diagnosis_code3,
//					`diagnosis4` = pat_diagnosis_code4,
//					`garment_type` = pat_garment_type,
//					`garment_size` = pat_garment_size,
//					`garment_color` = pat_garment_color
//					 WHERE pvda.`patient_id` = pat_patient_id AND pvda.`serial_number` = pat_device_serial_number;
				}
			}
			
		}else{
		
			//Operation Not Supported
		}
	}

	
	
	}






