package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AnnouncementsPermissionRepository;
import com.hillrom.vest.repository.AnnouncementsRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.AnnouncementsDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Service
@Transactional
public class AnnouncementsService {

	private final Logger log = LoggerFactory.getLogger(AnnouncementsService.class);
	
	@Inject
	private AnnouncementsRepository announcementsRepository;
	
	@Inject
	private ClinicService clinicService;
	
	@Inject
	private HCPClinicService hcpClinicService; 
	

	@Inject
	private AnnouncementsPermissionRepository announcementsPermissionRepository;

	/**
	 * 
	 * @param announcementsDTO
	 * @return
	 * @throws HillromException
	 */
	public Announcements savAnnouncementData(AnnouncementsDTO announcementsDTO) throws HillromException{

		Announcements announcement = new Announcements();
		announcement.setName(announcementsDTO.getName());
		announcement.setSubject(announcementsDTO.getSubject());
		announcement.setStartDate(announcementsDTO.getStartDate());
		announcement.setEndDate(announcementsDTO.getEndDate());
		announcement.setCreatedDate(DateUtil.getCurrentDateAndTime());
		announcement.setModifiedDate(DateUtil.getCurrentDateAndTime());
		announcement.setSendTo(announcementsDTO.getSentTo());
		announcement.setClinicType(announcementsDTO.getClicicType());
		announcement.setPdfFilePath(announcementsDTO.getPdfFilePath());
		announcement.setPatientType(announcementsDTO.getPatientType());
		announcement.setDeleted(false);
		announcementsRepository.save(announcement);
		
        log.debug("Created New Announcement: {}", announcement);
        return announcement;
	}
	
	/**
	 * 
	 * @return
	 * @throws HillromException
	 */
	public Page<Announcements> findAllAnnouncements(Pageable pageable) throws HillromException{
		
		Page<Announcements> announcements = announcementsRepository.findAnnouncements(false,pageable);
		return announcements;
	}
 
	/**
	 * 
	 * @param id
	 * @return
	 * @throws HillromException
	 */
	public Announcements findAnnouncementById(Long id) throws HillromException{
		
		Announcements announcements = announcementsRepository.findOneById(id,false);
		return announcements;
	}





public Page<Announcements> findVisibleAnnouncementsById(String userType, Long userId,String patientId,String filterClinicId, Pageable pageable,Map<String, Boolean> sortOrder) throws HillromException{
		
 		Page<Announcements> announcementList = null; // new ArrayList<Announcements>();
 		List<String> clinicList =  new ArrayList<String>();
 		
 		if(userType.equalsIgnoreCase(AuthoritiesConstants.CLINIC_ADMIN)){
 			if(Objects.nonNull(filterClinicId)){
 				clinicList.add(filterClinicId);
 			}else{
	 			Set<ClinicVO> clinics = clinicService.getAssociatedClinicsForClinicAdmin(userId);
	 			for(ClinicVO tclinic : clinics){
	 				clinicList.add(tclinic.getId());
	 			}
 			}
 		}
 		
 		if(userType.equalsIgnoreCase(AuthoritiesConstants.HCP)){
 			List<ClinicVO> clinics = hcpClinicService.getAssociatedClinicsForHCP(userId);
 			for(ClinicVO tclinic : clinics){
 				clinicList.add(tclinic.getId());
 			}
 		}
 		
 		
 		// Check for the clinic flag to differentiate between whether the clinic id is passed or patient id is passed
		if(Objects.nonNull(clinicList) && clinicList.size() > 0){
			announcementList = announcementsRepository.findAnnouncementsByClinicId(clinicList, false,pageable);
		}
		
		if(Objects.nonNull(patientId)){
			announcementList = announcementsPermissionRepository.findAnnouncementsByPatientId(pageable,sortOrder,patientId, false);
		}
		
        
        return announcementList;
}
	 
	 
	 
 /**
  * 
  * @param announcementsDTO
  * @return
  * @throws HillromException
  */
      public Announcements updateAnnouncementById(AnnouncementsDTO announcementsDTO) throws HillromException{

    	  Announcements announcement = announcementsRepository.findOne(announcementsDTO.getId());
    	  if(Objects.nonNull(announcement))
    	  {
    			  announcement.setName(announcementsDTO.getName());
    			  announcement.setSubject(announcementsDTO.getSubject());
    			  announcement.setStartDate(announcementsDTO.getStartDate());
    			  announcement.setEndDate(announcementsDTO.getEndDate());
    			  announcement.setModifiedDate(DateUtil.getCurrentDateAndTime());
    			  announcement.setSendTo(announcementsDTO.getSentTo());    			  
    			  announcement.setClinicType(announcementsDTO.getClicicType());
    			  announcement.setPdfFilePath(announcementsDTO.getPdfFilePath());
    			  announcement.setPatientType(announcementsDTO.getPatientType());
    			  announcementsRepository.save(announcement);
    	          log.debug("updated Announcement Details: {}", announcement);
    	  }
    	  return announcement;	
      }
      
      /**
       * 
       * @param id
       * @return
       * @throws HillromException
       */
 public Announcements deleteAnnouncementById(Long id) throws HillromException{

    	  Announcements announcement = announcementsRepository.findOne(id);
    	  if(Objects.nonNull(announcement))
    	  {
    			  announcement.setDeleted(true);
    			  announcement.setModifiedDate(DateUtil.getCurrentDateAndTime());
    			  announcementsRepository.save(announcement);
    	          log.debug("updated Announcement Details: {}", announcement);
    	  }
    	  return announcement;	
      }
 

	
}
