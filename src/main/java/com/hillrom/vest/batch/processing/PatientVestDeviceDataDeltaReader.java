package com.hillrom.vest.batch.processing;

import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;

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

import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientDevicesAssoc;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceHistory;
import com.hillrom.vest.domain.PatientVestDevicePK;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.domain.TherapySession;
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
import com.hillrom.vest.service.DeviceLogParser;
import com.hillrom.vest.service.PatientComplianceService;
import com.hillrom.vest.service.PatientNoEventService;
import com.hillrom.vest.service.TherapySessionService;
import com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil;
import com.hillrom.vest.util.RelationshipLabelConstants;

public class PatientVestDeviceDataDeltaReader implements ItemReader<List<PatientVestDeviceData>> {

	private final Logger log = LoggerFactory.getLogger(PatientVestDeviceDataDeltaReader.class);
	
	@Inject
	private UserPatientRepository userPatientRepository;

	@Inject
	private PatientInfoRepository patientInfoRepository;

	@Inject
	private UserExtensionRepository userExtensionRepository;

	@Inject
	private AuthorityRepository authorityRepository;

	@Inject
	private DeviceLogParser deviceLogParser;

	@Inject
	private PatientNoEventService noEventService;

	@Inject
	private TherapySessionService therapySessionService;

	@Inject
	private PatientComplianceService complianceService;
	
	@Inject
	private PatientVestDeviceRawLogRepository deviceRawLogRepository;
	
	@Inject
	private PatientVestDeviceDataRepository vestDeviceDataRepository;

	@Inject
    private PatientVestDeviceRepository patientVestDeviceRepository;
	
	@Inject
    private PatientDevicesAssocRepository patientDevicesAssocRepository;


	private String patientDeviceRawData;
	
	private boolean isReadComplete;

	@Value("#{jobParameters['rawData']}")
	public void setRawData(final String rawData) {
		this.patientDeviceRawData = rawData;
		this.isReadComplete = false;
	}

	private synchronized List<PatientVestDeviceData> parseRawData() throws Exception{
		log.debug("Parsing started rawData : ",patientDeviceRawData);
		PatientVestDeviceRawLog deviceRawLog = null;
		List<PatientVestDeviceData> patientVestDeviceEvents = null;
		deviceRawLog = deviceLogParser.parseBase64StringToPatientVestDeviceRawLog(patientDeviceRawData);

		deviceRawLogRepository.save(deviceRawLog);
		
		patientVestDeviceEvents = deviceLogParser
				.parseBase64StringToPatientVestDeviceLogEntry(deviceRawLog.getDeviceData());

		String deviceSerialNumber = deviceRawLog.getDeviceSerialNumber();
		if(!patientVestDeviceEvents.isEmpty())
		{
		UserPatientAssoc userPatientAssoc = createPatientUserIfNotExists(deviceRawLog, deviceSerialNumber);
		assignDefaultValuesToVestDeviceDataTemp(deviceRawLog, patientVestDeviceEvents, userPatientAssoc);
		}
		return patientVestDeviceEvents;
	}

	@Override
	public List<PatientVestDeviceData> read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		log.debug("ItemReader started");
		if (isReadComplete)
			return null;

		List<PatientVestDeviceData> patientVestDeviceEvents = parseRawData();
		Long patientUserId = 0l, from,to;
		String serialNumber = "";
		String patientId = "";
		if(patientVestDeviceEvents.isEmpty()){
		// this is required to let reader to know there is nothing to be read further
			isReadComplete = true;  
			return patientVestDeviceEvents; // spring batch reader to skip reading
		}else{
			patientUserId = patientVestDeviceEvents.get(0).getPatientUser().getId();
			patientId = patientVestDeviceEvents.get(0).getPatient().getId();
			Collections.sort(patientVestDeviceEvents);
			from = patientVestDeviceEvents.get(0).getTimestamp();
			to = patientVestDeviceEvents.get(patientVestDeviceEvents.size()-1).getTimestamp();
			serialNumber = patientVestDeviceEvents.get(0).getSerialNumber();
		}
		List<PatientVestDeviceData> existingEvents = vestDeviceDataRepository.findByPatientUserIdAndTimestampBetween(patientUserId, from, to);

		log.debug("Calculating the Delta ");
		List<PatientVestDeviceData> patientVestDeviceRecords = getDelta(existingEvents, patientVestDeviceEvents);
		
		// If no new events available , return empty list
		if(patientVestDeviceRecords.isEmpty()){
			log.debug("NO NEW EVENTS FOUND");
			return patientVestDeviceRecords;
		}
		
		log.debug("New Events found to be inserted ");
		PatientVestDeviceHistory latestInActiveDevice = patientVestDeviceRepository.findLatestInActiveDeviceByPatientId(patientId, false);
		List<TherapySession> therapySessions = PatientVestDeviceTherapyUtil
				.prepareTherapySessionFromDeviceData(patientVestDeviceRecords,latestInActiveDevice);

		if(therapySessions.isEmpty()){
			log.debug("Could not make session out of the events received, discarding to get delta");
			isReadComplete = true;  
			return new LinkedList<PatientVestDeviceData>();
		}
		
		therapySessionService.saveOrUpdate(therapySessions);
		return patientVestDeviceRecords;
	}

	private synchronized void assignDefaultValuesToVestDeviceDataTemp(PatientVestDeviceRawLog deviceRawLog,
			List<PatientVestDeviceData> patientVestDeviceRecords, UserPatientAssoc userPatientAssoc) throws Exception{
		patientVestDeviceRecords.stream().forEach(deviceData -> {
			deviceData.setHubId(deviceRawLog.getHubId());
			deviceData.setSerialNumber(deviceRawLog.getDeviceSerialNumber());
			deviceData.setPatient(userPatientAssoc.getPatient());
			deviceData.setPatientUser(userPatientAssoc.getUser());
			deviceData.setBluetoothId(deviceRawLog.getDeviceAddress());
		});
	}

	@Transactional
	private synchronized UserPatientAssoc createPatientUserIfNotExists(PatientVestDeviceRawLog deviceRawLog,
			String deviceSerialNumber) throws Exception{
		Optional<PatientInfo> patientFromDB = patientInfoRepository.findOneBySerialNumber(deviceSerialNumber);

		PatientInfo patientInfo = null;

		if (patientFromDB.isPresent()) {
			patientInfo = patientFromDB.get();
			List<UserPatientAssoc> associations = userPatientRepository.findOneByPatientId(patientInfo.getId());
			List<UserPatientAssoc> userPatientAssociations = associations.stream()
					.filter(assoc -> RelationshipLabelConstants.SELF.equalsIgnoreCase(assoc.getRelationshipLabel()))
					.collect(Collectors.toList());
			return userPatientAssociations.get(0);
		} else {
			patientInfo = new PatientInfo();
			// Assigns the next hillromId for the patient
			String hillromId = patientInfoRepository.id();
			patientInfo.setId(hillromId);
			patientInfo.setHillromId(hillromId);
			patientInfo.setBluetoothId(deviceRawLog.getDeviceAddress());
			patientInfo.setHubId(deviceRawLog.getHubId());
			patientInfo.setSerialNumber(deviceRawLog.getDeviceSerialNumber());
			patientInfo.setDeviceAssocDate(new DateTime());
			String customerName = deviceRawLog.getCustomerName();
			setNameToPatient(patientInfo, customerName);
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
			noEventService.createIfNotExists(
					new PatientNoEvent(createdOrTransmittedDate, createdOrTransmittedDate, patientInfo, userExtension));
			PatientCompliance compliance = new PatientCompliance();
			compliance.setPatient(patientInfo);
			compliance.setPatientUser(userExtension);
			compliance.setDate(userExtension.getCreatedDate().toLocalDate());
			compliance.setScore(DEFAULT_COMPLIANCE_SCORE);
			compliance.setLatestTherapyDate(createdOrTransmittedDate);
			complianceService.createOrUpdate(compliance);
			
			// Create Patient Device History
			PatientVestDeviceHistory deviceHistory = new PatientVestDeviceHistory(new PatientVestDevicePK(patientInfo, patientInfo.getSerialNumber()),
					patientInfo.getBluetoothId(), patientInfo.getHubId(), true);
			patientVestDeviceRepository.save(deviceHistory);
			
			PatientDevicesAssoc deviceAssoc = new PatientDevicesAssoc(patientInfo.getId(), "VEST" ,true);
			patientDevicesAssocRepository.save(deviceAssoc);
			
			return userPatientAssoc;
		}
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

	private List<PatientVestDeviceData> getDelta(List<PatientVestDeviceData> existingEvents, List<PatientVestDeviceData> newEvents){
		isReadComplete = true;
		if(Objects.isNull(existingEvents) || existingEvents.isEmpty())
			return newEvents;
		else{
			Iterator<PatientVestDeviceData> itr = newEvents.iterator();
			while(itr.hasNext()){
				PatientVestDeviceData newEvent = itr.next();
				for(PatientVestDeviceData existingData : existingEvents){
					if(newEvent.getTimestamp().equals(existingData.getTimestamp()) &&
					   newEvent.getBluetoothId().equals(existingData.getBluetoothId()) && 
					   newEvent.getEventId().equals(existingData.getEventId())){
						itr.remove();
						break;
					}
				}
			}
			Collections.sort(newEvents);
			return newEvents;
		}
	}
}
