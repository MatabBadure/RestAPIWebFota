package com.hillrom.vest.service;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientProtocolRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class PatientProtocolService {

    private final Logger log = LoggerFactory.getLogger(PatientProtocolService.class);

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private PatientProtocolRepository patientProtocolRepository;
    
    public PatientProtocolData addProtocolToPatient(Long id, Map<String, String> protocolData) throws HillromException {
    	User patientUser = userRepository.findOne(id);
		if(patientUser != null) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
		 	if(patientInfo != null){
		 		PatientProtocolData patientProtocolAssoc = new PatientProtocolData(patientInfo, 
		 				Integer.parseInt(protocolData.get("treatmentsPerDay")), Integer.parseInt(protocolData.get("minutesPerTreatment")), 
		 				protocolData.get("frequencies"), Integer.parseInt(protocolData.get("minimumMinutesOfUsePerDay")));
		 		patientProtocolRepository.save(patientProtocolAssoc);
		 		return patientProtocolAssoc;
		 	} else {
		 		throw new HillromException(ExceptionConstants.HR_523);
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
		}
    }
    
    public PatientProtocolData updateProtocolToPatient(Long id, Map<String, String> protocolData) throws HillromException {
    	User patientUser = userRepository.findOne(id);
		if(patientUser != null) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
		 	if(patientInfo != null){
		 		Optional<PatientProtocolData> currentAssoc = patientProtocolRepository.findOneByPatientId(patientInfo.getId());
		 		if(currentAssoc.isPresent()){
		 			assignValuesToPatientProtocolObj(protocolData, currentAssoc);
	 				patientProtocolRepository.save(currentAssoc.get());
		 		}
		 		return currentAssoc.get();
		 	} else {
		 		throw new HillromException(ExceptionConstants.HR_523);
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
		}
    }

	private void assignValuesToPatientProtocolObj(
			Map<String, String> protocolData,
			Optional<PatientProtocolData> currentAssoc) {
		if(StringUtils.isNoneEmpty(protocolData.get("treatmentsPerDay")))
			currentAssoc.get().setTreatmentsPerDay(Integer.parseInt(protocolData.get("treatmentsPerDay")));
		if(StringUtils.isNoneEmpty(protocolData.get("minutesPerTreatment")))
			currentAssoc.get().setMinutesPerTreatment(Integer.parseInt(protocolData.get("minutesPerTreatment")));
		if(StringUtils.isNoneEmpty(protocolData.get("frequencies")))
			currentAssoc.get().setFrequencies(protocolData.get("frequencies"));
		if(StringUtils.isNoneEmpty(protocolData.get("minimumMinutesOfUsePerDay")))
			currentAssoc.get().setMinimumMinutesOfUsePerDay(Integer.parseInt(protocolData.get("minimumMinutesOfUsePerDay")));
	}

    public PatientProtocolData getProtocolsAssociatedWithPatient(Long id) throws HillromException {
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		return patientProtocolRepository.findOneByPatientId(patientInfo.getId()).get();
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
     	}
    }

	private PatientInfo getPatientInfoObjFromPatientUser(User patientUser) {
		PatientInfo patientInfo = null;
		for(UserPatientAssoc patientAssoc : patientUser.getUserPatientAssoc()){
			if(RelationshipLabelConstants.SELF.equals(patientAssoc.getRelationshipLabel())){
				patientInfo = patientAssoc.getPatient();
			}
		}
		return patientInfo;
	}
	
	public String deactivateProtocolFromPatient(Long id) throws HillromException {
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		Optional<PatientProtocolData> patientDeviceAssoc = patientProtocolRepository.findOneByPatientId(patientInfo.getId());
	     		if(patientDeviceAssoc.isPresent()){
	     			patientProtocolRepository.delete(patientDeviceAssoc.get());
	     			return MessageConstants.HR_244;
	     		} else {
	     			throw new HillromException(ExceptionConstants.HR_551);
	     		}
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
     	}
    }
}

