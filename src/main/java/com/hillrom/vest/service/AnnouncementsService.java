package com.hillrom.vest.service;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AnnouncementsRepository;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.AnnouncementsDTO;

@Service
@Transactional
public class AnnouncementsService {

	private final Logger log = LoggerFactory.getLogger(AnnouncementsService.class);
	
	@Inject
	private AnnouncementsRepository announcementsRepository;

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
		announcement.setIsDeleted("0");
		announcementsRepository.save(announcement);
        log.debug("Created New Announcement: {}", announcement);
        return announcement;
	}
	
	/**
	 * 
	 * @return
	 * @throws HillromException
	 */
 public List<Announcements> findAnnouncementData() throws HillromException{
		
		List<Announcements> announcements = announcementsRepository.findAnnouncements("0");
		return announcements;
	}
 
/**
 * 
 * @param id
 * @return
 * @throws HillromException
 */
 public Announcements findAnnouncementById(Long id) throws HillromException{
		
		Announcements announcements = announcementsRepository.findObeById(id,"0");
		return announcements;
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
    			  announcement.setIsDeleted("1");
    			  announcement.setModifiedDate(DateUtil.getCurrentDateAndTime());
    			  announcementsRepository.save(announcement);
    	          log.debug("updated Announcement Details: {}", announcement);
    	  }
    	  return announcement;	
      }
	
}
