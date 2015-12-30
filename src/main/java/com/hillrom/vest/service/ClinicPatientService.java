package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
import com.hillrom.vest.util.ExceptionConstants;
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
		return sortClinicVOList(clinics);
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
	
	private List<ClinicVO> sortClinicVOList(List<ClinicVO> clinics) {
		return clinics.stream()
				  .sorted((clinicVO1, clinicVO2) -> clinicVO1.getName().compareToIgnoreCase(clinicVO2.getName()))
				  .collect(Collectors.toList());		
	}
}

