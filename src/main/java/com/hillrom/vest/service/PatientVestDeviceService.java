package com.hillrom.vest.service;

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

import com.hillrom.vest.domain.PatientDevicesAssoc;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceHistory;
import com.hillrom.vest.domain.PatientVestDeviceHistoryMonarch;
import com.hillrom.vest.domain.PatientVestDevicePK;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientDevicesAssocRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.repository.PatientVestDeviceRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class PatientVestDeviceService {

    private final Logger log = LoggerFactory.getLogger(PatientVestDeviceService.class);

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private PatientVestDeviceRepository patientVestDeviceRepository;
    
    @Inject
    private PatientInfoRepository patientInfoRepository;
    
    @Inject
    private UserService userService;

    @Inject
    private PatientVestDeviceDataRepository deviceDataRepository;

    @Inject
    PatientDevicesAssocRepository patientDevicesAssoc;
    
    public String getDeviceType(Long userId){
		PatientInfo patient = userService.getPatientInfoObjFromPatientUserId(userId);		
		if(patient != null){
			PatientInfo checkPatientId = patientInfoRepository.findOneById(patient.getId());
			if(Objects.nonNull(checkPatientId.getId() ) ){
				String deviceType = patientVestDeviceRepository.findDeviceType(patient.getId());
				return deviceType;
			}
		}
		return null;
	}
    
    public Object linkVestDeviceWithPatient(Long id, Map<String, Object> deviceData) throws HillromException {
    	User alreadyLinkedPatientuser = new User();
    	List<PatientVestDeviceHistory> assocList = patientVestDeviceRepository.findBySerialNumber(deviceData.get("serialNumber").toString());
    	Optional<PatientVestDeviceHistory> assocByBluetoothID = patientVestDeviceRepository.findByBluetoothIdAndStatusActive(deviceData.get("bluetoothId").toString());
    	PatientVestDeviceHistory patientVestDeviceAssoc = new PatientVestDeviceHistory();
    	if(assocByBluetoothID.isPresent()) {
    		alreadyLinkedPatientuser = (User) assocByBluetoothID.get().getPatient().getUserPatientAssoc().stream().filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser();
    		if(!alreadyLinkedPatientuser.getId().equals(id)){
    			return alreadyLinkedPatientuser;
			}
    	}
    	if(assocList.isEmpty()) {
    		patientVestDeviceAssoc = assignDeviceToPatient(id, deviceData);
    	} else {
    		List<PatientVestDeviceHistory> activeDeviceList = assocList.stream().filter(patientDevice -> patientDevice.isActive()).collect(Collectors.toList());
    		if(!activeDeviceList.isEmpty()){
    			PatientVestDeviceHistory activeDevice = activeDeviceList.get(0);
    			alreadyLinkedPatientuser = (User) activeDevice.getPatient().getUserPatientAssoc().stream().filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())).collect(Collectors.toList()).get(0).getUser();
    			if(alreadyLinkedPatientuser.getId().equals(id)){
    				patientVestDeviceAssoc = updateDeviceDetailsForPatient(activeDevice, deviceData);
    				return patientVestDeviceAssoc;
    			} else {
    				return alreadyLinkedPatientuser;
    			}
    		} else {
    			PatientInfo patientInfo = userService.getPatientInfoObjFromPatientUser(userRepository.getOne(id));
    			List<PatientVestDeviceHistory> patientDeviceList = assocList.stream().filter(patientDevice -> 
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

	private PatientVestDeviceHistory assignDeviceToPatient(Long id, Map<String, Object> deviceData) throws HillromException {
		User patientUser = userRepository.findOne(id);
		if(Objects.nonNull(patientUser)) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
		 	if(Objects.nonNull(patientInfo)){
		 		patientInfo.setSerialNumber(Objects.nonNull(deviceData.get("serialNumber")) ? deviceData.get("serialNumber").toString() : null);
		 		patientInfo.setBluetoothId(Objects.nonNull(deviceData.get("bluetoothId")) ? deviceData.get("bluetoothId").toString() : null);
		 		patientInfo.setHubId(Objects.nonNull(deviceData.get("hubId")) ? deviceData.get("hubId").toString() : null);
		 		patientInfo.setDeviceAssocDate(DateTime.now());
		 		patientInfoRepository.save(patientInfo);
		 		Optional<PatientVestDeviceHistory> currentAssoc = patientVestDeviceRepository.findOneByPatientIdAndActiveStatus(patientInfo.getId(), true);
		 		if(currentAssoc.isPresent()){
	 				currentAssoc.get().setActive(false);
	 				currentAssoc.get().setLastModifiedDate(DateTime.now());
	 				patientVestDeviceRepository.save(currentAssoc.get());
		 		}
		 		PatientVestDeviceHistory patientVestDeviceAssoc = new PatientVestDeviceHistory(
		 				new PatientVestDevicePK(patientInfo, Objects.nonNull(deviceData.get("serialNumber")) ? deviceData.get("serialNumber").toString() : null), 
		 				Objects.nonNull(deviceData.get("bluetoothId")) ? deviceData.get("bluetoothId").toString() : null, 
		 				Objects.nonNull(deviceData.get("hubId")) ? deviceData.get("hubId").toString() : null, true, DateTime.now());
		 		patientVestDeviceRepository.saveAndFlush(patientVestDeviceAssoc);
		 		return patientVestDeviceAssoc;
		 	} else {
		 		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
		 	}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);//No such user exist
		}
	}
	
	private PatientVestDeviceHistory updateDeviceDetailsForPatient(PatientVestDeviceHistory activeDevice, Map<String, Object> deviceData) throws HillromException {
		PatientInfo patientInfo = activeDevice.getPatient();
	 	if(Objects.nonNull(patientInfo)){
	 		patientInfo.setSerialNumber(Objects.nonNull(deviceData.get("serialNumber")) ? deviceData.get("serialNumber").toString() : null);
	 		patientInfo.setBluetoothId(Objects.nonNull(deviceData.get("bluetoothId")) ? deviceData.get("bluetoothId").toString() : null);
	 		patientInfo.setHubId(Objects.nonNull(deviceData.get("hubId")) ? deviceData.get("hubId").toString() : null);
	 		patientInfoRepository.save(patientInfo);
	 		activeDevice.setSerialNumber(Objects.nonNull(deviceData.get("serialNumber")) ? deviceData.get("serialNumber").toString() : null);
	 		activeDevice.setBluetoothId(Objects.nonNull(deviceData.get("bluetoothId")) ? deviceData.get("bluetoothId").toString() : null);
	 		activeDevice.setHubId(Objects.nonNull(deviceData.get("hubId")) ? deviceData.get("hubId").toString() : null);
	 		activeDevice.setActive(true);
	 		activeDevice.setLastModifiedDate(DateTime.now());
	 		patientVestDeviceRepository.saveAndFlush(activeDevice);
	 		return activeDevice;
	 	} else {
	 		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	 	}
	}
    
    public List<PatientVestDeviceHistory> getLinkedVestDeviceWithPatient(Long id) throws HillromException {
    	List<PatientVestDeviceHistory> deviceList;
    	User patientUser = userRepository.findOne(id);
    	if(Objects.nonNull(patientUser)) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(Objects.nonNull(patientInfo)){
	     		deviceList = patientVestDeviceRepository.findByPatientId(patientInfo.getId());
	     		/*if(Objects.nonNull(patientInfo.getSerialNumber())){
		     		PatientVestDeviceHistory activeDevice = new PatientVestDeviceHistory(new PatientVestDevicePK(patientInfo, patientInfo.getSerialNumber()),
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
	     		Optional<PatientVestDeviceHistory> patientDeviceAssoc = patientVestDeviceRepository.findOneByPatientIdAndSerialNumber(patientInfo.getId(), serialNumber);
	     		if(patientDeviceAssoc.isPresent()){
	     			if(patientDeviceAssoc.get().isActive()) {
		     			patientDeviceAssoc.get().setActive(false);
		     			// When dis-associated update the latest hmr
		     			patientDeviceAssoc.get().setHmr(getLatestHMR(id, serialNumber));
		     			patientDeviceAssoc.get().setLastModifiedDate(DateTime.now());
		 				patientVestDeviceRepository.save(patientDeviceAssoc.get());
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
	
	public PatientVestDeviceHistory deactivateActiveDeviceForPatient(Long id, DateTime dateTime) throws HillromException {
		 User patientUser = userRepository.findOne(id);
		 if(Objects.nonNull(patientUser)) {
			 PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     		if(Objects.nonNull(patientInfo)){
	     			Optional<PatientVestDeviceHistory> vestDevice = patientVestDeviceRepository.findOneByPatientIdAndActiveStatus(patientInfo.getId(), true);
	     			if(vestDevice.isPresent()) {
	     				vestDevice.get().setActive(false);
	     				vestDevice.get().setLastModifiedDate(dateTime);
		     			// When dis-associated update the latest hmr 
	     				vestDevice.get().setHmr(getLatestHMR(id,patientInfo.getSerialNumber()));
	     				vestDevice.get().setLastModifiedDate(DateTime.now());
		 				patientVestDeviceRepository.saveAndFlush(vestDevice.get());
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
	
	public PatientVestDeviceHistory activateLatestDeviceForPatientBeforeExpiration(Long id) throws HillromException {
		 User patientUser = userRepository.findOne(id);
		 if(Objects.nonNull(patientUser)) {
			 PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     		if(Objects.nonNull(patientInfo)){
	     			List<PatientVestDeviceHistory> vestDevice = patientVestDeviceRepository.findLatestDeviceForPatient(patientInfo.getId());
	     			if(!vestDevice.isEmpty()) {
	     				if(vestDevice.get(0).getLastModifiedDate().isAfter(patientUser.getExpirationDate())) {
	     					vestDevice.get(0).setActive(true);

	     					vestDevice.get(0).setLastModifiedDate(DateTime.now());	     					

			 				patientVestDeviceRepository.saveAndFlush(vestDevice.get(0));
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
		Optional<PatientVestDeviceHistory>  deviceHistoryFromDB = 
				patientVestDeviceRepository.findOneByPatientIdAndSerialNumber(patient.getId(),patient.getSerialNumber());
		if(deviceHistoryFromDB.isPresent()){
			PatientVestDeviceHistory history = deviceHistoryFromDB.get();
			history.setHmr(getLatestHMR(patientUser.getId(),patient.getSerialNumber()));			
			patientVestDeviceRepository.save(history);
		}else{
			PatientDevicesAssoc oldPatientDevicesAssoc = patientDevicesAssoc.findOneByPatientIdAndDeviceType(patient.getId(),"VEST");
			Optional<PatientVestDeviceHistory> oldDeviceHistoryFromDB = 
					patientVestDeviceRepository.findOneByPatientIdAndSerialNumber(patient.getId(),
																						oldPatientDevicesAssoc.getSerialNumber());
			if(oldDeviceHistoryFromDB.isPresent()){
				PatientVestDeviceHistory history = oldDeviceHistoryFromDB.get();
				history.setHmr(getLatestHMR(patientUser.getId(),oldPatientDevicesAssoc.getSerialNumber()));
				patientVestDeviceRepository.save(history);
			}else{
				PatientVestDeviceHistory history = new PatientVestDeviceHistory(new PatientVestDevicePK(patient, patient.getSerialNumber()),
	     				patient.getBluetoothId(), patient.getHubId(), true);
	     		history.setCreatedDate(patient.getDeviceAssocDate());
	     		history.setLastModifiedDate(patient.getDeviceAssocDate());
	     		history.setHmr(getLatestHMR(patientUser.getId(),patient.getSerialNumber()));
	     		patientVestDeviceRepository.save(history);
			}
		}
	}
	
	public PatientVestDeviceHistory getLatestInActiveDeviceFromHistory(String patientId){
		return patientVestDeviceRepository.findLatestInActiveDeviceByPatientId(patientId, false);
	}
	
	public String getDeviceType(String patientId){		
		PatientInfo checkPatientId = patientInfoRepository.findOneById(patientId);
		if(Objects.nonNull(checkPatientId.getId() ) ){
			String deviceType = patientVestDeviceRepository.findDeviceType(patientId);
			return deviceType;
		}
	return null;
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

