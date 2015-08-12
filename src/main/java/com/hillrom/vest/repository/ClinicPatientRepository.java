package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.ClinicPatientAssoc;
import com.hillrom.vest.domain.ClinicPatientAssocPK;

public interface ClinicPatientRepository extends
		JpaRepository<ClinicPatientAssoc, ClinicPatientAssocPK> {

	@Query("from ClinicPatientAssoc cpa where cpa.clinicPatientAssocPK.clinic.id = ?1")
	List<Optional<ClinicPatientAssoc>> findOneByClinicId(String clinicId);
	
	@Query("from ClinicPatientAssoc cpa where cpa.clinicPatientAssocPK.patient.id = ?1")
	List<Optional<ClinicPatientAssoc>> findOneByPatientId(String patientId);
	
	@Query("from ClinicPatientAssoc cpa where cpa.clinicPatientAssocPK.clinic.id = ?1 and cpa.clinicPatientAssocPK.patient.id = ?2")
	Optional<ClinicPatientAssoc> findOneByClinicIdAndPatientId(String clinicId,String patientId);
}
