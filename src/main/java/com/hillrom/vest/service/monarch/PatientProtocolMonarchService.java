package com.hillrom.vest.service.monarch;

import static com.hillrom.vest.config.AdherenceScoreConstants.UPPER_BOUND_VALUE;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateWeightedAvg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.PatientProtocolDataMonarch;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.ProtocolConstantsMonarch;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientProtocolRepository;
import com.hillrom.vest.repository.ProtocolConstantsRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.monarch.PatientProtocolMonarchRepository;
import com.hillrom.vest.repository.monarch.ProtocolConstantsMonarchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.MailService;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.monarch.ProtocolMonarchDTO;
import com.hillrom.vest.web.rest.dto.monarch.ProtocolEntryMonarchDTO;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class PatientProtocolMonarchService {

    private final Logger log = LoggerFactory.getLogger(PatientProtocolMonarchService.class);

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private PatientProtocolMonarchRepository patientProtocolMonarchRepository;
    
	@Inject
	private ProtocolConstantsMonarchRepository  protocolConstantsMonarchRepository;
	
	@Inject
	private MailService mailService;
    
    public List<PatientProtocolDataMonarch> addProtocolToPatient(Long patientUserId, ProtocolMonarchDTO protocolDTO) throws HillromException {
    	if(Constants.CUSTOM_PROTOCOL.equals(protocolDTO.getType())){
    		if(protocolDTO.getProtocolEntries().size() < 1 || protocolDTO.getProtocolEntries().size() > 7){
    			throw new HillromException(ExceptionConstants.HR_552);
    		}
    	}
    	User patientUser = userRepository.findOne(patientUserId);
		if(patientUser != null) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
		 	if(patientInfo != null){
		 		String protocolKey = patientProtocolMonarchRepository.id();
		 	    List<PatientProtocolDataMonarch> protocolList = new LinkedList<>();
		 	    Boolean isFirstPoint = true;
		 		for(ProtocolEntryMonarchDTO protocolEntry : protocolDTO.getProtocolEntries()){
		 			PatientProtocolDataMonarch patientProtocolAssoc = new PatientProtocolDataMonarch(protocolDTO.getType(), patientInfo, patientUser,
		 					protocolDTO.getTreatmentsPerDay(), protocolEntry.getMinMinutesPerTreatment(), protocolEntry.getTreatmentLabel(),
		 					protocolEntry.getMinFrequency(), protocolEntry.getMaxFrequency(), protocolEntry.getMinIntensity(),
		 					protocolEntry.getMaxIntensity());
		 			if(isFirstPoint) {
		 				patientProtocolAssoc.setId(protocolKey);
		 				isFirstPoint = false;
		 			} else {
		 				patientProtocolAssoc.setId(patientProtocolMonarchRepository.id());
		 			}
		 			patientProtocolAssoc.setProtocolKey(protocolKey);
		 			protocolList.add(patientProtocolAssoc);
		 		}
		 		patientProtocolMonarchRepository.save(protocolList);
		 		return protocolList;
		 	} else {
		 		throw new HillromException(ExceptionConstants.HR_523);
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
		}
    }
    
	public List<PatientProtocolDataMonarch> updateProtocolToPatient(Long patientUserId, List<PatientProtocolDataMonarch> ppdList)
			throws HillromException {
		User patientUser = userRepository.findOne(patientUserId);
		if (patientUser != null) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
			if (patientInfo != null) {
				List<PatientProtocolDataMonarch> protocolList = new LinkedList<>();
				int treatmentsPerDay = ppdList.get(0).getTreatmentsPerDay();
				String type = ppdList.get(0).getType();
				String protocolKey = ppdList.get(0).getProtocolKey();
				// Check whether protocol type is same
				Boolean isFirstPoint = true;
				PatientProtocolDataMonarch existingPPD = patientProtocolMonarchRepository.findOne(ppdList.get(0).getId());
				//If not same
				if (Objects.nonNull(existingPPD) & !existingPPD.getType().equalsIgnoreCase(type)) {
					String protocolId = null;
					deleteProtocolForPatient(patientUserId, protocolKey);
					for (PatientProtocolDataMonarch ppd : ppdList) {
						if (isFirstPoint) {
							protocolKey = patientProtocolMonarchRepository.id();
							protocolId = protocolKey;
							isFirstPoint = false;
						} else {
							protocolId = patientProtocolMonarchRepository.id();
						}
						addProtocolByProtocolKey(patientUser, patientInfo, protocolList, treatmentsPerDay, type,
								protocolKey, ppd, protocolId);
					}
				} else
					ppdList.forEach(ppd -> {
						if (Objects.nonNull(ppd.getId())) {
							PatientProtocolDataMonarch currentPPD = patientProtocolMonarchRepository.findOne(ppd.getId());
							if (Objects.nonNull(currentPPD)) {
								currentPPD.setLastModifiedDate(DateTime.now());
								assignValuesToPatientProtocolObj(ppd, currentPPD);
								patientProtocolMonarchRepository.saveAndFlush(currentPPD);
								protocolList.add(currentPPD);
							}
						} else {
							String existingprotocolKey = ppdList.get(0).getProtocolKey();
							addProtocolByProtocolKey(patientUser, patientInfo, protocolList, treatmentsPerDay, type,
									existingprotocolKey, ppd);
						}
					});
				try {
					if(Objects.nonNull(patientUser.getEmail()))
						mailService.sendUpdateProtocolMailToPatientMonarch(patientUser, protocolList);
					if(SecurityContextHolder.getContext().getAuthentication()
							.getAuthorities().contains(new SimpleGrantedAuthority(AuthoritiesConstants.HCP)) || SecurityContextHolder.getContext()
							.getAuthentication()
							.getAuthorities().contains(new SimpleGrantedAuthority(AuthoritiesConstants.CLINIC_ADMIN))){ 
						Optional<User> currentUser = userRepository
							.findOneByEmailOrHillromId(SecurityUtils.getCurrentLogin());
						mailService.sendUpdateProtocolMailToMailingListMonarch(currentUser.get(), patientUser, protocolList);
					}
				} catch (Exception ex) {
					StringWriter writer = new StringWriter();
					PrintWriter printWriter = new PrintWriter(writer);
					ex.printStackTrace(printWriter);
				}
				return protocolList;
			} else {
				throw new HillromException(ExceptionConstants.HR_523);
			}
		} else
			throw new HillromException(ExceptionConstants.HR_512);
	}
	
	private void assignValuesToPatientProtocolObj(PatientProtocolDataMonarch up, PatientProtocolDataMonarch cp) {
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
		if(Objects.nonNull(up.getMinIntensity()))
			cp.setMinIntensity(up.getMinIntensity());
		if(Objects.nonNull(up.getMaxIntensity()))
			cp.setMaxIntensity(up.getMaxIntensity());
	}

    public List<PatientProtocolDataMonarch> getActiveProtocolsAssociatedWithPatient(Long patientUserId) throws HillromException {
    	User patientUser = userRepository.findOne(patientUserId);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<PatientProtocolDataMonarch> protocolAssocList = patientProtocolMonarchRepository.findByPatientIdAndActiveStatus(patientInfo.getId());
	     		if(protocolAssocList.isEmpty()){
	     			return new LinkedList<PatientProtocolDataMonarch>();
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
    
    public List<PatientProtocolDataMonarch> getProtocolDetails(Long patientUserId, String protocolId) throws HillromException {
    	User patientUser = userRepository.findOne(patientUserId);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<PatientProtocolDataMonarch> protocolAssocList = patientProtocolMonarchRepository.findByProtocolKey(protocolId);
	     		if(protocolAssocList.isEmpty()){
	     			return new LinkedList<PatientProtocolDataMonarch>();
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
	     		List<PatientProtocolDataMonarch> protocolList = patientProtocolMonarchRepository.findByProtocolKey(protocolId);
	     		if(protocolList.isEmpty()){
	     			throw new HillromException(ExceptionConstants.HR_551);
	     		} else {
	     			protocolList.forEach(protocol -> {
	     				protocol.setDeleted(true);
	     				//When deleting last modified date needs to be updated
	     				//one sec less than the new created Date to recognize 
	     				//new and old separately
	     				protocol.setLastModifiedDate(new DateTime().minusSeconds(1));
	     				patientProtocolMonarchRepository.save(protocol);
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
	
	public List<PatientProtocolDataMonarch> findOneByPatientUserIdAndStatus(Long PatientUserId,boolean isDeleted){
		return patientProtocolMonarchRepository.findByPatientUserIdAndDeleted(PatientUserId, isDeleted);
	} 
	
	public List<PatientProtocolDataMonarch> findByPatientUserIds(List<Long> patientUserIds){
		return patientProtocolMonarchRepository.findByDeletedAndPatientUserIdIn(false,patientUserIds);
	}
	
	/**
	 * Get Protocol Constants by loading Protocol data
	 * @param List<Long> patientUserIds
	 * @return Map<Long,ProtocolConstants>
	 */
	public Map<Long,ProtocolConstantsMonarch> getProtocolByPatientUserIds(
			List<Long> patientUserIds) throws Exception{
		List<PatientProtocolDataMonarch> protocolData =  findByPatientUserIds(patientUserIds);
		
		Map<Long, List<PatientProtocolDataMonarch>> userIdProtocolMap = prepareUserIdProtocolMap(protocolData);
		Map<Long,ProtocolConstantsMonarch> userIdProtocolConstantsMap = new HashMap<>();
		for(Long patientUserId : patientUserIds){
			List<PatientProtocolDataMonarch> protocol = userIdProtocolMap.get(patientUserId);
			if(Objects.nonNull(protocol) && protocol.size() > 0){			
				String protocolType = protocol.get(0).getType();
				if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(protocolType)){
					userIdProtocolConstantsMap.put(patientUserId,getProtocolConstantFromNormalProtocol(protocol));
				}else{
					userIdProtocolConstantsMap.put(patientUserId,getProtocolConstantFromCustomProtocol(protocol));
				}
			}else{
				userIdProtocolConstantsMap.put(patientUserId,protocolConstantsMonarchRepository.findOne(1L));
			}
		}
		return userIdProtocolConstantsMap;
	}
	
	
	/**
	 * Get Protocol Constants by loading Protocol data for user Id
	 * @param Long patientUserId
	 * @return ProtocolConstants
	 */
	public ProtocolConstantsMonarch getProtocolForPatientUserId(
			Long patientUserId) throws Exception{
		List<PatientProtocolDataMonarch> protocol =  findOneByPatientUserIdAndStatus(patientUserId,false);
		
		ProtocolConstantsMonarch userIdProtocolConstant;
		
		if(Objects.nonNull(protocol) && protocol.size() > 0){			
			String protocolType = protocol.get(0).getType();
			if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(protocolType)){
				userIdProtocolConstant = getProtocolConstantFromNormalProtocol(protocol);
			}else{
				userIdProtocolConstant = getProtocolConstantFromCustomProtocol(protocol);
			}
		}else{
			userIdProtocolConstant = protocolConstantsMonarchRepository.findOne(1L);
		}
		return userIdProtocolConstant;
	}

	private Map<Long, List<PatientProtocolDataMonarch>> prepareUserIdProtocolMap(
			List<PatientProtocolDataMonarch> protocolData) {
		Map<Long, List<PatientProtocolDataMonarch>> userIdProtocolMap = new HashMap<>();
		for(PatientProtocolDataMonarch protocol : protocolData){
			List<PatientProtocolDataMonarch> protocolForUserId = userIdProtocolMap.get(protocol.getPatientUser().getId());
			if(Objects.isNull(protocolForUserId)){
				protocolForUserId = new LinkedList<>();
			}
			protocolForUserId.add(protocol);
			userIdProtocolMap.put(protocol.getPatientUser().getId(), protocolForUserId);
		}
		return userIdProtocolMap;
	}

	private ProtocolConstantsMonarch getProtocolConstantFromNormalProtocol(
			List<PatientProtocolDataMonarch> protocolData) {
		int maxFrequency,minFrequency,minIntensity,maxIntensity,minDuration,treatmentsPerDay;
		PatientProtocolDataMonarch protocol = protocolData.get(0); 
		maxFrequency = protocol.getMaxFrequency();
		minFrequency = protocol.getMinFrequency();
		maxIntensity = protocol.getMaxIntensity();
		minIntensity = protocol.getMinIntensity();
		treatmentsPerDay = protocol.getTreatmentsPerDay();
		minDuration = protocol.getMinMinutesPerTreatment() * protocol.getTreatmentsPerDay();
		return new ProtocolConstantsMonarch(maxFrequency,minFrequency,maxIntensity,minIntensity,treatmentsPerDay,minDuration);
	}

	private ProtocolConstantsMonarch getProtocolConstantFromCustomProtocol(
			List<PatientProtocolDataMonarch> protocolData) {
		int maxFrequency,minFrequency,minIntensity,maxIntensity,minDuration,treatmentsPerDay;
		float weightedAvgFrequency = 0,weightedAvgIntensity = 0;
		double totalDuration = protocolData.stream().collect(Collectors.summingDouble(PatientProtocolDataMonarch :: getMinMinutesPerTreatment));
		for(PatientProtocolDataMonarch protocol : protocolData){
			weightedAvgFrequency += calculateWeightedAvg(totalDuration, protocol.getMinMinutesPerTreatment(), protocol.getMinFrequency());
			weightedAvgIntensity += calculateWeightedAvg(totalDuration, protocol.getMinMinutesPerTreatment(), protocol.getMinIntensity());
		}
		treatmentsPerDay = protocolData.get(0).getTreatmentsPerDay();
		minFrequency = Math.round(weightedAvgFrequency);
		maxFrequency = (int) Math.round(minFrequency*UPPER_BOUND_VALUE);
		minIntensity = Math.round(weightedAvgIntensity);
		maxIntensity = (int) Math.round(minIntensity*UPPER_BOUND_VALUE);
		minDuration = (int)(totalDuration*treatmentsPerDay);
		return new ProtocolConstantsMonarch(maxFrequency,minFrequency,maxIntensity,minIntensity,treatmentsPerDay,minDuration);
	}
	
	public List<PatientProtocolDataMonarch> getAllProtocolsAssociatedWithPatient(Long patientUserId) throws HillromException {
    	User patientUser = userRepository.findOne(patientUserId);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		return patientProtocolMonarchRepository.findByPatientUserIdOrderByCreatedDateAsc(patientUserId);
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
     	}
    }
	
	private void addProtocolByProtocolKey(User patientUser, PatientInfo patientInfo,
			List<PatientProtocolDataMonarch> protocolList, int treatmentsPerDay, String type, String protocolKey,
			PatientProtocolDataMonarch ppd) {
		PatientProtocolDataMonarch patientProtocolAssoc = new PatientProtocolDataMonarch(type, patientInfo, patientUser,
				treatmentsPerDay, ppd.getMinMinutesPerTreatment(),ppd.getTreatmentLabel(),
				ppd.getMinFrequency(), ppd.getMaxFrequency(), ppd.getMinIntensity(),
				ppd.getMaxIntensity());
		patientProtocolAssoc.setId(patientProtocolMonarchRepository.id());
		patientProtocolAssoc.setProtocolKey(protocolKey);
		patientProtocolAssoc.setLastModifiedDate(DateTime.now());
		patientProtocolMonarchRepository.saveAndFlush(patientProtocolAssoc);
		protocolList.add(patientProtocolAssoc);
	}
	
	private void addProtocolByProtocolKey(User patientUser, PatientInfo patientInfo,
			List<PatientProtocolDataMonarch> protocolList, int treatmentsPerDay, String type, String protocolKey,
			PatientProtocolDataMonarch ppd, String protocolId) {
		PatientProtocolDataMonarch patientProtocolAssoc = new PatientProtocolDataMonarch(type, patientInfo, patientUser,
				treatmentsPerDay, ppd.getMinMinutesPerTreatment(),ppd.getTreatmentLabel(),
				ppd.getMinFrequency(), ppd.getMaxFrequency(), ppd.getMinIntensity(),
				ppd.getMaxIntensity());
		if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(type))
			patientProtocolAssoc.setTreatmentLabel("");
		
		if(Constants.CUSTOM_PROTOCOL.equalsIgnoreCase(type)){
			patientProtocolAssoc.setMaxFrequency(null);
		    patientProtocolAssoc.setMaxIntensity(null);
		}
		patientProtocolAssoc.setId(protocolId);
		patientProtocolAssoc.setProtocolKey(protocolKey);
		patientProtocolAssoc.setLastModifiedDate(DateTime.now().plusSeconds(1));
		patientProtocolMonarchRepository.saveAndFlush(patientProtocolAssoc);
		protocolList.add(patientProtocolAssoc);
	}
}

