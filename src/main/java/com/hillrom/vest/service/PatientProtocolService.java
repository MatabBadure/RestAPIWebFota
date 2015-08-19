package com.hillrom.vest.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.config.Constants;
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
import com.hillrom.vest.web.rest.dto.ProtocolDTO;

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
    
    public List<PatientProtocolData> addProtocolToPatient(Long patientUserId, ProtocolDTO protocolDTO) throws HillromException {
    	if(Constants.CUSTOM_PROTOCOL.equals(protocolDTO.getType())){
    		if(protocolDTO.getProtocolEntries().size() < 1 || protocolDTO.getProtocolEntries().size() > 7){
    			throw new HillromException(ExceptionConstants.HR_552);
    		}
    	}
    	User patientUser = userRepository.findOne(patientUserId);
		if(patientUser != null) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
		 	if(patientInfo != null){
		 		List<PatientProtocolData> protocolList = new LinkedList<>();
		 		protocolDTO.getProtocolEntries().forEach(protocolEntry -> {
		 			PatientProtocolData patientProtocolAssoc = new PatientProtocolData(protocolDTO.getType(), patientInfo, patientUser,
		 					protocolDTO.getTreatmentsPerDay(), protocolEntry.getMinMinutesPerTreatment(), protocolEntry.getMaxMinutesPerTreatment(),
		 					protocolEntry.getTreatmentLabel(), protocolEntry.getMinFrequency(), protocolEntry.getMaxFrequency(), protocolEntry.getMinPressure(),
		 					protocolEntry.getMaxPressure());
		 			patientProtocolAssoc.setId(patientProtocolRepository.id());
		 			patientProtocolAssoc.setProtocolKey(patientProtocolAssoc.getId());
		 			patientProtocolRepository.saveAndFlush(patientProtocolAssoc);
		 			protocolList.add(patientProtocolAssoc);
		 		});
		 		return protocolList;
		 	} else {
		 		throw new HillromException(ExceptionConstants.HR_523);
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
		}
    }
    
    public List<PatientProtocolData> updateProtocolToPatient(Long patientUserId, List<PatientProtocolData> ppdList) throws HillromException {
    	User patientUser = userRepository.findOne(patientUserId);
		if(patientUser != null) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
		 	if(patientInfo != null){
		 		List<PatientProtocolData> protocolList = new LinkedList<>();
		 		ppdList.forEach(ppd -> {
		 			PatientProtocolData currentPPD = patientProtocolRepository.findOne(ppd.getId());
			 		if(Objects.nonNull(currentPPD)){
			 			assignValuesToPatientProtocolObj(ppd, currentPPD);
		 				patientProtocolRepository.saveAndFlush(currentPPD);
		 				protocolList.add(currentPPD);
			 		}
		 		});
		 		return protocolList;
		 	} else {
		 		throw new HillromException(ExceptionConstants.HR_523);
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
		}
    }

	private void assignValuesToPatientProtocolObj(PatientProtocolData up, PatientProtocolData cp) {
		if(Objects.nonNull(up.getTreatmentsPerDay()))
			cp.setTreatmentsPerDay(up.getTreatmentsPerDay());
		if(Objects.nonNull(up.getTreatmentLabel()))
			cp.setTreatmentLabel(up.getTreatmentLabel());
		if(Objects.nonNull(up.getMinMinutesPerTreatment()))
			cp.setMinMinutesPerTreatment(up.getMinMinutesPerTreatment());
		if(Objects.nonNull(up.getMaxMinutesPerTreatment()))
			cp.setMaxMinutesPerTreatment(up.getMaxMinutesPerTreatment());
		if(Objects.nonNull(up.getMinFrequency()))
			cp.setMinFrequency(up.getMinFrequency());
		if(Objects.nonNull(up.getMaxFrequency()))
			cp.setMaxFrequency(up.getMaxFrequency());
		if(Objects.nonNull(up.getMinPressure()))
			cp.setMinPressure(up.getMinPressure());
		if(Objects.nonNull(up.getMaxPressure()))
			cp.setMaxPressure(up.getMaxPressure());
	}

    public List<PatientProtocolData> getAllProtocolsAssociatedWithPatient(Long patientUserId) throws HillromException {
    	User patientUser = userRepository.findOne(patientUserId);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<PatientProtocolData> protocolAssocList = patientProtocolRepository.findByPatientId(patientInfo.getId());
	     		if(protocolAssocList.isEmpty()){
	     			return new LinkedList<PatientProtocolData>();
	     		} else {
	     			return protocolAssocList;
	     		}
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
     	}
    }
    
    public List<PatientProtocolData> getProtocolDetails(Long patientUserId, String protocolId) throws HillromException {
    	User patientUser = userRepository.findOne(patientUserId);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<PatientProtocolData> protocolAssocList = patientProtocolRepository.findByProtocolKey(protocolId);
	     		if(protocolAssocList.isEmpty()){
	     			return new LinkedList<PatientProtocolData>();
	     		} else {
	     			return protocolAssocList;
	     		}
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
	
	public String deleteProtocolForPatient(Long patientUserId, String protocolId) throws HillromException {
    	User patientUser = userRepository.findOne(patientUserId);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<PatientProtocolData> protocolList = patientProtocolRepository.findByProtocolKey(protocolId);
	     		if(protocolList.isEmpty()){
	     			throw new HillromException(ExceptionConstants.HR_551);
	     		} else {
	     			protocolList.forEach(protocol -> {
	     				protocol.setDeleted(true);
	     				patientProtocolRepository.save(protocol);
	     			});
	     			return MessageConstants.HR_244;
	     		}
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
     	}
    }
	
	public List<PatientProtocolData> findOneByPatientUserIdAndStatus(Long PatientUserId,boolean isDeleted){
		return patientProtocolRepository.findByPatientUserIdAndDeleted(PatientUserId, isDeleted);
	} 
}

