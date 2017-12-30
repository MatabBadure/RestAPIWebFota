package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.User;


import java.time.LocalDate;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(DateTime dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    @Query("from User user where LOWER(user.email) = ?1")
    Optional<User> findOneByEmail(String email);

    @Query("from User user where LOWER(user.hillromId) = ?1")
    Optional<User> findOneByHillromId(String hillromId);

    @Override
    void delete(User t);

    @Query("from User user where LOWER(user.email) = ?1 or LOWER(user.hillromId) = ?1")
	Optional<User> findOneByEmailOrHillromId(String login);
    
    @Query("from User user where activated = false and activationLinkSentDate between ?1 and ?2")
    List<User> findAllByActivatedIsFalseAndActivationLinkSentDateBetweeen(DateTime dateTime1, DateTime dateTime2);
       
   /*@Query(" SELECT pi.id, pi.firstName, pi.lastName, uupa.email, pi.dob "
    		   + " ,uupa.id ,uupa.email, uupa.firstName ,uupa.lastName, uupa.activationKey "
    		   + " FROM PatientInfo pi "
    		   + " left join pi.userPatientAssoc upa "
    		   + " left join upa.userPatientAssocPK.user uupa  "
    		   + " where MONTH(pi.dob) = ?2  "
    		   + " and DAY(pi.dob) = ?3 and YEAR(pi.dob) + 18 = ?1 "
    		   + " and upa.relationshipLabel IN ('Self') and upa.userRole = 'PATIENT'")
       List<Object[]> findUserPatientsMaturityDobAfterDays(int year, int month, int day);*/

    @Query(nativeQuery=true,
  			 value=" SELECT pi.id as patientId ,us.id as userId from USER us"
  			 		+ " left outer join USER_PATIENT_ASSOC upa on upa.user_id = us.id "
  			 		+ " left outer join PATIENT_INFO pi on pi.id = upa.patient_id "
  			 		+ " where MONTH(pi.dob) = ?2  "
  	    		    + " and DAY(pi.dob) = ?3 and YEAR(pi.dob) + 18 = ?1 "
  			 		+ "	and upa.relation_label = 'Self' and upa.user_role = 'PATIENT' ")
    List<Object[]> findUserPatientsMaturityDobAfterDays(int year, int month, int day);
       

    @Query("from User user where LOWER(user.email) = ?1")
    User findUserOneByEmail(String email);
    
}
