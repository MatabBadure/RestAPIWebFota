package com.hillrom.vest.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.domain.Tims;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface TimsRepository extends JpaRepository<Tims, Long> {
	
    @Procedure(name = "create_patient_protocol_monarch")
    void createPatientProtocolMonarch(@Param("type_key") String typeKey,
    								  @Param("operation_type") String operationType,
    								  @Param("in_patient_id") String inPatientId,
    								  @Param("in_created_by") String inCreatedBy);
    
    @Procedure(name = "create_patient_protocol")
    void createPatientProtocol(@Param("type_key") String typeKey,
			  					 @Param("operation_type") String operationType,
			  					 @Param("in_patient_id") String inPatientId,
			  					 @Param("in_created_by") String inCreatedBy);
	

	    
}
