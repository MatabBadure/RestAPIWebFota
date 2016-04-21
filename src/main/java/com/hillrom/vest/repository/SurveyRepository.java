package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.Survey;

/**
 * Spring Data JPA repository for the Survey entity.
 */

public interface SurveyRepository extends JpaRepository<Survey,Long> {

}
