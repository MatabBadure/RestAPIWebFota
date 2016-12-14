package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.User;

//Hill-1852
import java.time.LocalDate;
//Hill-1852

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

    
    //Hill-1852
    @Query(" SELECT pi.id, pi.firstName, pi.lastName, pi.email, pi.dob "
            + " ,uupa.id ,uupa.email, uupa.firstName ,uupa.lastName, uupa.activationKey "
            + " FROM PatientInfo pi "
            + " left join pi.userPatientAssoc upa "
            + " left join upa.userPatientAssocPK.user uupa  "
            + " where upa.relationshipLabel = 'Caregiver' and upa.userRole = 'CARE_GIVER' and "
    		+ " MONTH(pi.dob) = MONTH(?1) and DAY(pi.dob) = DAY(?1) and YEAR(pi.dob) + 18 = YEAR(?1) ")
       List<Object[]> findUserDOB(LocalDate minDate);
     //Hill-1852
       
}
