package com.hillrom.vest.service.monarch;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceHistoryMonarch;
import com.hillrom.vest.domain.PatientVestDevicePK;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.repository.PatientVestDeviceRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.monarch.PatientMonarchDeviceRepository;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class PatientVestDeviceMonarchService {

    private final Logger log = LoggerFactory.getLogger(PatientVestDeviceMonarchService.class);

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private PatientMonarchDeviceRepository patientMonarchDeviceRepository;
    
    @Inject
    private PatientVestDeviceRepository patientVestDeviceRepository;
    
    @Inject
    private PatientInfoRepository patientInfoRepository;
    
    @Inject
    private UserService userService;

    @Inject
    private PatientVestDeviceDataRepository deviceDataRepository;
    
    public Object linkVestDeviceWithPatient(Long id, Map<String, Object> deviceData) throws HillromException {
    	User alreadyLinkedPatientuser = new User();
    	List<PatientVestDeviceHistoryMonarch> assocList = patientMonarchDeviceRepository.findBySerialNumber(deviceData.get("serialNumber").toString());
    	Optional<PatientVestDeviceHistoryMonarch> assocByBluetoothID = patientMonarchDeviceRepository.findByBluetoothIdAndStatusActive(deviceData.get("wifiId").toString());
    	PatientVestDeviceHistoryMonarch patientVestDeviceAssoc = new PatientVestDeviceHistoryMonarch();
    	if(assocByBluetoothID.isPresent()) {
    		alreadyLinkedPatientuser = (User) assocByBluetoothID.get().getPatient().getUserPatientAssoc().stream().filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser();
    		if(!alreadyLinkedPatientuser.getId().equals(id)){
    			return alreadyLinkedPatientuser;
			}
    	}
    	if(assocList.isEmpty()) {
    		patientVestDeviceAssoc = assignDeviceToPatient(id, deviceData);
    	} else {
    		List<PatientVestDeviceHistoryMonarch> activeDeviceList = assocList.stream().filter(patientDevice -> patientDevice.isActive()).collect(Collectors.toList());
    		if(!activeDeviceList.isEmpty()){
    			PatientVestDeviceHistoryMonarch activeDevice = activeDeviceList.get(0);
    			alreadyLinkedPatientuser = (User) activeDevice.getPatient().getUserPatientAssoc().stream().filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser();
    			if(alreadyLinkedPatientuser.getId().equals(id)){
    				patientVestDeviceAssoc = updateDeviceDetailsForPatient(activeDevice, deviceData);
    				return patientVestDeviceAssoc;
    			} else {
    				return alreadyLinkedPatientuser;
    			}
    		} else {
    			PatientInfo patientInfo = userService.getPatientInfoObjFromPatientUser(userRepository.getOne(id));
    			List<PatientVestDeviceHistoryMonarch> patientDeviceList = assocList.stream().filter(patientDevice -> 
    			(patientDevice.getPatient().getId().equalsIgnoreCase(patientInfo.getId()) && !patientDevice.isActive())).collect(Collectors.toList());
    			if(patientDeviceList.isEmpty()) {
    				patientVestDeviceAssoc = assignDeviceToPatient(id, deviceData);	
    			} else {
    				patientVestDeviceAssoc = updateDeviceDetailsForPatient(patientDeviceList.get(0), deviceData);
    			}
    		}
    	}
    	return patientVestDeviceAssoc;
    }

	private PatientVestDeviceHistoryMonarch assignDeviceToPatient(Long id, Map<String, Object> deviceData) throws HillromException {
		User patientUser = userRepository.findOne(id);
		if(Objects.nonNull(patientUser)) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
		 	if(Objects.nonNull(patientInfo)){
		 		patientInfo.setSerialNumber(Objects.nonNull(deviceData.get("serialNumber")) ? deviceData.get("serialNumber").toString() : null);
		 		patientInfo.setBluetoothId(Objects.nonNull(deviceData.get("wifiId")) ? deviceData.get("wifiId").toString() : null);
//		 		patientInfo.setHubId(Objects.nonNull(deviceData.get("hubId")) ? deviceData.get("hubId").toString() : null);
		 		patientInfo.setDeviceAssocDate(DateTime.now());
		 		patientInfoRepository.save(patientInfo);
		 		Optional<PatientVestDeviceHistoryMonarch> currentAssoc = patientMonarchDeviceRepository.findOneByPatientIdAndActiveStatus(patientInfo.getId(), true);
		 		if(currentAssoc.isPresent()){
	 				currentAssoc.get().setActive(false);
	 				patientMonarchDeviceRepository.save(currentAssoc.get());
		 		}
		 		PatientVestDeviceHistoryMonarch patientVestDeviceAssoc = new PatientVestDeviceHistoryMonarch(
		 				new PatientVestDevicePK(patientInfo, Objects.nonNull(deviceData.get("serialNumber")) ? deviceData.get("serialNumber").toString() : null), 
		 				Objects.nonNull(deviceData.get("wifiId")) ? deviceData.get("wifiId").toString() : null, 
		 				Objects.nonNull(deviceData.get("hubId")) ? deviceData.get("hubId").toString() : null, true);
		 		patientMonarchDeviceRepository.saveAndFlush(patientVestDeviceAssoc);
		 		return patientVestDeviceAssoc;
		 	} else {
		 		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);//No such user exist
		}
	}
	
	private PatientVestDeviceHistoryMonarch updateDeviceDetailsForPatient(PatientVestDeviceHistoryMonarch activeDevice, Map<String, Object> deviceData) throws HillromException {
		PatientInfo patientInfo = activeDevice.getPatient();
	 	if(Objects.nonNull(patientInfo)){
	 		patientInfo.setSerialNumber(Objects.nonNull(deviceData.get("serialNumber")) ? deviceData.get("serialNumber").toString() : null);
	 		patientInfo.setBluetoothId(Objects.nonNull(deviceData.get("wifiId")) ? deviceData.get("wifiId").toString() : null);
	 		patientInfo.setHubId(Objects.nonNull(deviceData.get("hubId")) ? deviceData.get("hubId").toString() : null);
	 		patientInfoRepository.save(patientInfo);
	 		activeDevice.setSerialNumber(Objects.nonNull(deviceData.get("serialNumber")) ? deviceData.get("serialNumber").toString() : null);
	 		activeDevice.setBluetoothId(Objects.nonNull(deviceData.get("wifiId")) ? deviceData.get("wifiId").toString() : null);
	 		activeDevice.setHubId(Objects.nonNull(deviceData.get("hubId")) ? deviceData.get("hubId").toString() : null);
	 		activeDevice.setActive(true);
	 		patientMonarchDeviceRepository.saveAndFlush(activeDevice);
	 		return activeDevice;
	 	} else {
	 		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	 	}
	}
    
    public List<PatientVestDeviceHistoryMonarch> getLinkedVestDeviceWithPatientMonarch(Long id) throws HillromException {
    	List<PatientVestDeviceHistoryMonarch> deviceList;
    	User patientUser = userRepository.findOne(id);
    	if(Objects.nonNull(patientUser)) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(Objects.nonNull(patientInfo)){
	     		deviceList = patientMonarchDeviceRepository.findByPatientId(patientInfo.getId());	     		
	     		/*if(Objects.nonNull(patientInfo.getSerialNumber())){
		     		PatientVestDeviceHistoryMonarch activeDevice = new PatientVestDeviceHistoryMonarch(new PatientVestDevicePK(patientInfo, patientInfo.getSerialNumber()),
		     				patientInfo.getBluetoothId(), patientInfo.getHubId(), true);
		     		activeDevice.setCreatedDate(patientInfo.getDeviceAssocDate());
		     		activeDevice.setLastModifiedDate(patientInfo.getDeviceAssocDate());
		     		deviceList.add(activeDevice);
	     		}*/
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	     	}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);//No such user exist
     	}
    	return deviceList;
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
	
	public String deactivateVestDeviceFromPatient(Long id, String serialNumber) throws HillromException {
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		Optional<PatientVestDeviceHistoryMonarch> patientDeviceAssoc = patientMonarchDeviceRepository.findOneByPatientIdAndSerialNumber(patientInfo.getId(), serialNumber);
	     		if(patientDeviceAssoc.isPresent()){
	     			if(patientDeviceAssoc.get().isActive()) {
		     			patientDeviceAssoc.get().setActive(false);
		     			// When dis-associated update the latest hmr
		     			patientDeviceAssoc.get().setHmr(getLatestHMR(id, serialNumber));
		 				patientMonarchDeviceRepository.save(patientDeviceAssoc.get());
		 				patientInfo.setSerialNumber(null);
		 				patientInfo.setBluetoothId(null);
		 				patientInfo.setHubId(null);
		 				patientInfo.setDeviceAssocDate(null);
		 				patientInfoRepository.saveAndFlush(patientInfo);
		 				return MessageConstants.HR_283;
	     			} else {
	     				throw new HillromException(ExceptionConstants.HR_570);//Vest device is already in Inactive mode
	     			}
	     		} else {
	     			throw new HillromException(ExceptionConstants.HR_571);//Invalid Serial Number
	     		}
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	     	}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);//No such user exist
     	}
    }
	
	public PatientVestDeviceHistoryMonarch deactivateActiveDeviceForPatient(Long id, DateTime dateTime) throws HillromException {
		 User patientUser = userRepository.findOne(id);
		 if(Objects.nonNull(patientUser)) {
			 PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     		if(Objects.nonNull(patientInfo)){
	     			Optional<PatientVestDeviceHistoryMonarch> vestDevice = patientMonarchDeviceRepository.findOneByPatientIdAndActiveStatus(patientInfo.getId(), true);
	     			if(vestDevice.isPresent()) {
	     				vestDevice.get().setActive(false);
	     				vestDevice.get().setLastModifiedDate(dateTime);
		     			// When dis-associated update the latest hmr 
	     				vestDevice.get().setHmr(getLatestHMR(id,patientInfo.getSerialNumber()));
		 				patientMonarchDeviceRepository.saveAndFlush(vestDevice.get());
		 				patientInfo.setSerialNumber(null);
		 				patientInfo.setBluetoothId(null);
		 				patientInfo.setHubId(null);
		 				patientInfo.setDeviceAssocDate(null);
		 				patientInfoRepository.saveAndFlush(patientInfo);
	     				return vestDevice.get();
	     			}
     				return null;
	     		} else {
	     			throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	     		}
		 } else {
			 throw new HillromException(ExceptionConstants.HR_512);//No such user exist
		 }
	}
	
	public PatientVestDeviceHistoryMonarch activateLatestDeviceForPatientBeforeExpiration(Long id) throws HillromException {
		 User patientUser = userRepository.findOne(id);
		 if(Objects.nonNull(patientUser)) {
			 PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     		if(Objects.nonNull(patientInfo)){
	     			List<PatientVestDeviceHistoryMonarch> vestDevice = patientMonarchDeviceRepository.findLatestDeviceForPatient(patientInfo.getId());
	     			if(!vestDevice.isEmpty()) {
	     				if(vestDevice.get(0).getLastModifiedDate().isAfter(patientUser.getExpirationDate())) {
	     					vestDevice.get(0).setActive(true);
			 				patientMonarchDeviceRepository.saveAndFlush(vestDevice.get(0));
			 				patientInfo.setSerialNumber(vestDevice.get(0).getSerialNumber());
			 				patientInfo.setBluetoothId(vestDevice.get(0).getBluetoothId());
			 				patientInfo.setHubId(vestDevice.get(0).getHubId());
			 				patientInfo.setDeviceAssocDate(vestDevice.get(0).getCreatedDate());
			 				patientInfoRepository.saveAndFlush(patientInfo);
		     				return vestDevice.get(0);
	     				}
	     				return vestDevice.get(0);
	     			}
    				return null;
	     		} else {
	     			throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	     		}
		 } else {
			 throw new HillromException(ExceptionConstants.HR_512);//No such user exist
		 }
	 }
	
	public Double getLatestHMR(Long id,String serialNumber){
		PatientVestDeviceData deviceData = deviceDataRepository.findTop1ByPatientUserIdAndSerialNumberOrderByHmrDesc(id, serialNumber);
		if(Objects.nonNull(deviceData))
			return deviceData.getHmr();
		else 
			return 0d;
	}
	
	public void updateHMR(User patientUser,PatientInfo patient)throws Exception{
		Optional<PatientVestDeviceHistoryMonarch>  deviceHistoryFromDB = patientMonarchDeviceRepository.findOneByPatientIdAndSerialNumber(patient.getId(),patient.getSerialNumber());
		if(deviceHistoryFromDB.isPresent()){
			PatientVestDeviceHistoryMonarch history = deviceHistoryFromDB.get();
			history.setHmr(getLatestHMR(patientUser.getId(),patient.getSerialNumber()));
			patientMonarchDeviceRepository.save(history);
		}else{
			PatientVestDeviceHistoryMonarch history = new PatientVestDeviceHistoryMonarch(new PatientVestDevicePK(patient, patient.getSerialNumber()),
     				patient.getBluetoothId(), patient.getHubId(), true);
     		history.setCreatedDate(patient.getDeviceAssocDate());
     		history.setLastModifiedDate(patient.getDeviceAssocDate());
     		history.setHmr(getLatestHMR(patientUser.getId(),patient.getSerialNumber()));
     		patientMonarchDeviceRepository.save(history);
		}
	}
	
	public PatientVestDeviceHistoryMonarch getLatestInActiveDeviceFromHistory(String patientId){
		return patientMonarchDeviceRepository.findLatestInActiveDeviceByPatientId(patientId, false);
	}
	
	public String getDeviceType(User user){
		PatientInfo patient = userService.getPatientInfoObjFromPatientUserId(user.getId());		
		if(patient != null){
			PatientInfo checkPatientId = patientInfoRepository.findOneById(patient.getId());
			if(Objects.nonNull(checkPatientId.getId() ) ){
				String deviceType = patientVestDeviceRepository.findDeviceType(patient.getId());
				return deviceType;
			}
		}
		return null;
	}
	
	
}

