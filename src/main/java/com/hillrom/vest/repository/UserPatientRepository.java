package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.domain.UserPatientAssocPK;

public interface UserPatientRepository extends
		JpaRepository<UserPatientAssoc, UserPatientAssocPK> {

	@Query("from UserPatientAssoc upa where upa.userPatientAssocPK.user.id = ?1")
	List<UserPatientAssoc> findOneByUserId(Long userId);
	
	@Query("from UserPatientAssoc upa where upa.userPatientAssocPK.patient.id = ?1")
	List<UserPatientAssoc> findOneByPatientId(String patientId);
	
	@Query("from UserPatientAssoc upa where upa.userPatientAssocPK.user.id = ?1 and upa.userPatientAssocPK.patient.id = ?2")
	Optional<UserPatientAssoc> findOneByUserIdAndPatientId(Long userId,String patientId);
	
	@Query("from UserPatientAssoc upa where upa.userPatientAssocPK.user.id = ?1 and upa.userRole = ?2")
	List<UserPatientAssoc> findByUserIdAndUserRole(Long userId, String userRole);
	
	@Query("from UserPatientAssoc upa where upa.userPatientAssocPK.patient.id = ?1 and upa.userRole = ?2")
	List<UserPatientAssoc> findByPatientIdAndUserRole(String patientId, String userRole);
	
	@Query(nativeQuery=true,value="SELECT id from USER u "
			+ "join USER_PATIENT_ASSOC upa on u.id = upa.user_id and u.id = :userId "
			+ "and upa.relation_label = :relationLabel and upa.patient_id = :patientId "
			+ "left join CLINIC_USER_ASSOC cua on u.id =  cua.users_id and u.id = :userId "
			+ "join CLINIC_PATIENT_ASSOC cpa on cua.clinics_id = cpa.clinic_id and cpa.patient_id = :patientId and cpa.is_active = :isActive")
	Long returnUserIdIfAssociationExists(@Param("userId")Long userId,@Param("relationLabel")String relationLabel,@Param("patientId")String patientId,@Param("isActive")boolean isActive);
}
