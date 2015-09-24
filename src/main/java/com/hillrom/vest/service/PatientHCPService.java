package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.GROUP_BY_MONTHLY;
import static com.hillrom.vest.config.Constants.GROUP_BY_YEARLY;

import java.util.ArrayList;
import java.util.Collection;
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
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.domain.UserPatientAssocPK;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.PatientNoEventsRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.PatientComplianceVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.dto.StatisticsVO;
import com.hillrom.vest.web.rest.dto.TreatmentStatisticsVO;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class PatientHCPService {

    private final Logger log = LoggerFactory.getLogger(PatientHCPService.class);

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
    private PatientComplianceRepository patientComplianceRepository;
    
    @Inject
    private TherapySessionService therapySessionService;
    
    @Inject
    private PatientNoEventsRepository noEventsRepository;

    public List<User> associateHCPToPatient(Long id, List<Map<String, String>> hcpList) throws HillromException {
    	List<User> users = new LinkedList<>();
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjeFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<UserPatientAssoc> hcpPatientAssocList = new ArrayList<>();
		    	for(Map<String, String> hcpId : hcpList) {
		    		UserExtension hcpUser = userExtensionRepository.findOne(Long.parseLong(hcpId.get("id")));
		    		if(hcpUser != null) {
			    		UserPatientAssoc userPatientAssoc = new UserPatientAssoc(new UserPatientAssocPK(patientInfo, hcpUser), AuthoritiesConstants.HCP, RelationshipLabelConstants.HCP);
			    		hcpPatientAssocList.add(userPatientAssoc);
		    		} else {
		    			throw new HillromException(ExceptionConstants.HR_532);//Invalid HCP id
		    		}
		    	}
		    	userPatientRepository.save(hcpPatientAssocList);
		    	userPatientRepository.flush();
		    	users = getAssociatedHCPUserList(patientInfo);
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	     	}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);//No such user exist
     	}
    	return users;
    }

    public List<User> getAssociatedHCPUserForPatient(Long id) throws HillromException {
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
    		PatientInfo patientInfo = getPatientInfoObjeFromPatientUser(patientUser);
	     	if(patientInfo != null){
		    	return getAssociatedHCPUserList(patientInfo);
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	     	}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);//No such user exist
     	}
    }

	private List<User> getAssociatedHCPUserList(PatientInfo patientInfo) {
		List<User> hcpUsers = new LinkedList<>();
		for(UserPatientAssoc userPatientAssoc : patientInfo.getUserPatientAssoc()){
			if(AuthoritiesConstants.HCP.equals(userPatientAssoc.getUserRole())){
				hcpUsers.add(userPatientAssoc.getUser());
			}
		}
		return hcpUsers;
	}

	public List<Map<String,Object>> getAssociatedPatientUsersForHCP(Long id, String filterByClinicId) throws HillromException {
    	UserExtension hcpUser = userExtensionRepository.findOne(id);
    	if(hcpUser != null) {
    		List<PatientInfo> patientList = new LinkedList<>();
	     	for(UserPatientAssoc patientAssoc : hcpUser.getUserPatientAssoc()){
	    		if(RelationshipLabelConstants.HCP.equals(patientAssoc.getRelationshipLabel())){
	    			patientList.add(patientAssoc.getPatient());
	    		}
	    	}
	     	if(!patientList.isEmpty()){
	     		List<Map<String,Object>> responseList = new LinkedList<Map<String,Object>>();
	     		patientList.forEach(patientInfo -> {
	     			Map<String, Object> entity = new HashMap<>();
	     			Set<ClinicPatientAssoc> filteredList = new HashSet<>();
	     			if(!Constants.ALL.equals(filterByClinicId)) {
	     				if(!patientInfo.getClinicPatientAssoc().isEmpty()) {
		     				filteredList = (Set<ClinicPatientAssoc>) patientInfo.getClinicPatientAssoc().stream()
		     						.filter(clinicPatientAssoc
		     								-> clinicPatientAssoc.getClinic()
		     								.getId().equals(filterByClinicId))
		     								.collect(Collectors.toSet());
		     			}
	     			} else {
	     				filteredList = patientInfo.getClinicPatientAssoc();
	     			}
	     			List<Clinic> clinics = new LinkedList<>();
	     			filteredList.forEach(cpa -> {
	     				clinics.add(cpa.getClinic());
	     			});
	     			if(!Constants.ALL.equals(filterByClinicId)) {
		     			if(!clinics.isEmpty()) {
		     				entity.put("patientInfo", patientInfo);
			     			entity.put("patientUser", userService.getUserObjFromPatientInfo(patientInfo));
			     			entity.put("clinics", clinics);
			     			responseList.add(entity);
		     			}
	     			} else {
	     				entity.put("patientInfo", patientInfo);
		     			entity.put("patientUser", userService.getUserObjFromPatientInfo(patientInfo));
		     			entity.put("clinics", clinics);
		     			responseList.add(entity);
	     			}
	     		});
		    	return responseList;
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_581);
	     	}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);
     	}
    }
	
	public String dissociateHCPFromPatient(Long id, List<Map<String, String>> hcpList) throws HillromException {
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjeFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<UserPatientAssoc> hcpPatientAssocList = new ArrayList<>();
		    	for(Map<String, String> hcpId : hcpList) {
		    		UserExtension hcpUser = userExtensionRepository.findOne(Long.parseLong(hcpId.get("id")));
		    		if(hcpUser != null) {
			    		UserPatientAssoc userPatientAssoc = new UserPatientAssoc(new UserPatientAssocPK(patientInfo, hcpUser), AuthoritiesConstants.HCP, RelationshipLabelConstants.HCP);
			    		hcpPatientAssocList.add(userPatientAssoc);
		    		} else {
		    			throw new HillromException(ExceptionConstants.HR_532);//Invalid HCP id
		    		}
		    	}
		    	userPatientRepository.delete(hcpPatientAssocList);
		    	return MessageConstants.HR_256;
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	     	}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);//No such user exist
     	}
    }
	
	private PatientInfo getPatientInfoObjeFromPatientUser(User patientUser) {
		PatientInfo patientInfo = null;
		for(UserPatientAssoc patientAssoc : patientUser.getUserPatientAssoc()){
			if(RelationshipLabelConstants.SELF.equals(patientAssoc.getRelationshipLabel())){
				patientInfo = patientAssoc.getPatient();
			}
		}
		return patientInfo;
	}
	
	public Map<String, Object> getTodaysPatientStatisticsForClinicAssociatedWithHCP(String clinicId, LocalDate date) throws HillromException{
		Map<String, Object> statistics = new HashMap();
		List<String> clinicList = new LinkedList<>();
		clinicList.add(clinicId);
		List<Map<String,Object>> patientUsers = clinicService.getAssociatedPatientUsers(clinicList);
		if(patientUsers.isEmpty()) {
			throw new HillromException(MessageConstants.HR_279);
		} else {
			int patientsWithNoEventRecorded = 0;
			List<Long> patientUserIds = new LinkedList<>();
			patientUsers.forEach(patientUser -> {
				patientUserIds.add(((UserExtension)patientUser.get("patient")).getId());
			});
			patientsWithNoEventRecorded = getPatientsListWithNoEventRecorded(patientUserIds).size();
			statistics.put("patientsWithHmrNonCompliance", patientComplianceRepository.findByDateAndIsHmrCompliantAndPatientUserIdIn(date, false, patientUserIds).size());
			statistics.put("patientsWithSettingDeviation", patientComplianceRepository.findByDateAndIsSettingsDeviatedAndPatientUserIdIn(date, true, patientUserIds).size());
			statistics.put("patientsWithMissedTherapy", patientComplianceRepository.findByDateAndMissedtherapyAndPatientUserIdIn(date,patientUserIds).size());
			statistics.put("patientsWithNoEventRecorded", patientsWithNoEventRecorded);
			statistics.put("date", date.toString());
			statistics.put("totalPatientCount", patientUsers.size());
		}
		return statistics;
	}

	private List<Long> getPatientsListWithNoEventRecorded(List<Long> patientUserIds) {
		Map<Long,List<TherapySession>> therapySessions = therapySessionService.getTherapySessionsGroupByPatientUserId(patientUserIds);
		List<Long> patientIdsWithEvent = new LinkedList(therapySessions.keySet());
		return RandomUtil.getDifference(patientUserIds, patientIdsWithEvent);
	}
	
	public List<PatientComplianceVO> getPatientListFilterByMetricForClinicAssociated(Long userId, String clinicId, LocalDate date, String filterBy) throws HillromException{
		List<PatientComplianceVO> patientComplianceVOList = new LinkedList<>();
		List<String> clinicList = new LinkedList<>();
		clinicList.add(clinicId);
		List<Map<String,Object>> patientUsers = clinicService.getAssociatedPatientUsers(clinicList);
		if(patientUsers.isEmpty()) {
			throw new HillromException(MessageConstants.HR_279);
		} else {
			List<Long> patientUserIds = new LinkedList<>();
			patientUsers.forEach(patientUser -> {
				patientUserIds.add(((UserExtension)patientUser.get("patient")).getId());
			});
			List<PatientCompliance> patientCompliances = new LinkedList<>();
			if(Constants.MISSED_THERAPY.equals(filterBy)){
				patientCompliances = patientComplianceRepository.findByDateAndMissedtherapyAndPatientUserIdIn(date,patientUserIds);
			} else if(Constants.NON_HMR_COMPLIANCE.equals(filterBy)){
				patientCompliances = patientComplianceRepository.findByDateAndIsHmrCompliantAndPatientUserIdIn(date, false, patientUserIds);
			} else if(Constants.SETTING_DEVIATION.equals(filterBy)){
				patientCompliances = patientComplianceRepository.findByDateAndIsSettingsDeviatedAndPatientUserIdIn(date, true, patientUserIds);
			} else {
				throw new HillromException(ExceptionConstants.HR_554);
			}
			for(PatientCompliance pCompliance : patientCompliances) {
				for(Map<String,Object> patientUser : patientUsers) {
					if(pCompliance.getPatientUser().getId().equals(((UserExtension)patientUser.get("patient")).getId())){
						patientComplianceVOList.add(new PatientComplianceVO(pCompliance, (UserExtension)patientUser.get("hcp")));
					}
				}
			}
			return patientComplianceVOList;
		}
	}
	
	public List<PatientUserVO> getPatientsWithNoEventsForClinicAssociated(Long userId, String clinicId, LocalDate date) throws HillromException{
		List<PatientUserVO> patientList = new LinkedList<>();
		List<String> clinicList = new LinkedList<>();
		clinicList.add(clinicId);
		List<Map<String,Object>> patientUsers = clinicService.getAssociatedPatientUsers(clinicList);
		if(patientUsers.isEmpty()) {
			throw new HillromException(MessageConstants.HR_279);
		} else {
			List<Long> patientUserIds = new LinkedList<>();
			patientUsers.forEach(patientUser -> {
				patientUserIds.add(((UserExtension)patientUser.get("patient")).getId());
			});
			List<Long> pList = getPatientsListWithNoEventRecorded(patientUserIds);
			List<User> patientUserList = userRepository.findAll(pList);
			for(User patientUser : patientUserList) {
				for(Map<String,Object> pUser : patientUsers) {
					if(patientUser.getId().equals(((UserExtension)pUser.get("patient")).getId())){
						PatientUserVO patientUserVO = new PatientUserVO((UserExtension) patientUser, userService.getPatientInfoObjFromPatientUser(patientUser));
						patientUserVO.setAdherence(0);
						patientUserVO.setHcp((UserExtension)pUser.get("hcp"));
						patientList.add(patientUserVO);
					}
				}
			}
			return patientList;
		}
	}
	
	public Collection<StatisticsVO> getCumulativePatientStatisticsForClinicAssociatedWithHCP(Long hcpId, String clinicId, 
			LocalDate from,LocalDate to) throws HillromException{
		List<String> clinicList = new LinkedList<>();
		clinicList.add(clinicId);
		List<Map<String,Object>> patientUsers = clinicService.getAssociatedPatientUsers(clinicList);
		if(patientUsers.isEmpty()) {
			throw new HillromException(MessageConstants.HR_279);
		} else {
			List<Long> patientUserIds = new LinkedList<>();
			patientUsers.forEach(patientUser -> {
				if(!((UserExtension)patientUser.get("patient")).isDeleted() && ((UserExtension)patientUser.get("patient")).getActivated()){ // Excluding deleted/inactive users
					patientUserIds.add(((UserExtension)patientUser.get("patient")).getId());
				}
			});
			return getPatienCumulativeStatistics(from, to, patientUserIds);
		}
	}

	public Collection<StatisticsVO> getPatienCumulativeStatistics(LocalDate from, LocalDate to,
			List<Long> patientUserIds){
		Map<LocalDate, StatisticsVO> statisticsMap = new TreeMap<>();
		List<PatientCompliance> complianceList = patientComplianceRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<LocalDate,Integer> datePatientNoEventCountMap = getPatientsWithNoEvents(from,to,patientUserIds);
		List<LocalDate> requestedDates = DateUtil.getAllLocalDatesBetweenDates(from, to);
		for(LocalDate date : requestedDates){
			int noEventCount = Objects.nonNull(datePatientNoEventCountMap.get(date)) ? datePatientNoEventCountMap.get(date) : 0 ;
			int hmrNonCompliantCount = 0, settingsDeviatedCount = 0,missedTherapyCount = 0;
			for(int i = 0;i<complianceList.size();i++){
				PatientCompliance compliance = complianceList.get(i);
				if(date.equals(compliance.getDate())){
					if(!compliance.isHmrCompliant())
						hmrNonCompliantCount++;
					if(!compliance.isSettingsDeviated())
						settingsDeviatedCount++;
					if(compliance.getMissedTherapyCount() > 0 && compliance.getMissedTherapyCount() % 3 == 0)
						missedTherapyCount++;
				}
			}
			StatisticsVO statisticsVO = new StatisticsVO(missedTherapyCount, hmrNonCompliantCount, settingsDeviatedCount, noEventCount,
					date,date);
			statisticsMap.put(date, statisticsVO);
		}
		return statisticsMap.values();
	}
	
	
	public Map<Long, List<PatientCompliance>> getComplianceGroupByPatientUserId(
			LocalDate from, LocalDate to, List<Long> patientUserIds) {
		List<PatientCompliance> complianceList = patientComplianceRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<Long,List<PatientCompliance>> complianceGroupByPatient = new HashMap<>();
		for(PatientCompliance compliance : complianceList){
			Long patientUserId = compliance.getPatientUser().getId();
			List<PatientCompliance> compliances = complianceGroupByPatient.get(compliance.getPatientUser().getId());
			if(Objects.isNull(compliances))
				compliances = new LinkedList<>();
			compliances.add(compliance);
			complianceGroupByPatient.put(patientUserId, compliances);
		}
		return complianceGroupByPatient;
	}
	
	public Map<LocalDate,Integer> getPatientsWithNoEvents(LocalDate from,LocalDate to,List<Long> patientUserIds) {
		List<PatientNoEvent> patients = noEventsRepository.findByUserCreatedDateBeforeAndPatientUserIdIn(to.plusDays(1),patientUserIds);
		Map<LocalDate,Integer> patientWithNoEventsMap = new HashMap<>(); 
		int count = 0;
		List<LocalDate> requestedDates = DateUtil.getAllLocalDatesBetweenDates(from, to);
		for(int i = 0;i<patients.size();i++){
			PatientNoEvent patientNoEvent = patients.get(i);
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
			List<Long> patientUserIds = new LinkedList<>();
			patientUsers.forEach(patientUser -> {
				if(!((UserExtension)patientUser.get("patient")).isDeleted() && ((UserExtension)patientUser.get("patient")).getActivated()){ // Excluding deleted/inactive users
					patientUserIds.add(((UserExtension)patientUser.get("patient")).getId());
				}
			});
			return therapySessionService.getTreatmentStatisticsByPatientUserIdsAndDuration(patientUserIds,from,to);
		}
	}
	
	public Set<UserExtension> getAssociatedHCPUsersForPatient(Long patientId, String filterByClinicId) throws HillromException {
    	UserExtension patientUser = userExtensionRepository.findOne(patientId);
    	if(Objects.nonNull(patientUser)) {
    		PatientInfo patientInfo = getPatientInfoObjeFromPatientUser(patientUser);
    		if(Objects.nonNull(patientInfo)){
    			List<UserExtension> hcpList = new LinkedList<>();
    	     	for(UserPatientAssoc patientAssoc : patientInfo.getUserPatientAssoc()){
    	    		if(AuthoritiesConstants.HCP.equals(patientAssoc.getUserRole())){
    	    			hcpList.add((UserExtension)patientAssoc.getUser());
    	    		}
    	    	}
    	     	if(!hcpList.isEmpty()){
    	     		Set<UserExtension> filteredList = new HashSet<>();
    	     		hcpList.forEach(hcpUser -> {
	     				if(!hcpUser.getClinics().isEmpty()) {
		     				hcpUser.getClinics().forEach(clinic -> {
		     					if(clinic.getId().equals(filterByClinicId)){
		     						filteredList.add(hcpUser);
		     					}
		     				});
		     			}
    	     		});
    		    	return filteredList;
    	     	} else {
    	     		throw new HillromException(ExceptionConstants.HR_588);
    	     	}
    		} else {
    			throw new HillromException(ExceptionConstants.HR_523);
    		}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);
     	}
    }
}

