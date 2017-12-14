package com.hillrom.monarch.service;

import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hillrom.monarch.repository.PatientNoEventsMonarchRepository;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientNoEventMonarch;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientDevicesAssocRepository;
import com.hillrom.vest.repository.PatientNoEventsRepository;
import com.hillrom.vest.service.AdherenceCalculationService;
import com.hillrom.vest.service.MailService;
import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.util.ExceptionConstants;

@Service
@Transactional
public class PatientNoEventMonarchService {
	
	private final Logger log = LoggerFactory.getLogger(AdherenceCalculationServiceMonarch.class);

	@Inject
	public PatientNoEventsMonarchRepository noEventsMonarchRepository;
	
	
	@Inject
	public PatientNoEventsRepository noEventsRepository;
	
	@Inject
	@Lazy
	private AdherenceCalculationService adherenceCalculationService;
	
	@Inject
	@Lazy
	private AdherenceCalculationServiceMonarch adherenceCalculationServiceMonarch;
	
	@Inject
	private MailService mailService;
	
	
	@Inject
	PatientDevicesAssocRepository patientDevicesAssocRepository;
	
	public PatientNoEventMonarch createIfNotExists(PatientNoEventMonarch newPatientWithNoEvent){
		PatientNoEventMonarch patientNoEvent = noEventsMonarchRepository.findByPatientUserId(newPatientWithNoEvent.getPatientUser().getId());
		if(Objects.nonNull(patientNoEvent)){
			return patientNoEvent;
		}else{
			patientNoEvent = noEventsMonarchRepository.save(newPatientWithNoEvent);
		}
		return patientNoEvent;
	}
	
	public PatientNoEventMonarch updatePatientFirstTransmittedDate(Long patientUserId,LocalDate transmittedDate,String patientId){
		PatientNoEventMonarch patientNoEvent = noEventsMonarchRepository.findByPatientUserId(patientUserId);
		/*if(Objects.nonNull(patientNoEvent)){
			patientNoEvent.setFirstTransmissionDate(transmittedDate);
			noEventsMonarchRepository.save(patientNoEvent);
		}*/
		
		LocalDate trainingDate = getTrainingDateForAdherence(patientId);
		if(Objects.nonNull(patientNoEvent)){
			if(Objects.nonNull(trainingDate) && trainingDate.isAfter (transmittedDate)){
				patientNoEvent.setFirstTransmissionDate(trainingDate);
				if ((Objects.nonNull(trainingDate)) && (Objects.isNull(patientNoEvent.getFirstTransmissionDateBeforeUpdate()))) {
					patientNoEvent.setFirstTransmissionDateBeforeUpdate(transmittedDate);
				}
			}else if(Objects.nonNull(trainingDate) && trainingDate.equals(transmittedDate)){ 
				patientNoEvent.setFirstTransmissionDate(transmittedDate);
				patientNoEvent.setFirstTransmissionDateBeforeUpdate(transmittedDate);
				patientNoEvent.setFirstTransDateType("first_trans");
			}else{
				patientNoEvent.setFirstTransmissionDate(transmittedDate);
				patientNoEvent.setFirstTransDateType("first_trans");
			}
			
			noEventsMonarchRepository.save(patientNoEvent);
		}
		
		
		
		return patientNoEvent;
	}
	//added new method to get training date
	private LocalDate getTrainingDateForAdherence(String patientId) {
			LocalDate trainingDate = patientDevicesAssocRepository.findOneByPatientIdAndDeviceType(patientId,"MONARCH").getTrainingDate();
			return trainingDate;
	}

	public PatientNoEventMonarch findByPatientUserId(Long patientUserId){
		return noEventsMonarchRepository.findByPatientUserId(patientUserId);
	}
	
	public PatientNoEventMonarch findByPatientId(String patientId){
		return noEventsMonarchRepository.findByPatientId(patientId);
	}
	
	public LocalDate getPatientFirstTransmittedDate(Long patientUserId) throws HillromException{
		PatientNoEventMonarch patientNoEvent = noEventsMonarchRepository
				.findByPatientUserId(patientUserId);
		if (Objects.nonNull(patientNoEvent)) {
			return GraphUtils.getFirstTransmissionDateMonarchByType(patientNoEvent);
		} else
			throw new HillromException(ExceptionConstants.HR_702);
	}
	
	public List<PatientNoEventMonarch> findAll(){
		return noEventsMonarchRepository.findAll();
	}
	

	public void save(PatientNoEventMonarch patientNoEventMonarch){
		noEventsMonarchRepository.save(patientNoEventMonarch);
	}
	

	public Map<Long,PatientNoEventMonarch> findAllGroupByPatientUserId(){
		List<PatientNoEventMonarch> patientNoEvents = findAll();
		Map<Long,PatientNoEventMonarch> userIdNoEventsMap = new HashMap<>();
		for(PatientNoEventMonarch patientNoEvent : patientNoEvents){
			userIdNoEventsMap.put(patientNoEvent.getPatientUser().getId(), patientNoEvent);
		}
		return userIdNoEventsMap;
	}
	
	public Map<Long,PatientNoEventMonarch> findAllByPatientUserId(List<Long> userIdList){
		List<PatientNoEventMonarch> patientNoEvents = noEventsMonarchRepository.findByPatientUserIdIn(userIdList);
		Map<Long,PatientNoEventMonarch> userIdNoEventsMap = new HashMap<>();
		for(PatientNoEventMonarch patientNoEvent : patientNoEvents){
			userIdNoEventsMap.put(patientNoEvent.getPatientUser().getId(), patientNoEvent);
		}
		return userIdNoEventsMap;
	}
	
	/**

	 * Reset Adherence score for Shell Patient by Training Date

	 */
	@Scheduled(cron="0 0 9 * * * ")
	//@Scheduled(cron="*/10 * * * * *")
	public void processAdherenceScoreForVestMonarchByTrainingDate(){
		try{
			LocalDate today = LocalDate.now();
			log.debug("Get all Patients for adherence reset calculation for shell patient Vest"+DateTime.now()+","+today);

			List<PatientNoEvent> listPatientNoEventVest =  noEventsRepository.findByModifiedDate(today.toString());			
			
			for(PatientNoEvent patientNoEvent : listPatientNoEventVest){
				
				adherenceCalculationService.adherenceResetForPatient(patientNoEvent.getPatientUser().getId(), patientNoEvent.getPatient().getId(), patientNoEvent.getFirstTransmissionDate(), DEFAULT_COMPLIANCE_SCORE, 0);				
				
			}
			log.debug("Get all Patients for adherence reset calculation for shell patient Monarch"+DateTime.now()+","+today);

			List<PatientNoEventMonarch> listPatientNoEventMonarch =  noEventsMonarchRepository.findByModifiedDate(today.toString());			
			
			for(PatientNoEventMonarch patientNoEvent : listPatientNoEventMonarch){
				
				adherenceCalculationServiceMonarch.adherenceResetForPatient(patientNoEvent.getPatientUser().getId(), patientNoEvent.getPatient().getId(), patientNoEvent.getFirstTransmissionDate(), DEFAULT_COMPLIANCE_SCORE, 0);				
				
			}
			
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotificationMonarch("processDeviceDetailsbyTrainingDate ",writer.toString());
		}
	}

}
