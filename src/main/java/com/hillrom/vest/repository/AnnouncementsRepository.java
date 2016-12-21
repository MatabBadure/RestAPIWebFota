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
	
	    @Query(" from Announcements announcement where isDeleted = ? ")
	    Page<Announcements> findAnnouncements(boolean isDeleted,Pageable pageable);
	    
	    // get Clinic iformations based on Id Deatils
	    @Query(" from Announcements announcement, Clinic clinic where (announcement.sendTo = 'All' or announcement.sendTo = 'Clinic') "
	    	   + " and (announcement.clinicType = 'All' or announcement.clinicType = clinic.speciality) and clinic.id = ?1  and announcement.isDeleted = ?2 "
	    	   + " and CURRENT_DATE() between announcement.startDate and announcement.endDate ")
	    List<Announcements> findOneByClinicId(String clinicId, boolean isDeleted);
	
	    @Query(value=" SELECT * from  ANNOUNCEMENTS ac, PATIENT_INFO pi "
				+ " where (ac.send_to = 'All' or ac.send_to = 'Patient') and (ac.patient_type = 'All' or pi.primary_diagnosis = ac.patient_type or "
				+ " IF(TIMESTAMPDIFF(YEAR, pi.dob, CURDATE()) > 18, 'Adult', 'Peds') = ac.patient_type )  "
				+ " and pi.id =:patientId and ac.is_deleted =:isDeleted and  ac.start_date <= now() and (ac.end_date >= now())", nativeQuery=true)
	    List<Announcements> findOneByPatientId(@Param("patientId")String patientId,@Param("isDeleted")boolean isDeleted);
	    
}
