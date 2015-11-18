package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.EntityUserAssoc;
import com.hillrom.vest.domain.EntityUserAssocPK;

public interface EntityUserRepository extends JpaRepository<EntityUserAssoc, EntityUserAssocPK> {

	@Query("from EntityUserAssoc eua where eua.entityUserAssocPK.clinic.id = ?1 and eua.userRole = ?2")
	List<EntityUserAssoc> findByClinicIdAndUserRole(String clinicId, String userRole);
	
	@Query("from EntityUserAssoc eua where eua.entityUserAssocPK.user.id = ?1 and eua.entityUserAssocPK.clinic.id=?2 and eua.userRole = ?3")
	EntityUserAssoc findByUserIdAndClinicIdAndUserRole(Long id, String clinicId, String userRole);

}
