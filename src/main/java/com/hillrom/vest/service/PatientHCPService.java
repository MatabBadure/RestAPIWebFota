package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.GROUP_BY_MONTHLY;
import static com.hillrom.vest.config.Constants.GROUP_BY_WEEKLY;
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
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.neo4j.cypher.internal.helpers.Converge.iterateUntilConverged;
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
import com.hillrom.vest.web.rest.dto.StatisticsVO;

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
	
	public Map<String, Integer> getTodaysPatientStatisticsForClinicAssociatedWithHCP(Long hcpId, String clinicId, LocalDate date) throws HillromException{
		Map<String, Integer> statistics = new HashMap();
		List<String> clinicList = new LinkedList<>();
		clinicList.add(clinicId);
		Set<UserExtension> patientUsers = clinicService.getAssociatedPatientUsers(clinicList);
		if(patientUsers.isEmpty()) {
			throw new HillromException(MessageConstants.HR_279);
		} else {
			int patientsWithNoEventRecorded = 0;
			List<Long> patientUserIds = new LinkedList<>();
			patientUsers.forEach(patientUser -> {
				patientUserIds.add(patientUser.getId());
			});
			Map<Long,List<TherapySession>> therapySessions = therapySessionService.getTherapySessionsGroupByPatientUserId(patientUserIds);
			List<Long> patientIdsWithEvent = new LinkedList(therapySessions.keySet());
			patientsWithNoEventRecorded = RandomUtil.getDifference(patientUserIds, patientIdsWithEvent).size();
			statistics.put("patientsWithHmrNonCompliance", patientComplianceRepository.findByDateAndIsHmrCompliantAndPatientUserIdIn(date, false, patientUserIds).size());
			statistics.put("patientsWithSettingDeviation", patientComplianceRepository.findByDateAndIsSettingsDeviatedAndPatientUserIdIn(date, true, patientUserIds).size());
			statistics.put("patientsWithMissedTherapy", patientComplianceRepository.findByDateAndMissedTherapyCountGreaterThanAndPatientUserIdIn(date, 0, patientUserIds).size());
			statistics.put("patientsWithNoEventRecorded", patientsWithNoEventRecorded);
		}
		return statistics;
	}

	public Collection<StatisticsVO> getCumulativePatientStatisticsForClinicAssociatedWithHCP(Long hcpId, String clinicId, LocalDate from,LocalDate to,String groupBy) throws HillromException{
		Map<Integer,StatisticsVO> statisticsMap = new HashMap<>();
		List<String> clinicList = new LinkedList<>();
		clinicList.add(clinicId);
		Set<UserExtension> patientUsers = clinicService.getAssociatedPatientUsers(clinicList);
		if(patientUsers.isEmpty()) {
			throw new HillromException(MessageConstants.HR_279);
		} else {
			List<Long> patientUserIds = new LinkedList<>();
			patientUsers.forEach(patientUser -> {
				patientUserIds.add(patientUser.getId());
			});
			
			statisticsMap = getStatisticsMapByPatientUserIds(from, to, groupBy,patientUserIds);
			
		}
		return statisticsMap.values();
	}

	public Map<Integer, StatisticsVO> getStatisticsMapByPatientUserIds(LocalDate from, LocalDate to,
			String groupBy,List<Long> patientUserIds) {
		Map<Integer, StatisticsVO> statisticsMap = new HashMap<>();
		Map<Integer, List<PatientCompliance>> groupedComplianceMap = getComplianceForPatientUserIdsGroupByDuration(
				from, to, groupBy, patientUserIds);

		Map<Integer,Integer> patientWithNoEventsMap = getPatientsWithNoEvents(from,to,groupBy,patientUserIds);
		
		for(Integer day : groupedComplianceMap.keySet()){
			List<PatientCompliance> complianceListPerDay = groupedComplianceMap.get(day);
			int hmrNonCompliance = 0;
			int settingDeviated = 0;
			int missedTherapy = 0;
			int noEvent = patientWithNoEventsMap.get(day);
			LocalDate complianceStartDate = complianceListPerDay.get(0).getDate();
			LocalDate complainceEndDate =  complianceListPerDay.get(complianceListPerDay.size()-1).getDate();
			for(PatientCompliance compliance: complianceListPerDay){
				if(!compliance.isHmrCompliant()){
					hmrNonCompliance++;
				}
				if(!compliance.isSettingsDeviated()){
					settingDeviated++;
				}
				if(compliance.getMissedTherapyCount() >=3 || compliance.getMissedTherapyCount() % 3 == 0){
					missedTherapy++;
				}
			}
			StatisticsVO statisticsVO = new StatisticsVO(missedTherapy, hmrNonCompliance,
					settingDeviated, noEvent, complianceStartDate,complainceEndDate);
			statisticsMap.put(day, statisticsVO);
		}
		return statisticsMap;
	}

	public Map<Integer, List<PatientCompliance>> getComplianceForPatientUserIdsGroupByDuration(
			LocalDate from, LocalDate to, String groupBy,
			List<Long> patientUserIds) {
		List<PatientCompliance> complianceList = patientComplianceRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);

		Map<Integer,List<PatientCompliance>> groupedComplianceMap = new HashMap<>();
		// Exclude expired and deleted users
		complianceList.forEach(compliance -> {
			if(compliance.getPatient().getExpired()|| compliance.getPatientUser().isDeleted()){
				complianceList.remove(compliance);
			}
		});
		if(GROUP_BY_WEEKLY.equalsIgnoreCase(groupBy)){
			groupedComplianceMap = complianceList.stream().collect(Collectors.groupingBy(PatientCompliance :: getDayOfTheWeek));
		}else if(GROUP_BY_MONTHLY.equalsIgnoreCase(groupBy)){
			groupedComplianceMap = complianceList.stream().collect(Collectors.groupingBy(PatientCompliance :: getWeekOfWeekyear));
		}else if(GROUP_BY_YEARLY.equalsIgnoreCase(groupBy)){
			groupedComplianceMap = complianceList.stream().collect(Collectors.groupingBy(PatientCompliance :: getMonthOfTheYear));
		}
		return groupedComplianceMap;
	}

	public Map<Integer,Integer> getPatientsWithNoEvents(LocalDate from,LocalDate to,String groupBy,List<Long> patientUserIds) {
		List<PatientNoEvent> patients = noEventsRepository.findByUserCreatedDateBeforeAndPatientUserIdIn(to,patientUserIds);
		// Exclude deleted and expired patients
		patients.forEach(patientNoEvent -> {
			if(patientNoEvent.getPatient().getExpired() || patientNoEvent.getPatientUser().isDeleted()){
				patients.remove(patientNoEvent);
			}
		});
		Map<Integer,Integer> patientWithNoEventsMap = new HashMap<>(); 
		int count = 0;
		for(int i = 0;i<patients.size();i++){
			LocalDate firstTransmissionDate = patients.get(i).getFirstTransmissionDate();
			List<LocalDate> requestedDates = DateUtil.getAllLocalDatesBetweenDates(from, to);
			for(int j = 0;j<requestedDates.size();j++){
				LocalDate requestedDate = requestedDates.get(j);
				int requestDateField = 0;
				int transmissionDateField = 0;
				if(GROUP_BY_WEEKLY.equalsIgnoreCase(groupBy)){
					requestDateField = requestedDate.getDayOfWeek();
					transmissionDateField = Objects.nonNull(firstTransmissionDate)?firstTransmissionDate.getDayOfWeek() : 0;
				}else if(GROUP_BY_MONTHLY.equalsIgnoreCase(groupBy)){
					requestDateField = requestedDate.getWeekOfWeekyear();
					transmissionDateField = Objects.nonNull(firstTransmissionDate)?firstTransmissionDate.getWeekOfWeekyear() : 0;
				}else{
					requestDateField = requestedDate.getMonthOfYear();
					transmissionDateField = Objects.nonNull(firstTransmissionDate)?firstTransmissionDate.getMonthOfYear() : 0;
				}
				count = Objects.nonNull(patientWithNoEventsMap.get(requestDateField))? patientWithNoEventsMap.get(requestDateField):0;
				if(Objects.isNull(firstTransmissionDate)){
					patientWithNoEventsMap.put(requestDateField,++count);
				}else if(requestDateField < transmissionDateField){
					patientWithNoEventsMap.put(requestDateField,++count);
				}
			}
		}
		return patientWithNoEventsMap;
	}

}

