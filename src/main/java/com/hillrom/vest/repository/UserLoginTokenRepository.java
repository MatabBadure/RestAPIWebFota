package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.UserLoginToken;

/**
 * Spring Data JPA repository for the UserLoginToken entity.
 */
public interface UserLoginTokenRepository extends JpaRepository<UserLoginToken,String> {

	@Query("from UserLoginToken token where token.user.id = ?1")
	List<UserLoginToken> findAllByUserId(Long userId);
	
}
