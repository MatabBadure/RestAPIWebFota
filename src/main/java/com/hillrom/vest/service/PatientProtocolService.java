package com.hillrom.vest.service;

import static com.hillrom.vest.config.AdherenceScoreConstants.UPPER_BOUND_VALUE;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateWeightedAvg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
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

import com.hillrom.monarch.repository.ProtocolConstantsMonarchRepository;
import com.hillrom.monarch.service.PatientProtocolMonarchService;
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
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
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
	private ProtocolConstantsMonarchRepository  protocolConstantsMonarchRepository;
	
	@Inject
	private MailService mailService;
	
	@Inject
	private PatientProtocolMonarchService patientProtocolMonarchService;
    
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
    
	public List<PatientProtocolData> updateProtocolToPatient(Long patientUserId, List<PatientProtocolData> ppdList)
			throws HillromException {
		User patientUser = userRepository.findOne(patientUserId);
		if (patientUser != null) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
			if (patientInfo != null) {
				List<PatientProtocolData> protocolList = new LinkedList<>();
				int treatmentsPerDay = ppdList.get(0).getTreatmentsPerDay();
				String type = ppdList.get(0).getType();
				String protocolKey = ppdList.get(0).getProtocolKey();
				// Check whether protocol type is same
				Boolean isFirstPoint = true;
				PatientProtocolData existingPPD = patientProtocolRepository.findOne(ppdList.get(0).getId());
				//If not same
				if (Objects.nonNull(existingPPD) & !existingPPD.getType().equalsIgnoreCase(type)) {
					String protocolId = null;
					deleteProtocolForPatient(patientUserId, protocolKey);
					for (PatientProtocolData ppd : ppdList) {
						if (isFirstPoint) {
							protocolKey = patientProtocolRepository.id();
							protocolId = protocolKey;
							isFirstPoint = false;
						} else {
							protocolId = patientProtocolRepository.id();
						}
						addProtocolByProtocolKey(patientUser, patientInfo, protocolList, treatmentsPerDay, type,
								protocolKey, ppd, protocolId);
					}
				} else
					ppdList.forEach(ppd -> {
						if (Objects.nonNull(ppd.getId())) {
							PatientProtocolData currentPPD = patientProtocolRepository.findOne(ppd.getId());
							if (Objects.nonNull(currentPPD)) {
								currentPPD.setLastModifiedDate(DateTime.now());
								assignValuesToPatientProtocolObj(ppd, currentPPD);
								patientProtocolRepository.saveAndFlush(currentPPD);
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
						mailService.sendUpdateProtocolMailToPatient(patientUser, protocolList);
					if(SecurityContextHolder.getContext().getAuthentication()
							.getAuthorities().contains(new SimpleGrantedAuthority(AuthoritiesConstants.HCP)) || SecurityContextHolder.getContext()
							.getAuthentication()
							.getAuthorities().contains(new SimpleGrantedAuthority(AuthoritiesConstants.CLINIC_ADMIN))){ 
						Optional<User> currentUser = userRepository
							.findOneByEmailOrHillromId(SecurityUtils.getCurrentLogin());
						mailService.sendUpdateProtocolMailToMailingList(currentUser.get(), patientUser, protocolList);
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

    public List<PatientProtocolData> getActiveProtocolsAssociatedWithPatient(Long patientUserId) throws HillromException {
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
	     			List<PatientProtocolData> protocolAssocListToSave = new LinkedList<>();
	     			for(PatientProtocolData protocolAssoc: protocolAssocList){
	     				protocolAssoc.setDeviceType("VEST");
	     				patientProtocolRepository.save(protocolAssoc);
	     				protocolAssocListToSave.add(protocolAssoc);
	     			}
	     			return protocolAssocListToSave;
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
	     				//When deleting last modified date needs to be updated
	     				//one sec less than the new created Date to recognize 
	     				//new and old separately
	     				protocol.setLastModifiedDate(new DateTime().minusSeconds(1));
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
	
	// Averaged Protocol of VEST and MONARCH for BOTH Device patient Users
	public Map<Long,ProtocolConstants> getMergedProtocolByPatientUserIds(List<Long> patientUserIds) throws Exception{
		List<PatientProtocolData> protocolData =  findByPatientUserIds(patientUserIds);		
		List<PatientProtocolDataMonarch> protocolDataMonarch =  patientProtocolMonarchService.findByPatientUserIds(patientUserIds);
		
		Map<Long, List<PatientProtocolData>> userIdProtocolMap = prepareUserIdProtocolMap(protocolData);		
		Map<Long, List<PatientProtocolDataMonarch>> userIdProtocolMapMonarch = patientProtocolMonarchService.prepareUserIdProtocolMap(protocolDataMonarch);
		
		Map<Long,ProtocolConstants> userIdProtocolConstantsMap = new HashMap<>();
		for(Long patientUserId : patientUserIds){
			ProtocolConstants protocolValue;
			
			List<PatientProtocolData> protocol = userIdProtocolMap.get(patientUserId);
			List<PatientProtocolDataMonarch> protocolMonarch = userIdProtocolMapMonarch.get(patientUserId);
			
			// Passing Vest and Monarch protocol to get the merged protocol for both device patient
			protocolValue = getMergedProtocolForPatientUser(protocol, protocolMonarch);
		
			userIdProtocolConstantsMap.put(patientUserId,protocolValue);
		}
		return userIdProtocolConstantsMap;
	}
	
	// Getting Vest protocol for vest only device patient
	public ProtocolConstants getVestProtocolForPatientUser(List<PatientProtocolData> protocol){
		
		ProtocolConstants protocolConstant;
		if(Objects.nonNull(protocol) && !protocol.isEmpty()){
			String protocolType = protocol.get(0).getType();
			if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(protocolType)){
				protocolConstant = getProtocolConstantFromNormalProtocol(protocol);
			}else{
				protocolConstant = getProtocolConstantFromCustomProtocol(protocol);
			}
		}else{
			protocolConstant = protocolConstantsRepository.findOne(1L);
		}
		
		return protocolConstant;
	}
	
	// Getting merged protocol for each patient
	public ProtocolConstants getMergedProtocolForPatientUser(List<PatientProtocolData> protocol, 
																List<PatientProtocolDataMonarch> protocolMonarch){
		ProtocolConstants protocolConstant;
		
		if(Objects.nonNull(protocol) && !protocol.isEmpty()){
			if(Objects.nonNull(protocolMonarch) && !protocolMonarch.isEmpty()){
				
				// To get Merged protocol for Both Vest and Monarch having protocols
				protocolConstant = getProtocolForBothVestAndMonarch(protocol, protocolMonarch);
				
			}else{
				
				// To get Merged protocol for Vest having and Default Monarch Protocols
				protocolConstant = getProtocolWithVestAndDefaultMonarch(protocol);
			}
		}else{
			if(Objects.nonNull(protocolMonarch) && !protocolMonarch.isEmpty()){
				
				// To get Merged protocol for Monarch having and Default Vest Protocols
				protocolConstant = getProtocolWithMonarchAndDefaultVest(protocolMonarch);
			}else{
				
				// To get Merged protocol for Both Vest and Monarch Default Protocols
				protocolConstant = getProtocolConstantFromDefaultVestDefaultMonarch();
			}
		}
		return protocolConstant;
	}

	// Getting Merged protocol for Both Vest and Monarch having protocols
	public ProtocolConstants getProtocolForBothVestAndMonarch(List<PatientProtocolData> protocol, 
			List<PatientProtocolDataMonarch> protocolMonarch){
		
		// Defining the return variable
		ProtocolConstants protocolConstant;
		
		// Getting protocol types from each device
		String protocolType = protocol.get(0).getType();
		String protocolTypeMonarch = protocolMonarch.get(0).getType();
		
		if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(protocolType)){
			if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(protocolTypeMonarch)){
				// To get Merged Protocol for Normal Vest and Monarch protocol 
				protocolConstant = getProtocolConstantFromNormalProtocolBoth(protocol,protocolMonarch);
			}else{
				// To get Merged Protocol for Normal Vest and Custom Monarch protocol
				protocolConstant = getProtocolConstantFromNormalVestAndCustomMonarch(protocol,protocolMonarch);
			}
		}else{
			if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(protocolTypeMonarch)){
				// To get Merged Protocol for Custom Vest and Normal Monarch protocol 
				protocolConstant = getProtocolConstantFromCustomVestAndNormalMonarch(protocol,protocolMonarch);
			}else{
				// To get Merged Protocol for Custom Vest and Monarch protocol
				protocolConstant = getProtocolConstantFromCustomProtocolBoth(protocol,protocolMonarch);
			}
		}
		return protocolConstant;
	}
	
	// Getting Merged protocol for Vest having and default Monarch protocols
	public ProtocolConstants getProtocolWithVestAndDefaultMonarch(List<PatientProtocolData> protocol){

		// Defining the return variable
		ProtocolConstants protocolConstant;
		
		// Getting protocol types the device
		String protocolType = protocol.get(0).getType();				
		
		if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(protocolType)){
			
			// To get Merged Protocol for Normal Vest and Default Monarch protocol
			protocolConstant = getProtocolConstantFromNormalVestDefaultMonarch(protocol);						
		}else{
			
			// To get Merged Protocol for Custom Vest and Default Monarch protocol
			protocolConstant = getProtocolConstantFromCustomVestDefaultMonarch(protocol);
		}
		
		return protocolConstant;
	}
	
	// Getting Merged protocol for Vest having and default Monarch protocols
	public ProtocolConstants getProtocolWithMonarchAndDefaultVest(List<PatientProtocolDataMonarch> protocolMonarch){
		
		// Defining the return variable
		ProtocolConstants protocolConstant;	
		
		// Getting protocol types the device
		String protocolTypeMonarch = protocolMonarch.get(0).getType();
		
		if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(protocolTypeMonarch)){
			
			// To get Merged Protocol for Normal Monarch and Default Vest protocol
			protocolConstant = getProtocolConstantFromNormalMonarchDefaultVest(protocolMonarch);
		}else{
			// To get Merged Protocol for Custom Monarch and Default Vest protocol
			protocolConstant = getProtocolConstantFromCustomMonarchDefaultVest(protocolMonarch);
		}
		
		return protocolConstant;
	}
	
	/**
	 * Get Protocol Constants by loading Protocol data for user Id
	 * @param Long patientUserId
	 * @return ProtocolConstants
	 */
	public ProtocolConstants getProtocolForPatientUserId(
			Long patientUserId) throws Exception{
		List<PatientProtocolData> protocol =  findOneByPatientUserIdAndStatus(patientUserId,false);
		
		ProtocolConstants userIdProtocolConstant;
		
		if(Objects.nonNull(protocol) && protocol.size() > 0){			
			String protocolType = protocol.get(0).getType();
			if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(protocolType)){
				userIdProtocolConstant = getProtocolConstantFromNormalProtocol(protocol);
			}else{
				userIdProtocolConstant = getProtocolConstantFromCustomProtocol(protocol);
			}
		}else{
			userIdProtocolConstant = protocolConstantsRepository.findOne(1L);
		}
		return userIdProtocolConstant;
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

	// Getting merged protocol for normal vest and monarch
	private ProtocolConstants getProtocolConstantFromNormalProtocolBoth(
			List<PatientProtocolData> protocolData, List<PatientProtocolDataMonarch> protocolDataMonarch) {
		int maxFrequency;
		int minFrequency;
		int minPressure;
		int maxPressure;
		int minDuration;
		int treatmentsPerDay;
		
		PatientProtocolData protocol = protocolData.get(0);
		PatientProtocolDataMonarch protocolMonarch = protocolDataMonarch.get(0);
		maxFrequency = (int) Math.round(protocol.getMaxFrequency() + protocolMonarch.getMaxFrequency()) / 2;
		minFrequency = (int) Math.round(protocol.getMinFrequency() + protocolMonarch.getMinFrequency()) / 2;
		maxPressure = (int) Math.round(protocol.getMaxPressure() + protocolMonarch.getMaxIntensity()) / 2;
		minPressure = (int) Math.round(protocol.getMinPressure() + protocol.getMinPressure())/2;
		treatmentsPerDay = (int) Math.round(protocol.getTreatmentsPerDay() + protocol.getTreatmentsPerDay()) / 2;
		
		minDuration = protocol.getMinMinutesPerTreatment() * protocol.getTreatmentsPerDay();		
		minDuration = (int) Math.round( minDuration + (protocolMonarch.getMinMinutesPerTreatment() * protocolMonarch.getTreatmentsPerDay() )) / 2;
		
		return new ProtocolConstants(maxFrequency,minFrequency,maxPressure,minPressure,treatmentsPerDay,minDuration);
	}
	
	// Getting merged protocol for normal vest and default monarch
	private ProtocolConstants getProtocolConstantFromNormalVestDefaultMonarch(
			List<PatientProtocolData> protocolData) {
		int maxFrequency;
		int minFrequency;
		int minPressure;
		int maxPressure;
		int minDuration;
		int treatmentsPerDay;
		
		ProtocolConstantsMonarch protocolMonarch = protocolConstantsMonarchRepository.findOne(1L);
		PatientProtocolData protocol = protocolData.get(0);

		maxFrequency = (int) Math.round(protocol.getMaxFrequency() + protocolMonarch.getMaxFrequency()) / 2;
		minFrequency = (int) Math.round(protocol.getMinFrequency() + protocolMonarch.getMinFrequency()) / 2;
		maxPressure = (int) Math.round(protocol.getMaxPressure() + protocolMonarch.getMaxIntensity()) / 2;
		minPressure = (int) Math.round(protocol.getMinPressure() + protocolMonarch.getMinIntensity())/2;
		treatmentsPerDay = (int) Math.round(protocol.getTreatmentsPerDay() + protocol.getTreatmentsPerDay()) / 2;
		
		minDuration = protocol.getMinMinutesPerTreatment() * protocol.getTreatmentsPerDay();		
		minDuration = (int) Math.round( minDuration + (protocolMonarch.getMinMinutesPerTreatment() * protocolMonarch.getTreatmentsPerDay() )) / 2;
		
		return new ProtocolConstants(maxFrequency,minFrequency,maxPressure,minPressure,treatmentsPerDay,minDuration);
	}	
	
	// Getting merged protocol for custom vest and default monarch
	private ProtocolConstants getProtocolConstantFromCustomVestDefaultMonarch(
			List<PatientProtocolData> protocolData) {		
		
		int maxFrequency;
		int minFrequency;
		int minPressure;
		int maxPressure;
		int minDuration;
		int treatmentsPerDay;
		float weightedAvgFrequency = 0;
		float weightedAvgPressure = 0;
		
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
						
		ProtocolConstantsMonarch protocolMonarch = protocolConstantsMonarchRepository.findOne(1L); 
		maxFrequency = (int) Math.round((maxFrequency + protocolMonarch.getMaxFrequency()) / 2);
		minFrequency = (int) Math.round((minFrequency + protocolMonarch.getMinFrequency()) / 2);
		maxPressure = (int) Math.round((maxPressure + protocolMonarch.getMaxIntensity()) / 2);
		minPressure = (int) Math.round((minPressure + protocolMonarch.getMinIntensity()) / 2);
		treatmentsPerDay = (int) Math.round(treatmentsPerDay+protocolMonarch.getTreatmentsPerDay())/2;
		minDuration = (int) Math.round(minDuration+(protocolMonarch.getMinMinutesPerTreatment() * protocolMonarch.getTreatmentsPerDay()));
		
		return new ProtocolConstants(maxFrequency,minFrequency,maxPressure,minPressure,treatmentsPerDay,minDuration);
	}
	
	// Getting to get the merged Protocol for Normal Monarch and Default Vest protocol
	private ProtocolConstants getProtocolConstantFromNormalMonarchDefaultVest(List<PatientProtocolDataMonarch> protocolDataMonarch) {
		int maxFrequency;
		int minFrequency;
		int minPressure;
		int maxPressure;
		int minDuration;
		int treatmentsPerDay;
		
		ProtocolConstants protocol = protocolConstantsRepository.findOne(1L);
		PatientProtocolDataMonarch protocolMonarch = protocolDataMonarch.get(0);
		maxFrequency = (int) Math.round(protocol.getMaxFrequency() + protocolMonarch.getMaxFrequency()) / 2;
		minFrequency = (int) Math.round(protocol.getMinFrequency() + protocolMonarch.getMinFrequency()) / 2;
		maxPressure = (int) Math.round(protocol.getMaxPressure() + protocolMonarch.getMaxIntensity()) / 2;
		minPressure = (int) Math.round(protocol.getMinPressure() + protocolMonarch.getMinIntensity())/2;
		treatmentsPerDay = (int) Math.round(protocol.getTreatmentsPerDay() + protocolMonarch.getTreatmentsPerDay()) / 2;
		
		minDuration = protocol.getMinMinutesPerTreatment() * protocol.getTreatmentsPerDay();		
		minDuration = (int) Math.round( minDuration + (protocolMonarch.getMinMinutesPerTreatment() * protocolMonarch.getTreatmentsPerDay() )) / 2;
		
		return new ProtocolConstants(maxFrequency,minFrequency,maxPressure,minPressure,treatmentsPerDay,minDuration);
	}	
	
	// Getting merged Protocol for Custom Monarch and Default Vest protocol
	private ProtocolConstants getProtocolConstantFromCustomMonarchDefaultVest(List<PatientProtocolDataMonarch> protocolDataMonarch) {
		int maxFrequency;
		int minFrequency;
		int minPressure;
		int maxPressure;
		int minDuration;
		int treatmentsPerDay;
		int treatmentsPerDayMonarch;
		float weightedAvgFrequency = 0;		
		float weightedAvgIntensity = 0;
		
		// Retrieving Default vest protocol details
		ProtocolConstants protocol = protocolConstantsRepository.findOne(1L);
		maxFrequency = protocol.getMaxFrequency();
		minFrequency = protocol.getMinFrequency();
		maxPressure = protocol.getMaxPressure();
		minPressure = protocol.getMinPressure();
		treatmentsPerDay = protocol.getTreatmentsPerDay();
		minDuration = protocol.getMinMinutesPerTreatment() * protocol.getTreatmentsPerDay();
		
		// Retrieving Custom monarch protocol details
		double totalDuration = protocolDataMonarch.stream().collect(Collectors.summingDouble(PatientProtocolDataMonarch :: getMinMinutesPerTreatment));
		for(PatientProtocolDataMonarch protocolMonarch : protocolDataMonarch){
			weightedAvgFrequency += calculateWeightedAvg(totalDuration, protocolMonarch.getMinMinutesPerTreatment(), protocolMonarch.getMinFrequency());
			weightedAvgIntensity += calculateWeightedAvg(totalDuration, protocolMonarch.getMinMinutesPerTreatment(), protocolMonarch.getMinIntensity());
		}
		
		// Updating protocol with the average of Normal and Custom protocols
		treatmentsPerDayMonarch = protocolDataMonarch.get(0).getTreatmentsPerDay();
		minFrequency = Math.round((minFrequency+weightedAvgFrequency)/2);
		maxFrequency = (int) Math.round((maxFrequency+(minFrequency*UPPER_BOUND_VALUE))/2);
		minPressure = Math.round((minPressure+weightedAvgIntensity)/2);
		maxPressure = (int) Math.round((maxPressure+(minPressure*UPPER_BOUND_VALUE))/2);
		minDuration = (int)(((totalDuration*treatmentsPerDayMonarch)+minDuration)/2);
		int avgTreatmentPerDay = (treatmentsPerDay+treatmentsPerDayMonarch)/2;
		
		return new ProtocolConstants(maxFrequency,minFrequency,maxPressure,minPressure,avgTreatmentPerDay,minDuration);		
	}
	
	// Getting Merged protocol for Both Vest and Monarch default protocols
	private ProtocolConstants getProtocolConstantFromDefaultVestDefaultMonarch() {
		int maxFrequency;
		int minFrequency;
		int minPressure;
		int maxPressure;
		int minDuration;
		int treatmentsPerDay;

		ProtocolConstants protocol = protocolConstantsRepository.findOne(1L);
		ProtocolConstantsMonarch protocolMonarch = protocolConstantsMonarchRepository.findOne(1L);

		maxFrequency = (int) Math.round(protocol.getMaxFrequency() + protocolMonarch.getMaxFrequency()) / 2;
		minFrequency = (int) Math.round(protocol.getMinFrequency() + protocolMonarch.getMinFrequency()) / 2;
		maxPressure = (int) Math.round(protocol.getMaxPressure() + protocolMonarch.getMaxIntensity()) / 2;
		minPressure = (int) Math.round(protocol.getMinPressure() + protocolMonarch.getMinIntensity())/2;
		treatmentsPerDay = (int) Math.round(protocol.getTreatmentsPerDay() + protocolMonarch.getTreatmentsPerDay()) / 2;
		
		minDuration = protocol.getMinMinutesPerTreatment() * protocol.getTreatmentsPerDay();		
		minDuration = (int) Math.round( minDuration + (protocolMonarch.getMinMinutesPerTreatment() * protocolMonarch.getTreatmentsPerDay() )) / 2;
		
		return new ProtocolConstants(maxFrequency,minFrequency,maxPressure,minPressure,treatmentsPerDay,minDuration);
	}
	
	// Getting merged protocol for normal vest and custom monarch
	private ProtocolConstants getProtocolConstantFromNormalVestAndCustomMonarch(
			List<PatientProtocolData> protocolData, List<PatientProtocolDataMonarch> protocolDataMonarch) {
		int maxFrequency;
		int minFrequency;
		int minPressure;
		int maxPressure;
		int minDuration;
		int treatmentsPerDay;
		int treatmentsPerDayMonarch;
		float weightedAvgFrequency = 0;		
		float weightedAvgIntensity = 0;
		
		// Retrieving Normal vest protocol details
		PatientProtocolData protocol = protocolData.get(0); 
		maxFrequency = protocol.getMaxFrequency();
		minFrequency = protocol.getMinFrequency();
		maxPressure = protocol.getMaxPressure();
		minPressure = protocol.getMinPressure();
		treatmentsPerDay = protocol.getTreatmentsPerDay();
		minDuration = protocol.getMinMinutesPerTreatment() * protocol.getTreatmentsPerDay();
		
		// Retrieving Custom monarch protocol details
		double totalDuration = protocolDataMonarch.stream().collect(Collectors.summingDouble(PatientProtocolDataMonarch :: getMinMinutesPerTreatment));
		for(PatientProtocolDataMonarch protocolMonarch : protocolDataMonarch){
			weightedAvgFrequency += calculateWeightedAvg(totalDuration, protocolMonarch.getMinMinutesPerTreatment(), protocolMonarch.getMinFrequency());
			weightedAvgIntensity += calculateWeightedAvg(totalDuration, protocolMonarch.getMinMinutesPerTreatment(), protocolMonarch.getMinIntensity());
		}
		
		// Updating protocol with the average of Normal and Custom protocols
		treatmentsPerDayMonarch = protocolDataMonarch.get(0).getTreatmentsPerDay();
		minFrequency = Math.round((minFrequency+weightedAvgFrequency)/2);
		maxFrequency = (int) Math.round((maxFrequency+(minFrequency*UPPER_BOUND_VALUE))/2);
		minPressure = Math.round((minPressure+weightedAvgIntensity)/2);
		maxPressure = (int) Math.round((maxPressure+(minPressure*UPPER_BOUND_VALUE))/2);
		minDuration = (int)(((totalDuration*treatmentsPerDayMonarch)+minDuration)/2);
		int avgTreatmentPerDay = (treatmentsPerDay+treatmentsPerDayMonarch)/2;
		
		return new ProtocolConstants(maxFrequency,minFrequency,maxPressure,minPressure,avgTreatmentPerDay,minDuration);		
	}
	
	// Getting merged protocol for custom vest and normal monarch
	private ProtocolConstants getProtocolConstantFromCustomVestAndNormalMonarch(
			List<PatientProtocolData> protocolData, List<PatientProtocolDataMonarch> protocolDataMonarch) {
		
		int maxFrequency;
		int minFrequency;
		int minPressure;
		int maxPressure;
		int minDuration;
		int treatmentsPerDay;
		float weightedAvgFrequency = 0;
		float weightedAvgPressure = 0;
		
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
		
		PatientProtocolDataMonarch protocolMonarch = protocolDataMonarch.get(0); 
		maxFrequency = (int) Math.round((maxFrequency + protocolMonarch.getMaxFrequency()) / 2);
		minFrequency = (int) Math.round((minFrequency + protocolMonarch.getMinFrequency()) / 2);
		maxPressure = (int) Math.round((maxPressure + protocolMonarch.getMaxIntensity()) / 2);
		minPressure = (int) Math.round((minPressure + protocolMonarch.getMinIntensity()) / 2);
		treatmentsPerDay = (int) Math.round(treatmentsPerDay+protocolMonarch.getTreatmentsPerDay())/2;
		minDuration = (int) Math.round(minDuration+(protocolMonarch.getMinMinutesPerTreatment() * protocolMonarch.getTreatmentsPerDay()));
		
		return new ProtocolConstants(maxFrequency,minFrequency,maxPressure,minPressure,treatmentsPerDay,minDuration);
	}
	
	// Getting merged protocol for both custom vest and monarch
	private ProtocolConstants getProtocolConstantFromCustomProtocolBoth(
			List<PatientProtocolData> protocolData, List<PatientProtocolDataMonarch> protocolDataMonarch) {
		int maxFrequency;
		int minFrequency;
		int minPressure;
		int maxPressure;
		int minDuration;
		int treatmentsPerDay;
		int treatmentsPerDayMonarch;
		float weightedAvgFrequency = 0;
		float weightedAvgPressure = 0;
		
		double totalDuration = protocolData.stream().collect(Collectors.summingDouble(PatientProtocolData :: getMinMinutesPerTreatment));
		for(PatientProtocolData protocol : protocolData){
			weightedAvgFrequency += calculateWeightedAvg(totalDuration, protocol.getMinMinutesPerTreatment(), protocol.getMinFrequency());
			weightedAvgPressure += calculateWeightedAvg(totalDuration, protocol.getMinMinutesPerTreatment(), protocol.getMinPressure());
		}
		
		double totalDurationMonarch = protocolDataMonarch.stream().collect(Collectors.summingDouble(PatientProtocolDataMonarch :: getMinMinutesPerTreatment));
		for(PatientProtocolDataMonarch protocolMonarch : protocolDataMonarch){
			weightedAvgFrequency += calculateWeightedAvg(totalDurationMonarch, protocolMonarch.getMinMinutesPerTreatment(), protocolMonarch.getMinFrequency());
			weightedAvgPressure += calculateWeightedAvg(totalDurationMonarch, protocolMonarch.getMinMinutesPerTreatment(), protocolMonarch.getMinIntensity());
		}
		
		treatmentsPerDay = protocolData.get(0).getTreatmentsPerDay();
		treatmentsPerDayMonarch = protocolDataMonarch.get(0).getTreatmentsPerDay();
		minFrequency = Math.round(weightedAvgFrequency);
		maxFrequency = (int) Math.round(minFrequency*UPPER_BOUND_VALUE);
		minPressure = Math.round(weightedAvgPressure);
		maxPressure = (int) Math.round(minPressure*UPPER_BOUND_VALUE);
		minDuration = (int)(((totalDuration*treatmentsPerDay)+(totalDurationMonarch*treatmentsPerDayMonarch))/2);
		int avgTreatmentPerDay = (int) Math.round((treatmentsPerDay+treatmentsPerDayMonarch)/2); 
		return new ProtocolConstants(maxFrequency,minFrequency,maxPressure,minPressure,avgTreatmentPerDay,minDuration);
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
	
	public List<PatientProtocolData> getAllProtocolsAssociatedWithPatient(Long patientUserId) throws HillromException {
    	User patientUser = userRepository.findOne(patientUserId);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		return patientProtocolRepository.findByPatientUserIdOrderByCreatedDateAsc(patientUserId);
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
     	}
    }
	
	private void addProtocolByProtocolKey(User patientUser, PatientInfo patientInfo,
			List<PatientProtocolData> protocolList, int treatmentsPerDay, String type, String protocolKey,
			PatientProtocolData ppd) {
		PatientProtocolData patientProtocolAssoc = new PatientProtocolData(type, patientInfo, patientUser,
				treatmentsPerDay, ppd.getMinMinutesPerTreatment(),ppd.getTreatmentLabel(),
				ppd.getMinFrequency(), ppd.getMaxFrequency(), ppd.getMinPressure(),
				ppd.getMaxPressure());
		patientProtocolAssoc.setId(patientProtocolRepository.id());
		patientProtocolAssoc.setProtocolKey(protocolKey);
		patientProtocolAssoc.setLastModifiedDate(DateTime.now());
		patientProtocolRepository.saveAndFlush(patientProtocolAssoc);
		protocolList.add(patientProtocolAssoc);
	}
	
	private void addProtocolByProtocolKey(User patientUser, PatientInfo patientInfo,
			List<PatientProtocolData> protocolList, int treatmentsPerDay, String type, String protocolKey,
			PatientProtocolData ppd, String protocolId) {
		PatientProtocolData patientProtocolAssoc = new PatientProtocolData(type, patientInfo, patientUser,
				treatmentsPerDay, ppd.getMinMinutesPerTreatment(),ppd.getTreatmentLabel(),
				ppd.getMinFrequency(), ppd.getMaxFrequency(), ppd.getMinPressure(),
				ppd.getMaxPressure());
		if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(type))
			patientProtocolAssoc.setTreatmentLabel("");
		
		if(Constants.CUSTOM_PROTOCOL.equalsIgnoreCase(type)){
			patientProtocolAssoc.setMaxFrequency(null);
		    patientProtocolAssoc.setMaxPressure(null);
		}
		patientProtocolAssoc.setId(protocolId);
		patientProtocolAssoc.setProtocolKey(protocolKey);
		patientProtocolAssoc.setLastModifiedDate(DateTime.now().plusSeconds(1));
		patientProtocolRepository.saveAndFlush(patientProtocolAssoc);
		protocolList.add(patientProtocolAssoc);
	}
	
	public void saveAll(Collection<PatientProtocolData> patientProtocolList){
		patientProtocolRepository.save(patientProtocolList);
	}
}

