package com.hillrom.vest.service;

import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateWeightedAvg;
import static com.hillrom.vest.config.AdherenceScoreConstants.UPPER_BOUND_VALUE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
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

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientProtocolRepository;
import com.hillrom.vest.repository.ProtocolConstantsRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.ProtocolDTO;
import com.hillrom.vest.web.rest.dto.ProtocolEntryDTO;

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
    
	@Inject
	private ProtocolConstantsRepository  protocolConstantsRepository;
	
	@Inject
	private MailService mailService;
    
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
		 		String protocolKey = patientProtocolRepository.id();
		 	    List<PatientProtocolData> protocolList = new LinkedList<>();
		 	    Boolean isFirstPoint = true;
		 		for(ProtocolEntryDTO protocolEntry : protocolDTO.getProtocolEntries()){
		 			PatientProtocolData patientProtocolAssoc = new PatientProtocolData(protocolDTO.getType(), patientInfo, patientUser,
		 					protocolDTO.getTreatmentsPerDay(), protocolEntry.getMinMinutesPerTreatment(), protocolEntry.getTreatmentLabel(),
		 					protocolEntry.getMinFrequency(), protocolEntry.getMaxFrequency(), protocolEntry.getMinPressure(),
		 					protocolEntry.getMaxPressure());
		 			if(isFirstPoint) {
		 				patientProtocolAssoc.setId(protocolKey);
		 				isFirstPoint = false;
		 			} else {
		 				patientProtocolAssoc.setId(patientProtocolRepository.id());
		 			}
		 			patientProtocolAssoc.setProtocolKey(protocolKey);
		 			protocolList.add(patientProtocolAssoc);
		 		}
		 		patientProtocolRepository.save(protocolList);
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
		 		int treatmentsPerDay = ppdList.get(0).getTreatmentsPerDay();
		 		String type = ppdList.get(0).getType();
		 		String protocolKey = ppdList.get(0).getProtocolKey();
		 		ppdList.forEach(ppd -> {
		 			if(Objects.nonNull(ppd.getId())){
			 			PatientProtocolData currentPPD = patientProtocolRepository.findOne(ppd.getId());
				 		if(Objects.nonNull(currentPPD)){
				 			assignValuesToPatientProtocolObj(ppd, currentPPD);
			 				patientProtocolRepository.saveAndFlush(currentPPD);
			 				protocolList.add(currentPPD);
				 		}
		 			} else {
		 				PatientProtocolData patientProtocolAssoc = new PatientProtocolData(type, patientInfo, patientUser,
		 						treatmentsPerDay, ppd.getMinMinutesPerTreatment(),ppd.getTreatmentLabel(),
		 						ppd.getMinFrequency(), ppd.getMaxFrequency(), ppd.getMinPressure(),
		 						ppd.getMaxPressure());
			 			patientProtocolAssoc.setId(patientProtocolRepository.id());
			 			patientProtocolAssoc.setProtocolKey(protocolKey);
			 			patientProtocolRepository.saveAndFlush(patientProtocolAssoc);
			 			protocolList.add(patientProtocolAssoc);
		 			}
		 		});
		 		try{
		 		mailService.sendUpdateProtocolMailToPatient(patientUser, protocolList);
		 		}catch(Exception ex){
					StringWriter writer = new StringWriter();
					PrintWriter printWriter = new PrintWriter( writer );
					ex.printStackTrace( printWriter );
		 		}
		 		
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
	     		List<PatientProtocolData> protocolAssocList = patientProtocolRepository.findByPatientIdAndActiveStatus(patientInfo.getId());
	     		if(protocolAssocList.isEmpty()){
	     			return new LinkedList<PatientProtocolData>();
	     		} else {
	     			return getProtocolDetails(patientUserId, protocolAssocList.get(0).getProtocolKey());
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
	
	public List<PatientProtocolData> findByPatientUserIds(List<Long> patientUserIds){
		return patientProtocolRepository.findByDeletedAndPatientUserIdIn(false,patientUserIds);
	}
	
	/**
	 * Get Protocol Constants by loading Protocol data
	 * @param List<Long> patientUserIds
	 * @return Map<Long,ProtocolConstants>
	 */
	public Map<Long,ProtocolConstants> getProtocolByPatientUserIds(
			List<Long> patientUserIds) throws Exception{
		List<PatientProtocolData> protocolData =  findByPatientUserIds(patientUserIds);
		
		Map<Long, List<PatientProtocolData>> userIdProtocolMap = prepareUserIdProtocolMap(protocolData);
		Map<Long,ProtocolConstants> userIdProtocolConstantsMap = new HashMap<>();
		for(Long patientUserId : patientUserIds){
			List<PatientProtocolData> protocol = userIdProtocolMap.get(patientUserId);
			if(Objects.nonNull(protocol) && protocol.size() > 0){			
				String protocolType = protocol.get(0).getType();
				if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(protocolType)){
					userIdProtocolConstantsMap.put(patientUserId,getProtocolConstantFromNormalProtocol(protocol));
				}else{
					userIdProtocolConstantsMap.put(patientUserId,getProtocolConstantFromCustomProtocol(protocol));
				}
			}else{
				userIdProtocolConstantsMap.put(patientUserId,protocolConstantsRepository.findOne(1L));
			}
		}
		return userIdProtocolConstantsMap;
	}

	private Map<Long, List<PatientProtocolData>> prepareUserIdProtocolMap(
			List<PatientProtocolData> protocolData) {
		Map<Long, List<PatientProtocolData>> userIdProtocolMap = new HashMap<>();
		for(PatientProtocolData protocol : protocolData){
			List<PatientProtocolData> protocolForUserId = userIdProtocolMap.get(protocol.getPatientUser().getId());
			if(Objects.isNull(protocolForUserId)){
				protocolForUserId = new LinkedList<>();
			}
			protocolForUserId.add(protocol);
			userIdProtocolMap.put(protocol.getPatientUser().getId(), protocolForUserId);
		}
		return userIdProtocolMap;
	}

	private ProtocolConstants getProtocolConstantFromNormalProtocol(
			List<PatientProtocolData> protocolData) {
		int maxFrequency,minFrequency,minPressure,maxPressure,minDuration,treatmentsPerDay;
		PatientProtocolData protocol = protocolData.get(0); 
		maxFrequency = protocol.getMaxFrequency();
		minFrequency = protocol.getMinFrequency();
		maxPressure = protocol.getMaxPressure();
		minPressure = protocol.getMinPressure();
		treatmentsPerDay = protocol.getTreatmentsPerDay();
		minDuration = protocol.getMinMinutesPerTreatment() * protocol.getTreatmentsPerDay();
		return new ProtocolConstants(maxFrequency,minFrequency,maxPressure,minPressure,treatmentsPerDay,minDuration);
	}

	private ProtocolConstants getProtocolConstantFromCustomProtocol(
			List<PatientProtocolData> protocolData) {
		int maxFrequency,minFrequency,minPressure,maxPressure,minDuration,treatmentsPerDay;
		float weightedAvgFrequency = 0,weightedAvgPressure = 0;
		double totalDuration = protocolData.stream().collect(Collectors.summingDouble(PatientProtocolData :: getMinMinutesPerTreatment));
		for(PatientProtocolData protocol : protocolData){
			weightedAvgFrequency += calculateWeightedAvg(totalDuration, protocol.getMinMinutesPerTreatment(), protocol.getMinFrequency());
			weightedAvgPressure += calculateWeightedAvg(totalDuration, protocol.getMinMinutesPerTreatment(), protocol.getMinPressure());
		}
		treatmentsPerDay = protocolData.get(0).getTreatmentsPerDay();
		minFrequency = Math.round(weightedAvgFrequency);
		maxFrequency = (int) Math.round(minFrequency*UPPER_BOUND_VALUE);
		minPressure = Math.round(weightedAvgPressure);
		maxPressure = (int) Math.round(minPressure*UPPER_BOUND_VALUE);
		minDuration = (int)(totalDuration*treatmentsPerDay);
		return new ProtocolConstants(maxFrequency,minFrequency,maxPressure,minPressure,treatmentsPerDay,minDuration);
	}
}

