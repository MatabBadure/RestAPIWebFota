package com.hillrom.vest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.UserPatientAssoc;

public interface UserPatientRepository extends
		JpaRepository<UserPatientAssoc, Long> {

	@Query("from UserPatientAssoc upa where upa.user.id = ?1")
	Optional<UserPatientAssoc> findOneByUserId(Long userId);
	
	@Query("from UserPatientAssoc upa where upa.patient.id = ?1")
	Optional<UserPatientAssoc> findOneByPatientId(Long patientId);
	
	@Query("from UserPatientAssoc upa where upa.user.id = ?1 and upa.patient.id = ?2")
	Optional<UserPatientAssoc> findOneByUserIdAndQuestionId(Long userId,Long patientId);
}
