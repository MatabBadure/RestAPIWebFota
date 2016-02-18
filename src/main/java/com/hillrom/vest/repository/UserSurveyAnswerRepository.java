package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.UserSurveyAnswer;

/**
 * Spring Data JPA repository for the SurveyQuestion entity.
 */

public interface UserSurveyAnswerRepository extends JpaRepository<UserSurveyAnswer, Long> {

}
