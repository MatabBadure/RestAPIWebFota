package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.User;

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
	@Query(nativeQuery=true,value=" SELECT pi.id as patient_id,pi.first_name as patient_first_name,pi.last_name as patient_last_name,pi.email as patient_email,pi.dob as patient_dob, "
		+ " u.id as user_id,u.email as user_email, u.first_name as user_first_name,u.last_name as user_last_name, u.activation_key as user_activation_Key "
		+ " FROM hillromvest_qa.PATIENT_INFO pi "
		+ " left outer join hillromvest_qa.USER_PATIENT_ASSOC upa on upa.relation_label = 'Self' and user_role = 'PATIENT' and pi.id = upa.patient_id "
		+ " left outer join hillromvest_qa.USER u on u.id = upa.user_id  where MONTH(pi.dob) = MONTH(CURDATE() + interval 90 DAY) and "
		+ " DAY(pi.dob) = DAY(CURDATE() + interval 90 DAY) and YEAR(pi.dob) + 18 = YEAR(CURDATE() + interval 90 DAY) ")
	List<Object[]> findUserDOB();
	//Hill-1852
	
}
