package com.hillrom.vest.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.Announcements;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface AnnouncementsRepository extends JpaRepository<Announcements, Long> {
	
		@Query(" from Announcements announcement where id = ? and isDeleted = ? ")
		Announcements findOneById(Long id, boolean isDeleted);
	
	    @Query(" from Announcements announcement where isDeleted = ? ")
	    Page<Announcements> findAnnouncements(boolean isDeleted,Pageable pageable);
	    
	    // get Clinic iformations based on Id Deatils
	    @Query("select distinct announcement.id, announcement.name, announcement.subject, announcement.startDate, announcement.endDate, announcement.createdDate, "
	    	   + " announcement.modifiedDate, announcement.sendTo,announcement.clinicType,announcement.pdfFilePath, announcement.patientType, announcement.isDeleted "
	    	   + " from Announcements announcement, Clinic clinic where (announcement.sendTo = 'All' or announcement.sendTo = 'Clinic') "
	    	   + " and (announcement.clinicType = 'All' or announcement.clinicType = clinic.speciality) and clinic.id in ?1  and announcement.isDeleted = ?2 "
	    	   + " and CURRENT_DATE() between announcement.startDate and announcement.endDate ")
	    Page<Announcements> findAnnouncementsByClinicId(List<String> clinicIds, boolean isDeleted,Pageable pageable);
	
	   
}
