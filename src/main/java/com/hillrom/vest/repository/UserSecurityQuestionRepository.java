package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.UserSecurityQuestion;

public interface UserSecurityQuestionRepository extends
		JpaRepository<UserSecurityQuestion, Long> {

	@Query("from UserSecurityQuestion usq where usq.user.id = ?1")
	Optional<UserSecurityQuestion> findOneByUserId(Long userId);
	
	@Query("from UserSecurityQuestion usq where usq.securityQuestion.id = ?1")
	Optional<UserSecurityQuestion> findOneByQuestionId(Long questionId);
	
	@Query("from UserSecurityQuestion usq where usq.user.id = ?1 and usq.securityQuestion.id = ?2")
	Optional<UserSecurityQuestion> findOneByUserIdAndQuestionId(Long userId,Long questionId);
	
	@Query("from UserSecurityQuestion usq where usq.user.id = ?1")
	List<UserSecurityQuestion> findByUserId(Long userId);
}
