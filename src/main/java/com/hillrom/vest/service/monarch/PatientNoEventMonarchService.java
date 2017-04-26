package com.hillrom.vest.service.monarch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientNoEventMonarch;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientNoEventsRepository;
import com.hillrom.vest.repository.monarch.PatientNoEventsMonarchRepository;
import com.hillrom.vest.util.ExceptionConstants;

@Service
@Transactional
public class PatientNoEventMonarchService {

	@Inject
	public PatientNoEventsMonarchRepository noEventsMonarchRepository;
	
	public PatientNoEventMonarch createIfNotExists(PatientNoEventMonarch newPatientWithNoEvent){
		PatientNoEventMonarch patientNoEvent = noEventsMonarchRepository.findByPatientUserId(newPatientWithNoEvent.getPatientUser().getId());
		if(Objects.nonNull(patientNoEvent)){
			return patientNoEvent;
		}else{
			patientNoEvent = noEventsMonarchRepository.save(newPatientWithNoEvent);
		}
		return patientNoEvent;
	}
	
	public PatientNoEventMonarch updatePatientFirstTransmittedDate(Long patientUserId,LocalDate transmittedDate){
		PatientNoEventMonarch patientNoEvent = noEventsMonarchRepository.findByPatientUserId(patientUserId);
		if(Objects.nonNull(patientNoEvent)){
			patientNoEvent.setFirstTransmissionDate(transmittedDate);
			noEventsMonarchRepository.save(patientNoEvent);
		}
		return patientNoEvent;
	}
	
	public PatientNoEventMonarch findByPatientUserId(Long patientUserId){
		return noEventsMonarchRepository.findByPatientUserId(patientUserId);
	}
	
	public PatientNoEventMonarch findByPatientId(String patientId){
		return noEventsMonarchRepository.findByPatientId(patientId);
	}
	
	public LocalDate getPatientFirstTransmittedDate(Long patientUserId) throws HillromException{
		PatientNoEventMonarch patientNoEvent = noEventsMonarchRepository.findByPatientUserId(patientUserId);
		if(Objects.nonNull(patientNoEvent)){
			return patientNoEvent.getFirstTransmissionDate();
		}
		else 
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
}
