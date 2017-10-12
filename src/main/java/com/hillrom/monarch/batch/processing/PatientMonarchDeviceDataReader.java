package com.hillrom.monarch.batch.processing;

import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;

import com.hillrom.monarch.repository.PatientMonarchDeviceDataRepository;
import com.hillrom.monarch.repository.PatientMonarchDeviceRawLogRepository;
import com.hillrom.monarch.repository.PatientMonarchDeviceRepository;
import com.hillrom.monarch.service.PatientComplianceMonarchService;
import com.hillrom.monarch.service.PatientNoEventMonarchService;
import com.hillrom.monarch.service.PatientVestDeviceDataServiceMonarch;
import com.hillrom.monarch.service.TherapySessionServiceMonarch;
import com.hillrom.monarch.service.util.PatientVestDeviceTherapyUtilMonarch;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientComplianceMonarch;
import com.hillrom.vest.domain.PatientDevicesAssoc;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientNoEventMonarch;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.domain.PatientVestDeviceHistory;
import com.hillrom.vest.domain.PatientVestDeviceHistoryMonarch;
import com.hillrom.vest.domain.PatientVestDevicePK;
import com.hillrom.vest.domain.PatientVestDeviceRawLogMonarch;
import com.hillrom.vest.domain.TherapySessionMonarch;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.domain.UserPatientAssocPK;
import com.hillrom.vest.repository.AuthorityRepository;
import com.hillrom.vest.repository.PatientDevicesAssocRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.repository.PatientVestDeviceRawLogRepository;
import com.hillrom.vest.repository.PatientVestDeviceRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.DeviceLogMonarchParser;
import com.hillrom.vest.service.DeviceLogParser;
import com.hillrom.vest.service.PatientInfoService;
import com.hillrom.vest.service.TherapySessionService;
import com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil;
import com.hillrom.vest.util.RelationshipLabelConstants;

public class PatientMonarchDeviceDataReader implements ItemReader<List<PatientVestDeviceDataMonarch>> {

	private final Logger log = LoggerFactory.getLogger(PatientMonarchDeviceDataReader.class);
	
	@Inject
	private UserPatientRepository userPatientRepository;

	@Inject
	private PatientInfoRepository patientInfoRepository;

	@Inject
	private UserExtensionRepository userExtensionRepository;

	@Inject
	private AuthorityRepository authorityRepository;

	@Inject
	private DeviceLogMonarchParser deviceLogMonarchParser;

	@Inject
	private PatientNoEventMonarchService noEventServiceMonarch;

	@Inject
	private TherapySessionServiceMonarch therapySessionServiceMonarch;

	@Inject
	private PatientComplianceMonarchService complianceMonarchService;
	
	@Inject
	private PatientMonarchDeviceRawLogRepository deviceRawLogRepositoryMonarch;
	
	@Inject
	private PatientMonarchDeviceDataRepository monarchDeviceDataRepository;

	@Inject
    private PatientMonarchDeviceRepository patientMonarchDeviceRepository;
	
	@Inject
    private PatientDevicesAssocRepository patientDevicesAssocRepository;

	@Inject
	PatientVestDeviceDataServiceMonarch patientVestDeviceDataServiceMonarch;
	
	@Inject
    private PatientInfoService patientInfoService;
	
	private String patientDeviceRawData;
	
	private boolean isReadComplete;

	@Value("#{jobParameters['rawData']}")
	public void setRawData(final String rawData) {
		this.patientDeviceRawData = rawData;
		this.isReadComplete = false;
	}

	private synchronized List<PatientVestDeviceDataMonarch> parseRawData() throws Exception{
		log.debug("Parsing started rawData : ",patientDeviceRawData);
		PatientVestDeviceRawLogMonarch deviceRawLogMonarch = null;
		List<PatientVestDeviceDataMonarch> patientVestDeviceEventsMonarch = null;
		deviceRawLogMonarch = deviceLogMonarchParser.parseBase64StringToPatientMonarchDeviceRawLog(patientDeviceRawData);
		
		deviceRawLogRepositoryMonarch.save(deviceRawLogMonarch);
		
		patientVestDeviceEventsMonarch = deviceLogMonarchParser
				.parseBase64StringToPatientMonarchDeviceLogEntry(patientDeviceRawData);

		String deviceSerialNumber = deviceRawLogMonarch.getDeviceSerialNumber();
		
		
		if(!patientVestDeviceEventsMonarch.isEmpty())
		{
			UserPatientAssoc userPatientAssoc = createPatientUserIfNotExists(deviceRawLogMonarch, deviceSerialNumber,patientVestDeviceEventsMonarch);
			assignDefaultValuesToVestDeviceDataTempMonarch(deviceRawLogMonarch, patientVestDeviceEventsMonarch, userPatientAssoc);
		}
		return patientVestDeviceEventsMonarch;
	}

	@Override
	public List<PatientVestDeviceDataMonarch> read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		log.debug("ItemReader started");
		if (isReadComplete)
			return null;

		List<PatientVestDeviceDataMonarch> patientVestDeviceEventsMonarch = parseRawData();
		
		
		Long patientUserId = 0l, from,to;
		String serialNumber = "";
		String patientId = "";
		if(patientVestDeviceEventsMonarch.isEmpty()){
		// this is required to let reader to know there is nothing to be read further
			isReadComplete = true;  
			return patientVestDeviceEventsMonarch; // spring batch reader to skip reading
		}
		
		patientUserId = patientVestDeviceEventsMonarch.get(0).getPatientUser().getId();
		patientId = patientVestDeviceEventsMonarch.get(0).getPatient().getId();
		Collections.sort(patientVestDeviceEventsMonarch);
		from = patientVestDeviceEventsMonarch.get(0).getTimestamp();
		to = patientVestDeviceEventsMonarch.get(patientVestDeviceEventsMonarch.size()-1).getTimestamp();
		serialNumber = patientVestDeviceEventsMonarch.get(0).getSerialNumber();
		
		int therapyIndex = patientVestDeviceEventsMonarch.get(0).getTherapyIndex();
		int fragTotal = patientVestDeviceEventsMonarch.get(0).getFragTotal();
		int fragCurr = patientVestDeviceEventsMonarch.get(0).getFragCurrent();			
					
		// Commenting the code to get the existing events to make the diff with current events
		//List<PatientVestDeviceDataMonarch> existingEvents = monarchDeviceDataRepository.findByPatientUserIdAndTimestampBetween(patientUserId, from, to);

		//log.debug("Calculating the Delta ");
		//List<PatientVestDeviceDataMonarch> patientVestDeviceRecords = getDelta(existingEvents, patientVestDeviceEvents);
		
		// If no new events available , return empty list
		/*if(patientVestDeviceRecords.isEmpty()){
			log.debug("NO NEW EVENTS FOUND");
			return patientVestDeviceRecords;
		}*/
		
		if(fragTotal == 1 || (fragTotal != 1 && fragTotal == fragCurr)){		
			List<PatientVestDeviceDataMonarch> patientVestDeviceRecordsMonarch = new LinkedList<>();
			
			if(fragTotal != 1 && fragTotal == fragCurr){
				List<PatientVestDeviceDataMonarch> existingVestDeviceEventsMonarch = patientVestDeviceDataServiceMonarch.getDeviceDataForAllFragments(patientUserId, serialNumber, therapyIndex);
				
				patientVestDeviceRecordsMonarch.addAll(existingVestDeviceEventsMonarch);
				patientVestDeviceRecordsMonarch.addAll(patientVestDeviceEventsMonarch);
			}else{
				patientVestDeviceRecordsMonarch = patientVestDeviceEventsMonarch;
			}
			
			PatientVestDeviceHistoryMonarch latestInActiveDevice = patientMonarchDeviceRepository.findLatestInActiveDeviceByPatientId(patientId, false);
			List<TherapySessionMonarch> therapySessionsMonarch = PatientVestDeviceTherapyUtilMonarch
					.prepareTherapySessionFromDeviceDataMonarch(patientVestDeviceRecordsMonarch,latestInActiveDevice);
	
			if(therapySessionsMonarch.isEmpty()){
				log.debug("Could not make session out of the events received, discarding to get delta");
				isReadComplete = true;
				return new LinkedList<PatientVestDeviceDataMonarch>();
			}
			therapySessionServiceMonarch.saveOrUpdate(therapySessionsMonarch);
		}
		
		isReadComplete = true;
		return patientVestDeviceEventsMonarch;
	}

	private synchronized void assignDefaultValuesToVestDeviceDataTempMonarch(PatientVestDeviceRawLogMonarch deviceRawLogMonarch,
			List<PatientVestDeviceDataMonarch> patientVestDeviceRecordsMonarch, UserPatientAssoc userPatientAssoc) throws Exception{
		patientVestDeviceRecordsMonarch.stream().forEach(deviceData -> {
			//deviceData.setHubId(deviceRawLog.getHubId());
			deviceData.setSerialNumber(deviceRawLogMonarch.getDeviceSerialNumber());
			deviceData.setPatient(userPatientAssoc.getPatient());
			deviceData.setPatientUser(userPatientAssoc.getUser());
			//deviceData.setBluetoothId(deviceRawLog.getDeviceAddress());
		});
	}

	@Transactional
	private synchronized UserPatientAssoc createPatientUserIfNotExists(PatientVestDeviceRawLogMonarch deviceRawLog,
			String deviceSerialNumber,List<PatientVestDeviceDataMonarch> patientVestDeviceEventsMonarch) throws Exception{
		
		Optional<PatientDevicesAssoc> patientDevicesFromDB = patientDevicesAssocRepository.findOneBySerialNumber(deviceSerialNumber);
		PatientInfo patientInfo = null;
		String devBT=null; String dev_wifi=null; String dev_lte=null;

		List<PatientVestDeviceHistoryMonarch> patientMonarchDeviceHistoryList = new LinkedList<>();
		patientMonarchDeviceHistoryList = patientMonarchDeviceRepository.findBySerialNumber(deviceSerialNumber);
		
		if(!patientVestDeviceEventsMonarch.isEmpty()){
			devBT = patientVestDeviceEventsMonarch.get(0).getDevBt();
			dev_wifi = patientVestDeviceEventsMonarch.get(0).getDevWifi();
			dev_lte = patientVestDeviceEventsMonarch.get(0).getDevLte();
		}
		
		if (patientDevicesFromDB.isPresent()) {
			
			if(!patientMonarchDeviceHistoryList.isEmpty()){
				for(PatientVestDeviceHistoryMonarch patientMonarchDevicePatient : patientMonarchDeviceHistoryList){
					if(Objects.nonNull(patientMonarchDevicePatient) && patientMonarchDevicePatient.isPending() 
							&& deviceSerialNumber.equalsIgnoreCase(patientMonarchDevicePatient.getSerialNumber())){
						patientMonarchDevicePatient.setPending(false);
						patientMonarchDeviceRepository.save(patientMonarchDevicePatient);
					}
					if(Objects.nonNull(patientMonarchDevicePatient) && patientMonarchDevicePatient.isActive() 
							&& deviceSerialNumber.equalsIgnoreCase(patientMonarchDevicePatient.getSerialNumber())){
						patientMonarchDevicePatient.setDevBt(devBT);
						patientMonarchDevicePatient.setWifiId(dev_wifi);
						patientMonarchDevicePatient.setLteId(dev_lte);
						patientMonarchDeviceRepository.save(patientMonarchDevicePatient);
					}
				}
			}
			
			return retrieveUserPatientAssoc(patientDevicesFromDB.get().getPatientId());
		} else {
			if(!patientMonarchDeviceHistoryList.isEmpty()){
				PatientVestDeviceHistoryMonarch patientMonarchDevicePatient = patientMonarchDeviceHistoryList.get(0);
			
				Optional<PatientVestDeviceHistoryMonarch> patientMonarchDeviceHistory = patientMonarchDeviceRepository.findOneByPatientIdAndPendingStatus(patientMonarchDevicePatient.getPatient().getId(), true);
			
				if (patientMonarchDeviceHistory.isPresent()){
					return retrieveUserPatientAssoc(patientMonarchDevicePatient.getPatient().getId());
				}
			}
			
			patientInfo = new PatientInfo();
			// Assigns the next hillromId for the patient
			String hillromId = patientInfoRepository.id();
			patientInfo.setId(hillromId);
			patientInfo.setHillromId(hillromId);
			patientInfo.setBluetoothId(deviceRawLog.getDeviceAddress());
			//patientInfo.setHubId(deviceRawLog.getHubId());
			patientInfo.setSerialNumber(deviceRawLog.getDeviceSerialNumber());
			patientInfo.setDeviceAssocDate(new DateTime());
			//String customerName = deviceRawLog.getCustomerName();
			// Hardcoded the patient name to Hill-Rom Monarch for monarch users
			setNameToPatient(patientInfo, "Hill-Rom Monarch");
			patientInfo = patientInfoRepository.save(patientInfo);

			UserExtension userExtension = new UserExtension();
			userExtension.setHillromId(patientInfo.getHillromId());
			userExtension.setActivated(true);
			userExtension.setDeleted(false);
			userExtension.setFirstName(patientInfo.getFirstName());
			userExtension.setLastName(patientInfo.getLastName());
			userExtension.setMiddleName(patientInfo.getMiddleName());
			userExtension.getAuthorities().add(authorityRepository.findOne(PATIENT));
			userExtensionRepository.save(userExtension);

			UserPatientAssoc userPatientAssoc = new UserPatientAssoc(new UserPatientAssocPK(patientInfo, userExtension),
					AuthoritiesConstants.PATIENT, RelationshipLabelConstants.SELF);

			userPatientRepository.save(userPatientAssoc);

			userExtension.getUserPatientAssoc().add(userPatientAssoc);
			patientInfo.getUserPatientAssoc().add(userPatientAssoc);

			userExtensionRepository.save(userExtension);
			patientInfoRepository.save(patientInfo);
			LocalDate createdOrTransmittedDate = userExtension.getCreatedDate().toLocalDate();
			noEventServiceMonarch.createIfNotExists(
					new PatientNoEventMonarch(createdOrTransmittedDate, createdOrTransmittedDate, patientInfo, userExtension));
			PatientComplianceMonarch compliance = new PatientComplianceMonarch();
			compliance.setPatient(patientInfo);
			compliance.setPatientUser(userExtension);
			compliance.setDate(userExtension.getCreatedDate().toLocalDate());
			compliance.setScore(DEFAULT_COMPLIANCE_SCORE);
			compliance.setLatestTherapyDate(createdOrTransmittedDate);
			complianceMonarchService.createOrUpdate(compliance);
			
			// Create Patient Device History
			PatientVestDeviceHistoryMonarch deviceHistoryMonarch = new PatientVestDeviceHistoryMonarch(new PatientVestDevicePK(patientInfo, patientInfo.getSerialNumber()),
					patientInfo.getBluetoothId(), patientInfo.getHubId(), true);
			deviceHistoryMonarch.setDevBt(devBT);
			deviceHistoryMonarch.setWifiId(dev_wifi);
			deviceHistoryMonarch.setLteId(dev_lte);
			patientMonarchDeviceRepository.save(deviceHistoryMonarch);
			
			PatientDevicesAssoc deviceAssoc = new PatientDevicesAssoc(patientInfo.getId(), "MONARCH", true, deviceSerialNumber);
			patientDevicesAssocRepository.save(deviceAssoc);
			
			return userPatientAssoc;
		}
	}
	
	public UserPatientAssoc retrieveUserPatientAssoc(String patientId){
		PatientInfo patientInfo = patientInfoService.findOneById(patientId);
		List<UserPatientAssoc> associations = new ArrayList<UserPatientAssoc> (patientInfo.getUserPatientAssoc());
		List<UserPatientAssoc> userPatientAssociations = associations.stream()
				.filter(assoc -> RelationshipLabelConstants.SELF.equalsIgnoreCase(assoc.getRelationshipLabel()))
				.collect(Collectors.toList());
		return userPatientAssociations.get(0);
	}

	private void setNameToPatient(PatientInfo patientInfo, String customerName) {
		String names[] = customerName.split(" ");
		if (names.length == 2) {
			assignNameToPatient(patientInfo, names[1], names[0], null);
		}
		if (names.length == 3) {
			assignNameToPatient(patientInfo, names[2], names[1], names[0]);
		}
		if (names.length == 1) {
			assignNameToPatient(patientInfo, names[0], null, null);
		}
	}

	private void assignNameToPatient(PatientInfo patientInfo, String firstName, String lastName, String middleName) {
		patientInfo.setFirstName(firstName);
		patientInfo.setLastName(lastName);
		patientInfo.setMiddleName(middleName);
	}
}
