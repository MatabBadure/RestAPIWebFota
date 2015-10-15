package com.hillrom.vest.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.repository.PatientComplianceRepository;

@Service
@Transactional
public class PatientComplianceService {

	@Inject
	private PatientComplianceRepository complianceRepository;
	
	/**
	 * Creates Or Updates Compliance 
	 * @param compliance
	 * @return
	 */
	public PatientCompliance createOrUpdate(PatientCompliance compliance){
		LocalDate date = compliance.getDate();
		Long patientUserId = compliance.getPatientUser().getId();
		PatientCompliance existingCompliance = complianceRepository.findByPatientUserIdAndDate(patientUserId, date);
		if(Objects.nonNull(existingCompliance)){
			existingCompliance.setScore(compliance.getScore());
			existingCompliance.setHmrRunRate(compliance.getHmrRunRate());
			compliance = complianceRepository.save(existingCompliance);
		}else{
			complianceRepository.save(compliance);
		}
		return compliance;
	}
	
	public PatientCompliance findTop1ByDateBeforeAndPatientUserId(LocalDate date,Long patientUserId){
		return complianceRepository.findTop1ByDateBeforeAndPatientUserIdOrderByDateDesc(date, patientUserId);
	}
	
	public SortedMap<LocalDate,PatientCompliance> getPatientComplainceMapByPatientUserId(Long patientUserId){
		List<PatientCompliance> complianceList = complianceRepository.findByPatientUserId(patientUserId);
		SortedMap<LocalDate,PatientCompliance> existingComplainceMap = new TreeMap<>(); 
		for(PatientCompliance compliance : complianceList){
			existingComplainceMap.put(compliance.getDate(),compliance);
		}
		return existingComplainceMap;
	}
	
	/*public SortedMap<LocalDate,PatientCompliance> createOrUpdate(SortedMap<LocalDate,PatientCompliance> complianceMap){
		if(complianceMap.size() > 0){
			LocalDate from = complianceMap.firstKey();
			LocalDate to = complianceMap.lastKey();
			Long patientUserId = complianceMap.get(from).getPatientUser().getId();
			List<Long> patientUserIds = new LinkedList<>();
			patientUserIds.add(patientUserId);
			List<PatientCompliance> complianceList = complianceRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
			for(PatientCompliance compliance : complianceList){
				
			}
		}
		return complianceMap;
	}*/
}
