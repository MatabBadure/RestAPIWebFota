package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.SurveyQuestion;

/**
 * Spring Data JPA repository for the SurveyQuestion entity.
 * 
 */

public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {

}
