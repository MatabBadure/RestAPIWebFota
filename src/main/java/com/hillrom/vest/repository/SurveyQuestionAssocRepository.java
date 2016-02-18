package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.SurveyQuestionAssoc;
import com.hillrom.vest.domain.SurveyQuestionAssocPK;

public interface SurveyQuestionAssocRepository extends JpaRepository<SurveyQuestionAssoc, SurveyQuestionAssocPK> {

	@Query("from SurveyQuestionAssoc sqa where sqa.surveyQuestionAssocPK.survey.id = ?1")
	List<SurveyQuestionAssoc> findBySurveyId(Long surveyId);

	@Query("from SurveyQuestionAssoc sqa where sqa.surveyQuestionAssocPK.surveyQuestion.id = ?1")
	List<SurveyQuestionAssoc> findOneBySurveyQuestion(Long questionId);

	@Query("from SurveyQuestionAssoc sqa where sqa.surveyQuestionAssocPK.survey.id = ?1 and sqa.surveyQuestionAssocPK.surveyQuestion.id = ?2")
	Optional<SurveyQuestionAssoc> findOneByClinicIdAndPatientId(Long surveyId, Long questionId);
}
