package com.hillrom.vest.service;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scala.collection.concurrent.Debug;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.EntityUserAssoc;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.EntityUserRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSearchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.CareGiverStatsNotificationVO;
import com.hillrom.vest.web.rest.dto.ClinicDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.util.ClinicVOBuilder;

//start: HILL-2004
import com.hillrom.vest.service.util.DateUtil;
//end: HILL-2004

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class ClinicService {

    private final Logger log = LoggerFactory.getLogger(ClinicService.class);

    @Inject
    private ClinicRepository clinicRepository;
    
    @Inject
    private UserService userService;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private UserExtensionRepository userExtensionRepository;
    
    @Inject
    private UserSearchRepository userSearchRepository;
    
    @Inject
    private EntityUserRepository entityUserRepository;
    
    @Inject
    private HCPClinicService hcpClinicService;
    
    @Inject
    private ClinicPatientService clinicPatientService;
    

    public Clinic createClinic(ClinicDTO clinicDTO) throws HillromException {
    	Clinic newClinic = new Clinic();
    	if(Objects.nonNull(clinicDTO.getHillromId())) {
			Optional<Clinic> existingClinic = clinicRepository.findOneByHillromId(clinicDTO.getHillromId());
			if (existingClinic.isPresent()) {
				throw new HillromException(ExceptionConstants.HR_522);
    		}
    	}
    	// Assigns the next clinic HillromId from Stored Procedure
    	newClinic.setId(clinicRepository.id());
    	if(clinicDTO.getParent()) {
    		newClinic.setParent(clinicDTO.getParent());
    	}
    	if(StringUtils.isNotBlank(clinicDTO.getParentClinic().get("id"))) {
    		Clinic parentClinic = clinicRepository.getOne(clinicDTO.getParentClinic().get("id"));
   			parentClinic.setParent(true);
   			clinicRepository.save(parentClinic);
   			newClinic.setParentClinic(parentClinic);
    	}
    	assignUpdatedValues(clinicDTO, newClinic);
		clinicRepository.save(newClinic);
        if(StringUtils.isNotBlank(newClinic.getId())) {
            return newClinic;
        } else {
        	throw new HillromException(ExceptionConstants.HR_541);
        }
    }

    public ClinicVO updateClinic(String id, ClinicDTO clinicDTO) throws HillromException {
    	Clinic clinic = clinicRepository.getOne(id);
    	if(Objects.isNull(clinic)) {
    		throw new HillromException(ExceptionConstants.HR_548);//No such clinic found
    	} else if(StringUtils.isNotBlank(clinic.getId())) {
    		if(Objects.nonNull(clinicDTO.getHillromId())) {
    			Optional<Clinic> existingClinic = clinicRepository.findOneByHillromId(clinicDTO.getHillromId());
    			if (existingClinic.isPresent() && !existingClinic.get().getId().equals(id)) {
    				throw new HillromException(ExceptionConstants.HR_522);
    			}
    		}
    		assignUpdatedValues(clinicDTO, clinic);
    		if(Objects.nonNull(clinicDTO.getParent()) && clinicDTO.getParent()) {
    			List<String> existingChildClinicIds = new ArrayList<String>();
    			List<String> newChildClinicIds = new ArrayList<String>();
    			for(Clinic childClinic : clinic.getChildClinics()) {
    				existingChildClinicIds.add(childClinic.getId().toString());
    			}
    			for(Map<String, String> childClinic : clinicDTO.getChildClinicList()) {
    				newChildClinicIds.add(childClinic.get("id"));
    			}
    			List<String> clinicsToBeRemoved = RandomUtil.getDifference(existingChildClinicIds, newChildClinicIds);
    			
    			//TODO : to be refactored with clinicRepository.findAll(clinicsToBeRemoved)
    			for(String clinicId : clinicsToBeRemoved) {
    				Clinic childClinic = clinicRepository.getOne(clinicId);
    				
    				childClinic.setParentClinic(null);
    				clinicRepository.save(childClinic);
    				clinic.getChildClinics().remove(childClinic);
    			}
    		} else if(Objects.nonNull(clinicDTO.getParentClinic()) && StringUtils.isNotBlank(clinicDTO.getParentClinic().get("id"))) {
        		if(!id.equals(clinicDTO.getParentClinic().get("id"))) {
        			Clinic parentClinic = clinicRepository.getOne(clinicDTO.getParentClinic().get("id"));
        			parentClinic.setParent(true);
        			clinicRepository.save(parentClinic);
        			clinic.setParentClinic(parentClinic);       			
        		} else {
        			throw new HillromException(ExceptionConstants.HR_542);
        		} 
        	} else {
       			clinicRepository.save(clinic);
        	} 
    		clinicRepository.save(clinic);
        } else {
	      	throw new HillromException(ExceptionConstants.HR_543);
	    }
	    return ClinicVOBuilder.buildWithChildClinics(clinic);
    }
    
     public String deleteClinic(String id) throws HillromException {
    	Clinic existingClinic = clinicRepository.findOne(id);
		if(existingClinic != null) {
			
			//Dissociate Clinic Admins attached to Clinic
			List<User> userList = getClinicAdmin(existingClinic.getId());
			Map<String,String> clinicAdminMap = new HashMap<String,String>();
			if(!userList.isEmpty()){					
					for(User user : userList){
						clinicAdminMap.put("id", user.getId().toString());
						dissociateClinicAdmin(id, clinicAdminMap);
					}				
			}
			
			//Dissociate HCPS attached to Clinic
//			Set<UserExtension> userExtList = existingClinic.getUsers();
			Map<String,String> hm = new HashMap<String,String>();
			hm.put("id", existingClinic.getId());
			List<Map<String,String>> clinicList =  new ArrayList<>();
			clinicList.add(hm);
			
			List<String> idList = new ArrayList<>();
			idList.add(existingClinic.getId());
			List<Map<String,Object>> hcpUserList = getAssociatedHCPUsers(idList);
			if(!hcpUserList.isEmpty()){
				for(Map<String,Object> hcpUser : hcpUserList){
					UserExtension uExtPatient = (UserExtension) hcpUser.get("hcp");
					hcpClinicService.dissociateClinicFromHCP(uExtPatient.getId(), clinicList);
				}
				
			}
			
			//Dissociate PatientUsers attached to Clinic

			List<Map<String,Object>> patientUserList = getAssociatedPatientUsers(idList);
			if(!patientUserList.isEmpty()){
				for(Map<String,Object> patientUser : patientUserList){
					UserExtension uExtPatient = (UserExtension) patientUser.get("patient");
					clinicPatientService.dissociateClinicsToPatient(uExtPatient.getId(), clinicList);
				}
			}

			if(existingClinic.isParent()) {
				existingClinic.getChildClinics().forEach(childClinic -> {
					childClinic.setParentClinic(null);
				});
				clinicRepository.save(existingClinic.getChildClinics());
				existingClinic.setParent(false);
			}
			clinicRepository.delete(existingClinic);
			return MessageConstants.HR_224;

		} else {
			throw new HillromException(ExceptionConstants.HR_544);
		}
    }

	/**
	 * @param clinicDTO
	 * @param clinic
	 * Method assignUpdated Values
	 */
	private void assignUpdatedValues(ClinicDTO clinicDTO, Clinic clinic) {
		if (clinicDTO.getName() != null)
			clinic.setName(clinicDTO.getName());
		if (clinicDTO.getAddress() != null)
			clinic.setAddress(clinicDTO.getAddress());
		if (clinicDTO.getAddress2() != null)
			clinic.setAddress2(clinicDTO.getAddress2());
		if (clinicDTO.getCity() != null)
			clinic.setCity(clinicDTO.getCity());
		if (clinicDTO.getState() != null)
			clinic.setState(clinicDTO.getState());
		if (clinicDTO.getZipcode() != null)
			clinic.setZipcode(clinicDTO.getZipcode());
		if (clinicDTO.getPhoneNumber() != null)
			clinic.setPhoneNumber(clinicDTO.getPhoneNumber());
		if (clinicDTO.getFaxNumber() != null)
			clinic.setFaxNumber(clinicDTO.getFaxNumber());
		if (clinicDTO.getSpeciality() != null)
			clinic.setSpeciality(clinicDTO.getSpeciality());
		if (clinicDTO.getHillromId() != null)
			clinic.setHillromId(clinicDTO.getHillromId());
		if (clinicDTO.getClinicAdminId() != null)
			clinic.setClinicAdminId(clinicDTO.getClinicAdminId());
		if (clinicDTO.getDeleted() != null)
			clinic.setDeleted(clinicDTO.getDeleted());
		if (clinicDTO.getAdherenceSetting() != null)
			clinic.setAdherenceSetting(clinicDTO.getAdherenceSetting());
		if (clinicDTO.getIsMessageOpted() != null)
			clinic.setIsMessageOpted(clinicDTO.getIsMessageOpted());
		//start: HILL-2004
		if (clinicDTO.getAdherenceSettingFlag() != null && clinicDTO.getAdherenceSettingFlag().equals(Boolean.TRUE))
		{
		   clinic.setAdherenceSettingModifiedDte(DateUtil.getCurrentDateAndTime());
		}
		//end: HILL-2004
				
	}
	
	public boolean checkAllNullExceptDelete(ClinicDTO clinicDTO){
		
		try {
			for(PropertyDescriptor propDesc :
			    Introspector.getBeanInfo(ClinicDTO.class).getPropertyDescriptors()){
				
				if(!propDesc.getReadMethod().getName().equals("getChildClinicList") &&
					!propDesc.getReadMethod().getName().equals("getClass") &&
					!propDesc.getReadMethod().getName().equals("getDeleted") &&
					!propDesc.getReadMethod().getName().equals("getParentClinic")&&
					Objects.nonNull(propDesc.getReadMethod().invoke(clinicDTO))){
					return false;
				}
			}
			return true;
		} catch (IntrospectionException e) {
			log.debug(e.getMessage());
		} catch (IllegalAccessException e) {
			log.debug(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.debug(e.getMessage());
		} catch (InvocationTargetException e) {
			log.debug(e.getMessage());
		}
		return false;
	}

	public Set<UserExtension> getHCPUsers(List<String> idList) throws HillromException, EntityNotFoundException {
		Set<UserExtension> hcpUserList = new HashSet<>();
		for(String id : idList){
	    	Clinic clinic = clinicRepository.getOne(id);
	        if(Objects.isNull(clinic)){
	        	throw new HillromException(ExceptionConstants.HR_547);//Invalid clinic id found
	        } else {
	        	hcpUserList.addAll(clinic.getUsers());
	        }
		}
        return hcpUserList;
    }
	
	public List<Map<String,Object>> getAssociatedPatientUsers(List<String> idList) throws HillromException {
		List<Map<String,Object>> patientUserList = new LinkedList<>();
		for(String id : idList){
	    	Clinic clinic = clinicRepository.getOne(id);
	        if(Objects.isNull(clinic)) {
	        	throw new HillromException(ExceptionConstants.HR_547);
	        } else {
	        	clinic.getClinicPatientAssoc().forEach(clinicPatientAssoc -> {
	        		Map<String, Object> patientMap = new HashMap<>();
	        		UserExtension patientUser = (UserExtension) userService.getUserObjFromPatientInfo(clinicPatientAssoc.getPatient());
	        		patientMap.put("patient", patientUser);
	        		patientMap.put("mrnId", clinicPatientAssoc.getMrnId());
	        		patientMap.put("status", clinicPatientAssoc.getActive() & !patientUser.isDeleted());
	        		List<UserPatientAssoc> hcpAssocList = new LinkedList<>();
	    	     	for(UserPatientAssoc patientAssoc : clinicPatientAssoc.getPatient().getUserPatientAssoc()){
	    	    		if(AuthoritiesConstants.HCP.equals(patientAssoc.getUserRole())){
	    	    			hcpAssocList.add(patientAssoc);
	    	    		}
	    	    	}
	    	     	Collections.sort(hcpAssocList);
	    	     	if(!hcpAssocList.isEmpty())
	    	     		patientMap.put("hcp",hcpAssocList.get(0).getUser());
	    	     	else patientMap.put("hcp",null);
	    	     	patientUserList.add(patientMap);
	        	});
	        }
		}
		return patientUserList;
	}

	public List<Map<String,Object>> getAssociatedHCPUsers(List<String> idList) throws HillromException {
		List<Map<String,Object>> hcpUserList = new LinkedList<>();
		for(String id : idList){
	    	Clinic clinic = clinicRepository.getOne(id);
	        if(Objects.isNull(clinic)) {
	        	throw new HillromException(ExceptionConstants.HR_547);
	        } else {
	        	
	        	clinic.getClinicUserAssoc().forEach(clinicUserAssoc -> {

	        		Map<String, Object> hcpMap = new HashMap<>();
	        		UserExtension hcpUser = (UserExtension) userExtensionRepository.findOne(clinicUserAssoc.getUser().getId());
	        		hcpMap.put("hcp", hcpUser);
	        		hcpUserList.add(hcpMap);

	        	});    	
	        	
	        }
		}
		return hcpUserList;
	}

	public List<PatientUserVO> getNotAssociatedPatientUsers(String ClinicId, String searchString, String filter) throws HillromException {
		List<PatientUserVO> patientUserList = userSearchRepository.findPatientNotAssociatedToClinic(ClinicId,searchString, filter);
		return patientUserList;
	}
	
	public Clinic getClinicInfo(String clinicId) throws HillromException {
		Clinic clinic = clinicRepository.findOne(clinicId);
        if(Objects.isNull(clinic)) {
	      	throw new HillromException(ExceptionConstants.HR_548);
        } else {
        	return clinic;
        }
    }
	
	public ClinicVO getClinicWithChildClinics(String clinicId) throws HillromException{
		ClinicVO clinicVO = ClinicVOBuilder.buildWithChildClinics(getClinicInfo(clinicId));
		clinicVO.setChildClinicVOs(RandomUtil.sortClinicVOListByName(clinicVO.getChildClinicVOs()));
		return clinicVO;
	}
	
	public List<Clinic> getChildClinics(String clinicId) throws HillromException {
		Clinic clinic = clinicRepository.findOne(clinicId);
        if(Objects.isNull(clinic)) {
	      	throw new HillromException(ExceptionConstants.HR_548);
        } else {
        	return clinic.getChildClinics();
        }
    }
	
	public List<User> getClinicAdmin(String clinicId) throws HillromException {
		
		List<User> clinicAdmins = new ArrayList<>();
		Clinic clinic = clinicRepository.findOne(clinicId);
		if (Objects.nonNull(clinic)) {
			List<EntityUserAssoc> userAssocList  = entityUserRepository.findByClinicIdAndUserRole(clinic.getId(), AuthoritiesConstants.CLINIC_ADMIN);
			if (Objects.nonNull(userAssocList)) {
				for(EntityUserAssoc entityUserAssoc : userAssocList){
					clinicAdmins.add(entityUserAssoc.getUser());
				}
				return RandomUtil.sortUserListByLastNameFirstName(clinicAdmins);
			}
			else 
				throw new HillromException(ExceptionConstants.HR_607);
		} else {
			throw new HillromException(ExceptionConstants.HR_544); // No such clinic exist
		}
	}
	
	public Set<User> getAllClinicAdmins() throws HillromException {
		List<Clinic> clinicList = clinicRepository.findAllWithClinicAdmins();
		if(clinicList.isEmpty()) {
			throw new HillromException(ExceptionConstants.HR_548);
		} else {
			Set<User> clinicAdminList = new HashSet<>();
			clinicList.forEach(clinic -> {
				if(Objects.nonNull(clinic.getClinicAdminId())) {
					User clinicAdminUser = userRepository.findOne(clinic.getClinicAdminId());
					clinicAdminList.add(clinicAdminUser);
				}
			});
			return clinicAdminList;
		}
    }
	
	public User associateClinicAdmin(String clinicId, Map<String,String> clinicAdminId) throws HillromException {
		Clinic clinic = clinicRepository.findOne(clinicId);
		User clinicAdminUser = userRepository.findOne(Long.parseLong(clinicAdminId.get("id"))); 
        if(Objects.isNull(clinic)) 
	      	throw new HillromException(ExceptionConstants.HR_548);
        else
        	if(Objects.isNull(clinicAdminUser) && !clinicAdminUser.getAuthorities().contains(AuthoritiesConstants.CLINIC_ADMIN))
        		throw new HillromException(ExceptionConstants.HR_538);
        	else
	        {
	        	EntityUserAssoc entityUserAssoc = entityUserRepository.findByUserIdAndClinicIdAndUserRole(clinicAdminUser.getId(), clinicId,  AuthoritiesConstants.CLINIC_ADMIN);
				if (Objects.nonNull(entityUserAssoc)) {
					throw new HillromException(ExceptionConstants.HR_539);
				}
				       		
				entityUserAssoc = new EntityUserAssoc(clinicAdminUser, clinic, AuthoritiesConstants.CLINIC_ADMIN);
				entityUserRepository.saveAndFlush(entityUserAssoc);
				return clinicAdminUser;      	
	        }
    }
	
	public String dissociateClinicAdmin(String clinicId, Map<String, String> clinicAdminId) throws HillromException {
		Clinic clinic = clinicRepository.findOne(clinicId);
		
		Long clinicAdminUserId = Long.parseLong(clinicAdminId.get("id"));
		if (Objects.nonNull(clinic)) {
			UserExtension clinicAdminUser = userExtensionRepository.findOne(clinicAdminUserId);
			EntityUserAssoc entityUserAssoc = entityUserRepository.findByUserIdAndClinicIdAndUserRole(clinicAdminUserId, clinicId,  AuthoritiesConstants.CLINIC_ADMIN);
			if (Objects.isNull(entityUserAssoc)) {
				throw new HillromException(ExceptionConstants.HR_550);
			}
			if (Objects.nonNull(clinicAdminUser)) {
				entityUserAssoc = new EntityUserAssoc(clinicAdminUser, clinic, AuthoritiesConstants.CLINIC_ADMIN);
				entityUserRepository.delete(entityUserAssoc);
				return MessageConstants.HR_289;
			} else {
				throw new HillromException(ExceptionConstants.HR_538);
			}
		} else {
			throw new HillromException(ExceptionConstants.HR_548);
		}
	}
	
	public int getAssociatedPatientUsersCountWithClinic(String clinicId) throws HillromException {
    	Clinic clinic = clinicRepository.getOne(clinicId);
        if(Objects.isNull(clinic)) {
        	throw new HillromException(ExceptionConstants.HR_547);
        } else {
        	return clinic.getClinicPatientAssoc().size();
        }
	}
	
	public Set<ClinicVO> getAssociatedClinicsForClinicAdmin(Long clinicAdminId) throws HillromException {
		UserExtension clinicAdminUser = userExtensionRepository.findOne(clinicAdminId);
		Set<ClinicVO> clinics = new HashSet<>();
	    if(Objects.isNull(clinicAdminUser)){
	    	throw new HillromException(ExceptionConstants.HR_512);
	    } else {
	    	List<EntityUserAssoc> entityUserAssocs = entityUserRepository.findByUserIdAndUserRole(clinicAdminId, AuthoritiesConstants.CLINIC_ADMIN);
	    	for(EntityUserAssoc entityUserAssoc : entityUserAssocs){
	    		clinics.add(ClinicVOBuilder.build(entityUserAssoc.getClinic()));
	    	}
	    	return clinics;
	    }
    }

	public List<Clinic> getActiveClinicsWithPatients(boolean isDeleted) {
		List<Clinic> clinics = clinicRepository.findByDeletedAndClinicPatientAssocIsNotEmpty(isDeleted);
		return clinics;
	}
	
	public Boolean isMessagesOpted(String userId){
		try {
			List<Object[]> results = clinicRepository.findByUserId(userId);
			Boolean boolValue = false;
			String strVal = "";
			for(Object[] result : results){
				strVal = String.valueOf(result[36]);
			}
			String[] strArray = strVal.split(",");
			for(String s: strArray){
				if(s.contains("1")){ 
					boolValue = true;
					break;
				}
			}
			return boolValue;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
