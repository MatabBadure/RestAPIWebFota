package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.ClinicPatientAssoc;
import com.hillrom.vest.domain.ClinicPatientAssocPK;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.domain.UserPatientAssocPK;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ClinicPatientRepository;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.util.ClinicVOBuilder;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class ClinicPatientService {

    private final Logger log = LoggerFactory.getLogger(ClinicPatientService.class);

    @Inject
    private UserPatientRepository userPatientRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private ClinicRepository clinicRepository;
    
    @Inject
    private ClinicPatientRepository clinicPatientRepository;
    
    @Inject
    private PatientInfoRepository patientInfoRepository;
	
	@Inject
    private UserService userService;
    
    public List<ClinicVO> associateClinicsToPatient(Long id, List<Map<String, String>> clinicList) throws HillromException {
    	User patientUser = userRepository.findOne(id);
    	
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjeFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<ClinicPatientAssoc> clinicPatientAssocList = new ArrayList<>();
	     		List<UserPatientAssoc> userPatientAssocList = new ArrayList<>();
	     		getAssocObjLists(clinicList, patientInfo, clinicPatientAssocList, userPatientAssocList);
		    	clinicPatientAssocList =  clinicPatientRepository.save(clinicPatientAssocList);
		    	userPatientAssocList = userPatientRepository.save(userPatientAssocList);
		    	patientInfo.getClinicPatientAssoc().addAll(clinicPatientAssocList);
		    	patientInfo.getUserPatientAssoc().addAll(userPatientAssocList);
		    	patientInfoRepository.save(patientInfo);
		    	return getClinicVOList(patientInfo);
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	     	}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);//No such user exist
     	}
    	
    }
    
    public List<ClinicVO> getAssociatedClinicsForPatient(Long id) throws HillromException {
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
    		PatientInfo patientInfo = getPatientInfoObjeFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		return getClinicVOList(patientInfo);
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	     	}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);//No such user exist
     	}
    }

	private List<ClinicVO> getClinicVOList(PatientInfo patientInfo) {
		List<Clinic> associatedClinics =  getAssociatedClinicsList(patientInfo);
		List<ClinicVO> clinics = new LinkedList<>();
		for(Clinic clinic : associatedClinics){
			clinics.add(ClinicVOBuilder.build(clinic));
		}
		return RandomUtil.sortClinicVOListByName(clinics);
	}

	private List<Clinic> getAssociatedClinicsList(PatientInfo patientInfo) {
		List<Clinic> clinics = new LinkedList<>();
		for(ClinicPatientAssoc clinicPatientAssoc : patientInfo.getClinicPatientAssoc()){
			clinics.add(clinicPatientAssoc.getClinic());
		}
		return clinics;
	}
	
    public List<ClinicVO> dissociateClinicsToPatient(Long id, List<Map<String, String>> clinicList) throws HillromException {
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjeFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<ClinicPatientAssoc> clinicPatientAssocList = new ArrayList<>();
	     		List<UserPatientAssoc> userPatientAssocList = new ArrayList<>();
		    	getAssocObjLists(clinicList, patientInfo, clinicPatientAssocList, userPatientAssocList);
		    	if (userPatientAssocList.size() > 0) userPatientRepository.delete(userPatientAssocList);
		    	if (clinicPatientAssocList.size() > 0) clinicPatientRepository.delete(clinicPatientAssocList);
		    	return getClinicVOList(patientInfo);
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	     	}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);//No such user exist
     	}
    }
    
    public String dissociateClinicToPatients(String clinicId) throws HillromException {
    
    	//	List<ClinicPatientAssoc> patientClinicAssocList = clinicPatientRepository.findOneByClinicId(id);
    	List<User> userList = getUserListForClinic(clinicId);    	
    	
    	if(userList.isEmpty()){
    		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
    	}else{
    		for(User patientUser : userList ){
    			PatientInfo patientInfo = getPatientInfoObjeFromPatientUser(patientUser);
    			if(patientInfo != null){
    	     		List<ClinicPatientAssoc> clinicPatientAssocList = new ArrayList<>();
    	     		List<UserPatientAssoc> userPatientAssocList = new ArrayList<>();
    	     		getAssocObjLists(clinicId, patientInfo, clinicPatientAssocList, userPatientAssocList);
    		    	if (userPatientAssocList.size() > 0) userPatientRepository.delete(userPatientAssocList);
    		    	if (clinicPatientAssocList.size() > 0) clinicPatientRepository.delete(clinicPatientAssocList);
    		    	return MessageConstants.HR_214;
    		    } else {
    	     		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
    	     	}
    		}
    	}
		return clinicId;   
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

	private void getAssocObjLists(List<Map<String, String>> clinicList,
			PatientInfo patientInfo,
			List<ClinicPatientAssoc> clinicPatientAssocList,
			List<UserPatientAssoc> userPatientAssocList) {
		for(Map<String, String> clinicAssocDetails : clinicList) {
			Clinic clinic = clinicRepository.findOne(clinicAssocDetails.get("id"));
			if(clinic != null) {
				ClinicPatientAssoc clinicPatientAssoc = new ClinicPatientAssoc(new ClinicPatientAssocPK(patientInfo, clinic), clinicAssocDetails.get("mrnId"), clinicAssocDetails.get("notes"));
				clinicPatientAssocList.add(clinicPatientAssoc);
				if (clinic.getClinicAdminId() != null) {
					User clinicAdminUser = userRepository.findOne(clinic.getClinicAdminId());
					if(Objects.nonNull(clinicAdminUser)) {
						UserPatientAssoc clinicAdminPatientAssoc = new UserPatientAssoc(new UserPatientAssocPK(patientInfo, clinicAdminUser), AuthoritiesConstants.CLINIC_ADMIN, RelationshipLabelConstants.CLINIC_ADMIN);
						clinicAdminPatientAssoc.setCreatedBy(SecurityUtils.getCurrentLogin());
						userPatientAssocList.add(clinicAdminPatientAssoc);
					}
				}
			}
		}
	}	
		
	private void getAssocObjLists(String clinicList,
			PatientInfo patientInfo,
			List<ClinicPatientAssoc> clinicPatientAssocList,
			List<UserPatientAssoc> userPatientAssocList) {
		
			
			Clinic clinic = clinicRepository.findOne(clinicList);
			if(clinic != null) {
				ClinicPatientAssoc clinicPatientAssoc = new ClinicPatientAssoc(new ClinicPatientAssocPK(patientInfo, clinic));
				clinicPatientAssocList.add(clinicPatientAssoc);
				if (clinic.getClinicAdminId() != null) {
					User clinicAdminUser = userRepository.findOne(clinic.getClinicAdminId());
					if(Objects.nonNull(clinicAdminUser)) {
						UserPatientAssoc clinicAdminPatientAssoc = new UserPatientAssoc(new UserPatientAssocPK(patientInfo, clinicAdminUser), AuthoritiesConstants.CLINIC_ADMIN, RelationshipLabelConstants.CLINIC_ADMIN);
						clinicAdminPatientAssoc.setCreatedBy(SecurityUtils.getCurrentLogin());
						userPatientAssocList.add(clinicAdminPatientAssoc);
					}
				}			
		}
	}	
   //start:HILL-2004
	/*
		Method invoked from adherence calculation service to get the clinic with the latest adherence setting
	 */
	
	public Clinic getAssociatedClinic(PatientInfo patientInfo) {
		List<Clinic> clinics = new LinkedList<>();
		List<String> clinicIdsList = new LinkedList<>();
		Clinic clinic = null;
		
		// add all patient associtaed clinic information into the clinics list
		for(ClinicPatientAssoc clinicPatientAssoc : patientInfo.getClinicPatientAssoc()){ 
			
			clinics.add(clinicPatientAssoc.getClinic());
		}
	
		// remove objects with modifieddate as null
		clinics.removeIf(o -> o.getAdherenceSettingModifiedDte() == null);
		
		// If all clinics modified date is null send default adherence setting date as null
		if( clinics.size() == 0)
		{
			return clinic;
		}
		// else sort based on modified date, the lkatest modified date need to send as result
		Collections.sort(clinics);
		
		
		return clinics.get(clinics.size()-1);
	
		
	}
	//end:HILL-2004
	
	public List<PatientInfo> getPatientListForClinic(String clinicId) throws HillromException{
		// get the clinic patient association for the clinic
		List<ClinicPatientAssoc> clinicPatientAssocList = clinicPatientRepository.findOneByClinicId(clinicId);
		
		List<PatientInfo> patientList = new LinkedList<>();
		// Check for non null of object
		if(Objects.nonNull(clinicPatientAssocList) && !clinicPatientAssocList.isEmpty()) {			
			for(ClinicPatientAssoc clinicPatientAssoc : clinicPatientAssocList){				
				patientList.add(clinicPatientAssoc.getPatient());				
			}			
		}
		return patientList;
	}
	
	public List<User> getUserListForClinic(String clinicId) throws HillromException{
		// get the clinic patient association for the clinic
		List<ClinicPatientAssoc> clinicPatientAssocList = clinicPatientRepository.findOneByClinicId(clinicId);
		
		List<User> userList = new LinkedList<>();
		// Check for non null of object
		if(Objects.nonNull(clinicPatientAssocList) && !clinicPatientAssocList.isEmpty()) {			
			for(ClinicPatientAssoc clinicPatientAssoc : clinicPatientAssocList){				
				userList.add(userService.getUserObjFromPatientInfo(clinicPatientAssoc.getPatient()));
			}
		}
		
		return userList;
	}
	
	public List<Long> getUserIdListFromUserList(List<User> userList) throws HillromException{
		
		List<Long> userIdList = new LinkedList<>();
		for(User user : userList ){
			userIdList.add(user.getId());
		}
		
		return userIdList;
	}
	
}