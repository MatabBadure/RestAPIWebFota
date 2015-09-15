package com.hillrom.vest.service;

import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION;

import java.util.ArrayList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.ClinicPatientAssoc;
import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.domain.UserPatientAssocPK;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.NotificationRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;

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
    private NotificationRepository notificationRepository;
    
    @Inject
    private TherapySessionService therapySessionService;

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
			Map<String,List<Notification>> statisticsData = getNotificationsGroupByType(patientUserIds, false, date);
			Map<Long,List<TherapySession>> therapySessions = therapySessionService.getTherapySessionsGroupByPatientUserId(patientUserIds);
			List<Long> patientIdsWithEvent = new LinkedList(therapySessions.keySet());
			patientsWithNoEventRecorded = RandomUtil.getDifference(patientUserIds, patientIdsWithEvent).size();
			if(Objects.nonNull(statisticsData.get(MISSED_THERAPY)))
				statistics.put("patientsWithMissedTherapy", statisticsData.get(MISSED_THERAPY).size());
			if(Objects.nonNull(statisticsData.get(HMR_NON_COMPLIANCE)))
				statistics.put("patientsWithHmrNonCompliance", statisticsData.get(HMR_NON_COMPLIANCE).size());
			if(Objects.nonNull(statisticsData.get(SETTINGS_DEVIATION)))
				statistics.put("patientsWithSettingDeviation", statisticsData.get(SETTINGS_DEVIATION).size());
			statistics.put("patientsWithNoEventRecorded", patientsWithNoEventRecorded);
		}
		return statistics;
	}
	
	public Map<String,List<Notification>> getNotificationsGroupByType(List<Long> patientUserIds,boolean isAcknowledged,LocalDate date){
		List<Notification> notifications = notificationRepository.findByDateAndIsAcknowledgedAndPatientUserIdIn(date, isAcknowledged, patientUserIds);
		return notifications.stream().collect(Collectors.groupingBy(Notification::getNotificationType));
	}
	
	public Map<String,List<Notification>> getNotificationsFilterByType(List<Long> patientUserIds,boolean isAcknowledged,LocalDate date, String type){
		List<Notification> notifications = notificationRepository.findByDateAndIsAcknowledgedAndNotificationTypeAndPatientUserIdIn(date, isAcknowledged, type, patientUserIds);
		return notifications.stream().collect(Collectors.groupingBy(Notification::getNotificationType));
	}
}

