package com.hillrom.vest.service;

import java.util.Objects;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.repository.PatientNoEventsRepository;

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
	
	public PatientNoEvent updatePatientFirstTransmittedDate(Long patientUserId,LocalDate transmittedDate){
		PatientNoEvent patientNoEvent = noEventsRepository.findByPatientUserId(patientUserId);
		if(Objects.nonNull(patientNoEvent)){
			patientNoEvent.setFirstTransmissionDate(transmittedDate);
			noEventsRepository.save(patientNoEvent);
		}
		return patientNoEvent;
	}
	
	public PatientNoEvent findByPatientUserId(Long patientUserId){
		return noEventsRepository.findByPatientUserId(patientUserId);
	}
}
