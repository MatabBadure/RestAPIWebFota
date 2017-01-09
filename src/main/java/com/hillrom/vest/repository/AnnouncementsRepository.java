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
	    @Query(" from Announcements announcement, Clinic clinic where (announcement.sendTo = 'All' or announcement.sendTo = 'Clinic') "
	    	   + " and (announcement.clinicType = 'All' or announcement.clinicType = clinic.speciality) and clinic.id in ?1  and announcement.isDeleted = ?2 "

	    	   + " and CURRENT_DATE() between announcement.startDate and announcement.endDate group by announcement.id")

	    Page<Announcements> findAnnouncementsByClinicId(List<String> clinicIds, boolean isDeleted,Pageable pageable);
	
	    /*
	    @Query(value=" SELECT * from  ANNOUNCEMENTS ac, PATIENT_INFO pi "
				+ " where (ac.send_to = 'All' or ac.send_to = 'Patient') and (ac.patient_type = 'All' or pi.primary_diagnosis = ac.patient_type or "
				+ " IF(TIMESTAMPDIFF(YEAR, pi.dob, CURDATE()) > 18, 'Adult', 'Peds') = ac.patient_type )  "
				+ " and pi.id =:patientId and ac.is_deleted =:isDeleted and  ac.start_date <= now() and (ac.end_date >= now())", nativeQuery=true)
	    List<Announcements> findAnnouncementsByPatientId(@Param("patientId")String patientId,@Param("isDeleted")boolean isDeleted,Pageable pageable);
	    */
	     /* @Query(" from Announcements announcement, PatientInfo patientInfo where (announcement.sendTo = 'All' or announcement.sendTo = 'Patient') "
 	   + " and (announcement.patientType = 'All' or announcement.patientType = patientInfo.primaryDiagnosis or "
	   + " IF (DATEDIFF(YEAR, pi.dob,CURRENT_DATE()) > 18, 'Adult', 'Peds') = announcement.patientType  )"
 	   + " and patientInfo.id = ?1  and announcement.isDeleted = ?2  ")
	      List<Announcements> findOneByPatientId(String patientId, boolean isDeleted);*/
	    
}
