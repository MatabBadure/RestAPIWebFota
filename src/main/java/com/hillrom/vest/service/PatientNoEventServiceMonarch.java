package com.hillrom.vest.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientNoEventMonarch;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientNoEventsRepositoryMonarch;
import com.hillrom.vest.util.ExceptionConstants;

@Service
@Transactional
public class PatientNoEventServiceMonarch {

	@Inject
	public PatientNoEventsRepositoryMonarch noEventsRepository;
	
	public PatientNoEventMonarch createIfNotExists(PatientNoEventMonarch newPatientWithNoEvent){
		PatientNoEventMonarch patientNoEvent = noEventsRepository.findByPatientUserId(newPatientWithNoEvent.getPatientUser().getId());
		if(Objects.nonNull(patientNoEvent)){
			return patientNoEvent;
		}else{
			patientNoEvent = noEventsRepository.save(newPatientWithNoEvent);
		}
		return patientNoEvent;
	}
	
	public PatientNoEventMonarch updatePatientFirstTransmittedDate(Long patientUserId,LocalDate transmittedDate){
		PatientNoEventMonarch patientNoEvent = noEventsRepository.findByPatientUserId(patientUserId);
		if(Objects.nonNull(patientNoEvent)){
			patientNoEvent.setFirstTransmissionDate(transmittedDate);
			noEventsRepository.save(patientNoEvent);
		}
		return patientNoEvent;
	}
	
	public PatientNoEventMonarch findByPatientUserId(Long patientUserId){
		return noEventsRepository.findByPatientUserId(patientUserId);
	}
	
	public PatientNoEventMonarch findByPatientId(String patientId){
		return noEventsRepository.findByPatientId(patientId);
	}
	
	public LocalDate getPatientFirstTransmittedDate(Long patientUserId) throws HillromException{
		PatientNoEventMonarch patientNoEvent = noEventsRepository.findByPatientUserId(patientUserId);
		if(Objects.nonNull(patientNoEvent)){
			return patientNoEvent.getFirstTransmissionDate();
		}
		else 
			throw new HillromException(ExceptionConstants.HR_702);
	}
	
	public List<PatientNoEventMonarch> findAll(){
		return noEventsRepository.findAll();
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
		List<PatientNoEventMonarch> patientNoEvents = noEventsRepository.findByPatientUserIdIn(userIdList);
		Map<Long,PatientNoEventMonarch> userIdNoEventsMap = new HashMap<>();
		for(PatientNoEventMonarch patientNoEvent : patientNoEvents){
			userIdNoEventsMap.put(patientNoEvent.getPatientUser().getId(), patientNoEvent);
		}
		return userIdNoEventsMap;
	}
}
