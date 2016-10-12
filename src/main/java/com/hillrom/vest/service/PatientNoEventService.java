package com.hillrom.vest.service;

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
import com.hillrom.vest.repository.PatientNoEventsRepository;
import com.hillrom.vest.util.ExceptionConstants;
import static com.hillrom.vest.config.AdherenceScoreConstants.OLD_TRAINING_DATE;

@Service
@Transactional
public class PatientNoEventService {

	@Inject
	public PatientNoEventsRepository noEventsRepository;
	
	public PatientNoEvent createIfNotExists(PatientNoEvent newPatientWithNoEvent){
		PatientNoEvent patientNoEvent = noEventsRepository.findByPatientUserId(newPatientWithNoEvent.getPatientUser().getId());
		if(Objects.nonNull(patientNoEvent)){
			return patientNoEvent;
		}else{
			patientNoEvent = noEventsRepository.save(newPatientWithNoEvent);
		}
		return patientNoEvent;
	}
	
	// Hill Rom Changes
	public PatientNoEvent updatePatientFirstTransmittedDate(LocalDate firstTransmittedDate, Long patientUserId,LocalDate transmittedDate){
		PatientNoEvent patientNoEvent = noEventsRepository.findByPatientUserId(patientUserId);
		// Hill Rom Changes
		if(Objects.nonNull(patientNoEvent)){
			// Hill Rom Changes
			if(firstTransmittedDate.isBefore(LocalDate.now().minusYears(OLD_TRAINING_DATE)))
				{
					patientNoEvent.setFirstTransmissionDate(transmittedDate);
				}
			else
				{
					patientNoEvent.setFirstTransmissionDate(firstTransmittedDate);
				}
			// Hill Rom Changes
			
			noEventsRepository.save(patientNoEvent);
		}
		return patientNoEvent;
	}
	
	public PatientNoEvent findByPatientUserId(Long patientUserId){
		return noEventsRepository.findByPatientUserId(patientUserId);
	}
	
	public PatientNoEvent findByPatientId(String patientId){
		return noEventsRepository.findByPatientId(patientId);
	}
	
	public LocalDate getPatientFirstTransmittedDate(Long patientUserId) throws HillromException{
		PatientNoEvent patientNoEvent = noEventsRepository.findByPatientUserId(patientUserId);
		if(Objects.nonNull(patientNoEvent)){
			return patientNoEvent.getFirstTransmissionDate();
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
}
