package com.hillrom.vest.service;

import static com.hillrom.vest.config.AdherenceScoreConstants.FIRST_TRANSMISSION_FIRTS_TYPE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientDevicesAssocRepository;
import com.hillrom.vest.repository.PatientNoEventsRepository;
import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.util.ExceptionConstants;

@Service
@Transactional
public class PatientNoEventService {

	@Inject
	public PatientNoEventsRepository noEventsRepository;
	
	@Inject
	PatientDevicesAssocRepository patientDevicesAssocRepository;
	
	public PatientNoEvent createIfNotExists(PatientNoEvent newPatientWithNoEvent){
		PatientNoEvent patientNoEvent = noEventsRepository.findByPatientUserId(newPatientWithNoEvent.getPatientUser().getId());
		if(Objects.nonNull(patientNoEvent)){
			return patientNoEvent;
		}else{
		//	patientNoEvent = noEventsRepository.save(newPatientWithNoEvent);
		}
		return patientNoEvent;
	}
	
	public PatientNoEvent updatePatientFirstTransmittedDate(Long patientUserId,LocalDate transmittedDate, String patientId){
		PatientNoEvent patientNoEvent = noEventsRepository.findByPatientUserId(patientUserId);
		
		//GIMP 11
		LocalDate trainingDate = getTrainingDateForAdherence(patientId);
		if(Objects.nonNull(patientNoEvent)){
			if(Objects.nonNull(trainingDate) && trainingDate.isAfter (transmittedDate)){
				patientNoEvent.setFirstTransmissionDate(trainingDate);
				if ((Objects.nonNull(trainingDate)) && (Objects.isNull(patientNoEvent.getFirstTransmissionDateBeforeUpdate()))) {
					patientNoEvent.setFirstTransmissionDateBeforeUpdate(transmittedDate);
					patientNoEvent.setDateFirstTransmissionDateUpdated(new LocalDate());
				}else if((Objects.nonNull(trainingDate)) && (patientNoEvent.getFirstTransmissionDateBeforeUpdate()).isAfter(transmittedDate)){
					patientNoEvent.setFirstTransmissionDateBeforeUpdate(transmittedDate);
					//patientNoEvent.setDateFirstTransmissionDateUpdated(new LocalDate());
				}
			}else if(Objects.nonNull(trainingDate) && trainingDate.equals(transmittedDate)){ 
				//patientNoEvent.setFirstTransmissionDate(transmittedDate);
				if(Objects.isNull(patientNoEvent.getFirstTransmissionDateBeforeUpdate())){
				patientNoEvent.setFirstTransmissionDateBeforeUpdate(transmittedDate);
				}
				//patientNoEvent.setFirstTransDateType(FIRST_TRANSMISSION_FIRTS_TYPE);
			}else if(Objects.nonNull(trainingDate) && trainingDate.isBefore(transmittedDate)){
				patientNoEvent.setFirstTransmissionDate(transmittedDate);
				patientNoEvent.setFirstTransDateType(FIRST_TRANSMISSION_FIRTS_TYPE);
				patientNoEvent.setDateFirstTransmissionDateUpdated(new LocalDate());
			}else{
				patientNoEvent.setFirstTransmissionDate(transmittedDate);
				patientNoEvent.setFirstTransDateType(FIRST_TRANSMISSION_FIRTS_TYPE);
			}
			
			noEventsRepository.save(patientNoEvent);
		}
		
		return patientNoEvent;
	}
	
	//added new method to get training date
	private LocalDate getTrainingDateForAdherence(String id) {
		LocalDate trainingDate = patientDevicesAssocRepository.findOneByPatientIdAndDeviceType(id,"VEST").getTrainingDate();
		return trainingDate;
	}
	
	public PatientNoEvent findByPatientUserId(Long patientUserId){
		return noEventsRepository.findByPatientUserId(patientUserId);
	}
	
	public PatientNoEvent findByPatientId(String patientId){
		return noEventsRepository.findByPatientId(patientId);
	}
	//Modified for GIM 11
	public LocalDate getPatientFirstTransmittedDate(Long patientUserId) throws HillromException{
		PatientNoEvent patientNoEvent = noEventsRepository.findByPatientUserId(patientUserId);
		if(Objects.nonNull(patientNoEvent)){
			
			return GraphUtils.getFirstTransmissionDateVestByType(patientNoEvent);
			
			}
		else 
			throw new HillromException(ExceptionConstants.HR_702);
	}
	
	public List<PatientNoEvent> findAll(){
		return noEventsRepository.findAll();
	}
	
	public Map<Long,PatientNoEvent> findAllGroupByPatientUserId(){
		List<PatientNoEvent> patientNoEvents = findAll();
		Map<Long,PatientNoEvent> userIdNoEventsMap = new HashMap<>();
		for(PatientNoEvent patientNoEvent : patientNoEvents){
			userIdNoEventsMap.put(patientNoEvent.getPatientUser().getId(), patientNoEvent);
		}
		return userIdNoEventsMap;
	}
	
	public Map<Long,PatientNoEvent> findAllByPatientUserId(List<Long> userIdList){
		List<PatientNoEvent> patientNoEvents = noEventsRepository.findByPatientUserIdIn(userIdList);
		Map<Long,PatientNoEvent> userIdNoEventsMap = new HashMap<>();
		for(PatientNoEvent patientNoEvent : patientNoEvents){
			userIdNoEventsMap.put(patientNoEvent.getPatientUser().getId(), patientNoEvent);
		}
		return userIdNoEventsMap;
	}
	public void save(PatientNoEvent patientNoEvent){
		noEventsRepository.save(patientNoEvent);
	}
}
