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
import com.hillrom.vest.service.ClinicService;
import com.hillrom.vest.service.PatientHCPService;
import com.hillrom.vest.service.TherapySessionService;
import com.hillrom.vest.service.UserService;
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
import com.hillrom.vest.web.rest.dto.monarch.PatientComplianceMonarchVO;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class PatientHCPMonarchService extends PatientHCPService{

    private final Logger log = LoggerFactory.getLogger(PatientHCPMonarchService.class);

    @Inject
    private UserPatientRepository userPatientRepository;

    @Inject
    private UserExtensionRepository userExtensionRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;
    
    @Inject
    private ClinicService clinicService;

    @Inject
    private PatientComplianceMonarchRepository patientComplianceMonarchRepository;
    
    @Inject
    private PatientComplianceRepository patientComplianceRepository;
    
    @Inject
    private TherapySessionServiceMonarch therapySessionMonarchService;
    
    @Inject
    private PatientNoEventsMonarchRepository noEventsMonarchRepository;
    
    @Inject
    private PatientNoEventsRepository noEventsRepository;
    
    @Inject
    private ClinicPatientRepository clinicPatientRepository;

    @Inject
    private PatientHCPService patientHCPService;
	
	
	public Map<String, Object> getTodaysPatientStatisticsForClinicAssociatedWithHCP(String clinicId, LocalDate date) throws HillromException{
		Map<String, Object> statistics = new HashMap();
		List<String> clinicList = new LinkedList<>();
		clinicList.add(clinicId);
		List<Map<String,Object>> patientUsers = clinicService.getAssociatedPatientUsers(clinicList);
		List<Long> patientUserIds = filterActivePatientIds(patientUsers);
		if(patientUsers.isEmpty()) {
	       	Map<String, Object> statistics0 = new HashMap();
				statistics0.put("patientsWithHmrNonCompliance", 0);
				statistics0.put("patientsWithSettingDeviation", 0);
				statistics0.put("patientsWithMissedTherapy", 0);
				statistics0.put("patientsWithNoEventRecorded", 0);
				statistics0.put("date", LocalDate.now());
				statistics0.put("totalPatientCount", 0);        	
	        	return statistics0;
			//throw new HillromException(MessageConstants.HR_279);
		} else if(patientUserIds.isEmpty()) {
			throw new HillromException(MessageConstants.HR_267);
		} else {
			date = LocalDate.now().minusDays(1);// yester days data, HCP and Clinic Admin would see yesterdays data
			Map<LocalDate,Integer> datePatientNoEventCountMap = getPatientsWithNoEvents(date,date,patientUserIds);
			int patientsWithNoEventRecorded = Objects.nonNull(datePatientNoEventCountMap.get(date))? datePatientNoEventCountMap.get(date):0;
			statistics.put("patientsWithHmrNonCompliance", patientComplianceMonarchRepository.findByDateAndIsHmrCompliantAndPatientUserIdIn(date, false, patientUserIds).size());
			statistics.put("patientsWithSettingDeviation", patientComplianceMonarchRepository.findByDateAndIsSettingsDeviatedAndPatientUserIdIn(date, true, patientUserIds).size());
			statistics.put("patientsWithMissedTherapy", patientComplianceMonarchRepository.findByDateAndMissedtherapyAndPatientUserIdIn(date, patientUserIds).size());
			statistics.put("patientsWithNoEventRecorded", patientsWithNoEventRecorded);
			statistics.put("date", date.toString());
			statistics.put("totalPatientCount", patientUserIds.size());
		}
		return statistics;
	}
	
	public Map<String, Object> getTodaysPatientStatisticsForClinicAssociatedWithHCPAll(String clinicId, LocalDate date) throws HillromException{
		Map<String, Object> statistics = new HashMap();
		List<String> clinicList = new LinkedList<>();
		clinicList.add(clinicId);
		List<Map<String,Object>> patientUsers = clinicService.getAssociatedPatientUsers(clinicList);
		List<Long> patientUserIds = filterActivePatientIds(patientUsers);
		if(patientUsers.isEmpty()) {
	       	Map<String, Object> statistics0 = new HashMap();
				statistics0.put("patientsWithHmrNonCompliance", 0);
				statistics0.put("patientsWithSettingDeviation", 0);
				statistics0.put("patientsWithMissedTherapy", 0);
				statistics0.put("patientsWithNoEventRecorded", 0);
				statistics0.put("date", LocalDate.now());
				statistics0.put("totalPatientCount", 0);        	
	        	return statistics0;
			//throw new HillromException(MessageConstants.HR_279);
		} else if(patientUserIds.isEmpty()) {
			throw new HillromException(MessageConstants.HR_267);
		} else {
			date = LocalDate.now().minusDays(1);// yester days data, HCP and Clinic Admin would see yesterdays data
			Map<LocalDate,Integer> datePatientNoEventCountMap = new HashMap<LocalDate, Integer>(getPatientsWithNoEvents(date,date,patientUserIds));
			datePatientNoEventCountMap.putAll(patientHCPService.getPatientsWithNoEvents(date, date, patientUserIds));
			int patientsWithNoEventRecorded = Objects.nonNull(datePatientNoEventCountMap.get(date))? datePatientNoEventCountMap.get(date):0;
			int patientsWithHmrNonCompliance = patientComplianceMonarchRepository.findByDateAndIsHmrCompliantAndPatientUserIdIn(date, false, patientUserIds).size() + patientComplianceRepository.findByDateAndIsHmrCompliantAndPatientUserIdIn(date, false, patientUserIds).size();
			int patientsWithSettingDeviation = patientComplianceMonarchRepository.findByDateAndIsSettingsDeviatedAndPatientUserIdIn(date, true, patientUserIds).size() + patientComplianceRepository.findByDateAndIsSettingsDeviatedAndPatientUserIdIn(date, true, patientUserIds).size();
			int patientsWithMissedTherapy =  patientComplianceMonarchRepository.findByDateAndMissedtherapyAndPatientUserIdIn(date, patientUserIds).size() + patientComplianceRepository.findByDateAndMissedtherapyAndPatientUserIdIn(date, patientUserIds).size();
			statistics.put("patientsWithHmrNonCompliance", patientsWithHmrNonCompliance);
			statistics.put("patientsWithSettingDeviation", patientsWithSettingDeviation);
			statistics.put("patientsWithMissedTherapy", patientsWithMissedTherapy);
			statistics.put("patientsWithNoEventRecorded", patientsWithNoEventRecorded);
			statistics.put("date", date.toString());
			statistics.put("totalPatientCount", patientUserIds.size());
		}
		return statistics;
	}

	
	/**
	 * 
	 * @param clinicId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws HillromException
	 */
    public Map<String, Object> getTodaysPatientStatisticsForClinicAssociatedWithHCP(String clinicId, LocalDate startDate,LocalDate endDate) throws HillromException{
    	
    	    log.debug(" Entering getTodaysPatientStatisticsForClinicAssociatedWithHCP ");
               Map<String, Object> statistics = new HashMap();
           List<String> clinicList = new LinkedList<>();
           clinicList.add(clinicId);
           List<Map<String,Object>> patientUsers = clinicService.getAssociatedPatientUsers(clinicList);
           List<Long> patientUserIds = filterActivePatientIds(patientUsers);
           if(patientUsers.isEmpty()) {
                  Map<String, Object> statistics0 = new HashMap();
                        statistics0.put("patientsWithHmrNonComplianceRange", 0);
                        statistics0.put("patientsWithSettingDeviationRange", 0);
                        statistics0.put("patientsWithMissedTherapyRange", 0);
                        statistics0.put("patientsWithNoEventRecordedRange", 0);
                        statistics0.put("dateRange", LocalDate.now());
                        statistics0.put("totalPatientCountRange", 0);          
                  return statistics0;
                  //throw new HillromException(MessageConstants.HR_279);
           } else if(patientUserIds.isEmpty()) {
                  throw new HillromException(MessageConstants.HR_267);
           } else {
                  
                 /* startDate = startDate.minusDays(1);
                  endDate = endDate.minusDays(1);*/
                  
                  Map<LocalDate,Integer> datePatientNoEventCountMap = getPatientsWithNoEvents(startDate,endDate,patientUserIds);
                  int patientsWithNoEventRecorded = 0;
                  for(LocalDate lDate : DateUtil.getAllLocalDatesBetweenDates(startDate, endDate)){
                        int patientsWithNoEventRecordedtemp = Objects.nonNull(datePatientNoEventCountMap.get(lDate))? datePatientNoEventCountMap.get(lDate):0;
                        patientsWithNoEventRecorded = patientsWithNoEventRecorded + patientsWithNoEventRecordedtemp;
                  }
                        
                  int patientsWithHMRNonCompliance = patientComplianceMonarchRepository.findByDateBetweenAndIsHmrCompliantAndPatientUserIdIn(startDate,endDate, false, patientUserIds).size();
                  
                  int patientsWithSettingsDeviation = patientComplianceMonarchRepository.findByDateBetweenAndIsSettingsDeviatedAndPatientUserIdIn(startDate,endDate, true, patientUserIds).size();
                        
                  int patientsWithMissedTherapy = patientComplianceMonarchRepository.findByDateBetweenAndMissedtherapyAndPatientUserIdIn(startDate,endDate, patientUserIds).size();
                  
                  statistics.put("patientsWithHmrNonComplianceRange", patientsWithHMRNonCompliance);
                  statistics.put("patientsWithSettingDeviationRange", patientsWithSettingsDeviation);
                  statistics.put("patientsWithMissedTherapyRange", patientsWithMissedTherapy);
                  statistics.put("patientsWithNoEventRecordedRange", patientsWithNoEventRecorded);
                  statistics.put("dateRange", "Start Date " + startDate.toString() + " : " + " End Date " + endDate.toString());
                  statistics.put("totalPatientCountRange", patientUserIds.size());
           }
           log.debug(" Exit getTodaysPatientStatisticsForClinicAssociatedWithHCP() ");
           
           return statistics;
    }
	


	private List<Long> getPatientsListWithNoEventRecorded(List<Long> patientUserIds) {
		List<PatientNoEventMonarch> allPatients = noEventsMonarchRepository
				.findByUserCreatedDateBeforeAndPatientUserIdIn(LocalDate.now()
						.plusDays(1), patientUserIds);
		List<PatientNoEventMonarch> patientsWithNoEvent = allPatients
				.stream()
				.filter(patient -> Objects.isNull(patient
						.getFirstTransmissionDate()))
				.collect(Collectors.toList());
		List<Long> patientWithNoEventIds = new LinkedList<>();
		for (PatientNoEventMonarch noEventPatient : patientsWithNoEvent) {
			patientWithNoEventIds.add(noEventPatient.getPatientUser().getId());
		}
		return patientWithNoEventIds;
	}
	
	public List<PatientComplianceMonarchVO> getPatientListFilterByMetricForClinicMonarchAssociated(Long userId, String clinicId, LocalDate date, String filterBy) throws HillromException{
		List<PatientComplianceMonarchVO> patientComplianceVOList = new LinkedList<>();
		List<String> clinicList = new LinkedList<>();
		clinicList.add(clinicId);
		List<Map<String,Object>> patientUsers = clinicService.getAssociatedPatientUsers(clinicList);
		if(patientUsers.isEmpty()) {
			throw new HillromException(MessageConstants.HR_279);
		} else {
			List<Long> patientUserIds = filterActivePatientIds(patientUsers);
			List<PatientComplianceMonarch> patientCompliances = new LinkedList<>();
			if(Constants.MISSED_THERAPY.equals(filterBy)){
				patientCompliances = patientComplianceMonarchRepository.findByDateAndMissedtherapyAndPatientUserIdIn(date, patientUserIds);
			} else if(Constants.NON_HMR_COMPLIANCE.equals(filterBy)){
				patientCompliances = patientComplianceMonarchRepository.findByDateAndIsHmrCompliantAndPatientUserIdIn(date, false, patientUserIds);
			} else if(Constants.SETTING_DEVIATION.equals(filterBy)){
				patientCompliances = patientComplianceMonarchRepository.findByDateAndIsSettingsDeviatedAndPatientUserIdIn(date, true, patientUserIds);
			} else {
				throw new HillromException(ExceptionConstants.HR_554);
			}
			for(PatientComplianceMonarch pCompliance : patientCompliances) {
				PatientComplianceMonarchVO complianceVO = new PatientComplianceMonarchVO();
				complianceVO.setPatientCompMonarch(pCompliance);
				for(Map<String,Object> patientUser : patientUsers) {
					if(pCompliance.getPatientUser().getId().equals(((UserExtension)patientUser.get("patient")).getId())){
						complianceVO.setHcp((UserExtension)patientUser.get("hcp"));
						complianceVO.setMrnId((String)patientUser.get("mrnId"));
					}
				}
				patientComplianceVOList.add(complianceVO);
			}
			return patientComplianceVOList;
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
	
	
	public Map<Long, List<PatientComplianceMonarch>> getComplianceMonarchGroupByPatientUserId(
			LocalDate from, LocalDate to, List<Long> patientUserIds) {
		List<PatientComplianceMonarch> complianceList = patientComplianceMonarchRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<Long,List<PatientComplianceMonarch>> complianceGroupByPatient = new HashMap<>();
		for(PatientComplianceMonarch compliance : complianceList){
			Long patientUserId = compliance.getPatientUser().getId();
			List<PatientComplianceMonarch> compliances = complianceGroupByPatient.get(compliance.getPatientUser().getId());
			if(Objects.isNull(compliances))
				compliances = new LinkedList<>();
			compliances.add(compliance);
			complianceGroupByPatient.put(patientUserId, compliances);
		}
		return complianceGroupByPatient;
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
	
	public Collection<TreatmentStatisticsVO> getTreatmentStatisticsForClinicAssociatedWithHCP(
			Long hcpId, String clinicId, LocalDate from, LocalDate to) throws HillromException {
		List<String> clinicList = new LinkedList<>();
		clinicList.add(clinicId);
		List<Map<String,Object>> patientUsers = clinicService.getAssociatedPatientUsers(clinicList);
		if(patientUsers.isEmpty()) {
			throw new HillromException(MessageConstants.HR_279);
		} else {
			List<Long> patientUserIds = filterActivePatientIds(patientUsers);
			return therapySessionMonarchService.getTreatmentStatisticsByPatientUserIdsAndDuration(patientUserIds,from,to);
		}
	}
	

	
}

