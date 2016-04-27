package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.EntityUserAssoc;
import com.hillrom.vest.domain.EntityUserAssocPK;

public interface EntityUserRepository extends JpaRepository<EntityUserAssoc, EntityUserAssocPK> {

	@Query("from EntityUserAssoc eua where eua.entityUserAssocPK.clinic.id = ?1 and eua.userRole = ?2")
	List<EntityUserAssoc> findByClinicIdAndUserRole(String clinicId, String userRole);
	
	@Query("from EntityUserAssoc eua where eua.entityUserAssocPK.user.id = ?1 and eua.entityUserAssocPK.clinic.id=?2 and eua.userRole = ?3")
	EntityUserAssoc findByUserIdAndClinicIdAndUserRole(Long id, String clinicId, String userRole);
	
	@Query("from EntityUserAssoc eua where eua.entityUserAssocPK.user.id = ?1 and eua.userRole = ?2")
	List<EntityUserAssoc> findByUserIdAndUserRole(Long Id, String userRole);
	
	@Query(nativeQuery=true,value="SELECT user_id from ENTITY_USER_ASSOC eua join CLINIC_PATIENT_ASSOC cpa "
			+ "on eua.user_id = :userId and cpa.patient_id = :patientId and cpa.is_active = :isActive and eua.entity_id = cpa.clinic_id")
	Long returnUserIdIfAssociationExists(@Param("userId")Long userId,@Param("patientId")String patientId,@Param("isActive")boolean isActive);
}
