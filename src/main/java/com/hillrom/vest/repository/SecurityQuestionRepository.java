package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.SecurityQuestion;

/**
 * Spring Data JPA repository for the SecurityQuestion entity.
 * 
 */
public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion,Long> {

}
