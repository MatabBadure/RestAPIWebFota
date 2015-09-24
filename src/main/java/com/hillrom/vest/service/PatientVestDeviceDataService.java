package com.hillrom.vest.service;

import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.domain.UserPatientAssocPK;
import com.hillrom.vest.domain.VestDeviceBadData;
import com.hillrom.vest.repository.AuthorityRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.repository.PatientVestDeviceRawLogRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.VestDeviceBadDataRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil;
import com.hillrom.vest.util.RelationshipLabelConstants;

@Service
@Transactional(noRollbackFor={RuntimeException.class})
public class PatientVestDeviceDataService {

	@Inject
	private DeviceLogParser deviceLogParser;

	@Inject
	private PatientVestDeviceDataRepository deviceDataRepository;

	@Inject
	private PatientVestDeviceRawLogRepository deviceRawLogRepository;

	@Inject
	private PatientInfoRepository patientInfoRepository;
	
	@Inject
	private UserExtensionRepository userExtensionRepository;
	
	@Inject
	private UserPatientRepository userPatientRepository;
	
	@Inject
	private TherapySessionService therapySessionService;
	
	@Inject
	private AuthorityRepository authorityRepository;
	
	@Inject
	private VestDeviceBadDataRepository vestDeviceBadDataRepository;

	@Inject
	private PatientNoEventService noEventService;
	
	public List<PatientVestDeviceData> save(final String rawData) {
		PatientVestDeviceRawLog deviceRawLog = null;
		List<PatientVestDeviceData> patientVestDeviceRecords = null;
		try {
			deviceRawLog = deviceLogParser
					.parseBase64StringToPatientVestDeviceRawLog(rawData);
			
			patientVestDeviceRecords = deviceLogParser
					.parseBase64StringToPatientVestDeviceLogEntry(deviceRawLog
							.getDeviceData());
			
			String deviceSerialNumber = deviceRawLog.getDeviceSerialNumber();

			UserPatientAssoc userPatientAssoc = createPatientUserIfNotExists(deviceRawLog,
					deviceSerialNumber);
			assignDefaultValuesToVestDeviceData(deviceRawLog,
					patientVestDeviceRecords, userPatientAssoc);
			List<TherapySession> therapySessions = PatientVestDeviceTherapyUtil
					.prepareTherapySessionFromDeviceData(patientVestDeviceRecords);
			
			therapySessions = therapySessionService.saveOrUpdate(therapySessions);
			
			deviceDataRepository.save(patientVestDeviceRecords);
		} catch (Exception e) {
			vestDeviceBadDataRepository.save(new VestDeviceBadData(rawData));
			throw new RuntimeException(e.getMessage());
		}finally{
			if(Objects.nonNull(deviceRawLog)){				
				deviceRawLogRepository.save(deviceRawLog);
			}
		}
		return patientVestDeviceRecords;
	}

	private UserPatientAssoc createPatientUserIfNotExists(
			PatientVestDeviceRawLog deviceRawLog, String deviceSerialNumber) {
		Optional<PatientInfo> patientFromDB = patientInfoRepository
				.findOneBySerialNumber(deviceSerialNumber);

		PatientInfo patientInfo = null;
		
		if(patientFromDB.isPresent()){
			patientInfo = patientFromDB.get();
			List<UserPatientAssoc> associations = userPatientRepository.findOneByPatientId(patientInfo.getId());
			List<UserPatientAssoc> userPatientAssociations =  associations.stream().filter(assoc -> 
				RelationshipLabelConstants.SELF.equalsIgnoreCase(assoc.getRelationshipLabel())
			).collect(Collectors.toList());
			return userPatientAssociations.get(0);
		}else{
			patientInfo = new PatientInfo();
			// Assigns the next hillromId for the patient
			String hillromId = patientInfoRepository.id();
			patientInfo.setId(hillromId);
			patientInfo.setHillromId(hillromId);
			patientInfo.setBluetoothId(deviceRawLog.getDeviceAddress());
			patientInfo.setHubId(deviceRawLog.getHubId());
			patientInfo.setSerialNumber(deviceRawLog.getDeviceSerialNumber());
			String customerName = deviceRawLog.getCustomerName();
			setNameToPatient(patientInfo, customerName);
			patientInfo = patientInfoRepository.save(patientInfo);
			
			UserExtension userExtension = new UserExtension();
			userExtension.setHillromId(hillromId);
			userExtension.setActivated(true);
			userExtension.setDeleted(false);
			userExtension.setFirstName(patientInfo.getFirstName());
			userExtension.setLastName(patientInfo.getLastName());
			userExtension.setMiddleName(patientInfo.getMiddleName());
			userExtension.getAuthorities().add(authorityRepository.findOne(PATIENT));
			userExtensionRepository.save(userExtension);
			
			UserPatientAssoc userPatientAssoc = new UserPatientAssoc(
					new UserPatientAssocPK(patientInfo, userExtension),
					AuthoritiesConstants.PATIENT,
					RelationshipLabelConstants.SELF);
			
			userPatientRepository.save(userPatientAssoc);
			
			userExtension.getUserPatientAssoc().add(userPatientAssoc);
			patientInfo.getUserPatientAssoc().add(userPatientAssoc);
			
			userExtensionRepository.save(userExtension);
			patientInfoRepository.save(patientInfo);
			LocalDate createdOrTransmittedDate = userExtension.getCreatedDate().toLocalDate();
			noEventService.createIfNotExists(new PatientNoEvent(createdOrTransmittedDate,createdOrTransmittedDate, patientInfo, userExtension));
			return userPatientAssoc;
		}
	}

	private void setNameToPatient(PatientInfo patientInfo,String customerName){
		String names[] = customerName.split(" ");
		if(names.length == 2){
			assignNameToPatient(patientInfo,names[1],names[0],null);
		}
		if(names.length == 3){
			assignNameToPatient(patientInfo,names[2],names[1],names[0]);
		}
		if(names.length == 1){
			assignNameToPatient(patientInfo,names[0],null,null);
		}
	}
	
	private void assignNameToPatient(PatientInfo patientInfo,String firstName,String lastName,String middleName){
		patientInfo.setFirstName(firstName);
		patientInfo.setLastName(lastName);
		patientInfo.setMiddleName(middleName);
	}
	
	private void assignDefaultValuesToVestDeviceData(
			PatientVestDeviceRawLog deviceRawLog,
			List<PatientVestDeviceData> patientVestDeviceRecords,
			UserPatientAssoc userPatientAssoc) {
		patientVestDeviceRecords.stream().forEach(deviceData -> {
			deviceData.setHubId(deviceRawLog.getHubId());
			deviceData.setSerialNumber(deviceRawLog.getDeviceSerialNumber());
			deviceData.setPatient(userPatientAssoc.getPatient());
			deviceData.setPatientUser(userPatientAssoc.getUser());
			deviceData.setBluetoothId(deviceRawLog.getDeviceAddress());
		});
	}
}
