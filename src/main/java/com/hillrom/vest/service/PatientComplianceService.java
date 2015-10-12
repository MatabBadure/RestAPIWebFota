package com.hillrom.vest.service;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.service.util.DateUtil;

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
	
	public int getMissedTherapyCountByPatientUserId(Long patientUSerId){
		PatientCompliance existingCompliance = complianceRepository.findByPatientUserIdAndDate(patientUSerId,LocalDate.now());
		if (Objects.nonNull(existingCompliance))
			return existingCompliance.getMissedTherapyCount();
		else
			return 0;
	}
}
