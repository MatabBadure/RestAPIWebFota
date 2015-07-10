package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.UserLoginToken;

/**
 * Spring Data JPA repository for the UserLoginToken entity.
 */
public interface UserLoginTokenRepository extends JpaRepository<UserLoginToken,String> {
	
}
