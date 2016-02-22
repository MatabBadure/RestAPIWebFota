package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.UserSurveyAnswer;

/**
 * Spring Data JPA repository for the SurveyQuestion entity.
 */

public interface UserSurveyAnswerRepository extends JpaRepository<UserSurveyAnswer, Long> {
	@Query("select count(usa) from UserSurveyAnswer usa where usa.user.id =?1 and usa.survey.id =?2")
	Integer findCountByUserIdAndSurveyId(Long userId, Long SurveyId);
}
