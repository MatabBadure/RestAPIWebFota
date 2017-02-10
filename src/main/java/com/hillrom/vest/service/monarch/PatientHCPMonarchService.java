package com.hillrom.vest.service.monarch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.ClinicPatientAssoc;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientComplianceMonarch;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientNoEventMonarch;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.domain.UserPatientAssocPK;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ClinicPatientRepository;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.PatientNoEventsRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.monarch.PatientComplianceMonarchRepository;
import com.hillrom.vest.repository.monarch.PatientNoEventsMonarchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.HcpClinicsVO;
import com.hillrom.vest.web.rest.dto.PatientComplianceVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.dto.StatisticsVO;
import com.hillrom.vest.web.rest.dto.TreatmentStatisticsVO;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class PatientHCPMonarchService {

    private final Logger log = LoggerFactory.getLogger(PatientHCPMonarchService.class);

    @Inject
    private UserPatientRepository userPatientRepository;

    @Inject
    private UserExtensionRepository userExtensionRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private ClinicMonarchService clinicMonarchService;

    @Inject
    private PatientComplianceMonarchRepository patientComplianceMonarchRepository;

    @Inject
    private PatientNoEventsMonarchRepository noEventsMonarchRepository;
    
    @Inject
    private ClinicPatientRepository clinicPatientRepository;



  


	/**
	 * 
	 * @param clinicId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws HillromException
	 */
   
	
	private List<Long> filterActivePatientIds(
			List<Map<String, Object>> patientUsers) {
		List<Long> patientUserIds = new LinkedList<>();
		patientUsers.forEach(patientUser -> {
			if((Boolean)patientUser.get("status")){
				if(Objects.nonNull(patientUser.get("patient")))
					patientUserIds.add(((UserExtension)patientUser.get("patient")).getId());
			}
		});
		return patientUserIds;
	}

	public List<StatisticsVO> getCumulativePatientStatisticsForClinicAssociatedWithHCP(Long hcpId, String clinicId, 
			LocalDate from,LocalDate to) throws HillromException{
		List<String> clinicList = new LinkedList<>();
		clinicList.add(clinicId);
		List<Map<String,Object>> patientUsers = clinicMonarchService.getAssociatedPatientUsers(clinicList);
		if(patientUsers.isEmpty()) {
			throw new HillromException(MessageConstants.HR_279);
		} else {
			if(to.isEqual(LocalDate.now())){
				to = LocalDate.now().minusDays(1); // HCP, Clinic Admin could see yesterdays data
			}
			List<Long> patientUserIds = filterActivePatientIds(patientUsers);
			return getPatienCumulativeStatistics(from, to, patientUserIds);
		}
	}

	public List<StatisticsVO> getPatienCumulativeStatistics(LocalDate from, LocalDate to,
			List<Long> patientUserIds){
		Map<LocalDate, StatisticsVO> statisticsMap = new TreeMap<>();
		List<PatientComplianceMonarch> complianceList = patientComplianceMonarchRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<LocalDate,Integer> datePatientNoEventCountMap = getPatientsWithNoEvents(from,to,patientUserIds);
		List<LocalDate> requestedDates = DateUtil.getAllLocalDatesBetweenDates(from, to);
		for(LocalDate date : requestedDates){
			int noEventCount = Objects.nonNull(datePatientNoEventCountMap.get(date)) ? datePatientNoEventCountMap.get(date) : 0 ;
			int hmrNonCompliantCount = 0, settingsDeviatedCount = 0,missedTherapyCount = 0;
			for(int i = 0;i<complianceList.size();i++){
				PatientComplianceMonarch compliance = complianceList.get(i);
				if(date.equals(compliance.getDate())){
					if(!compliance.isHmrCompliant())
						hmrNonCompliantCount++;
					if(compliance.isSettingsDeviated())
						settingsDeviatedCount++;
					if(compliance.getMissedTherapyCount() >= 3)
						missedTherapyCount++;
				}
			}
			StatisticsVO statisticsVO = new StatisticsVO(missedTherapyCount, hmrNonCompliantCount, settingsDeviatedCount, noEventCount,
					date,date);
			statisticsMap.put(date, statisticsVO);
		}
		return new LinkedList<>(statisticsMap.values());
	}	
	
	public Map<LocalDate,Integer> getPatientsWithNoEvents(LocalDate from,LocalDate to,List<Long> patientUserIds) {
		List<PatientNoEventMonarch> patients = noEventsMonarchRepository.findByUserCreatedDateBeforeAndPatientUserIdIn(to.plusDays(1),patientUserIds);
		Map<LocalDate,Integer> patientWithNoEventsMap = new HashMap<>(); 
		int count = 0;
		List<LocalDate> requestedDates = DateUtil.getAllLocalDatesBetweenDates(from, to);
		for(int i = 0;i<patients.size();i++){
			PatientNoEventMonarch patientNoEvent = patients.get(i);
			LocalDate firstTransmissionDate = patientNoEvent.getFirstTransmissionDate();
			LocalDate userCreatedDate = patientNoEvent.getUserCreatedDate();
			for(int j = 0;j<requestedDates.size();j++){
				LocalDate requestedDate = requestedDates.get(j);
				count = Objects.nonNull(patientWithNoEventsMap.get(requestedDate))? patientWithNoEventsMap.get(requestedDate):0;
				if(requestedDate.isAfter(userCreatedDate)){
					if(Objects.isNull(firstTransmissionDate)){
						patientWithNoEventsMap.put(requestedDate,++count);
					}else if(requestedDate.isBefore(firstTransmissionDate)){
						patientWithNoEventsMap.put(requestedDate,++count);
					}
				}
			}
		}
		return patientWithNoEventsMap;
	}
	

	
	
}

