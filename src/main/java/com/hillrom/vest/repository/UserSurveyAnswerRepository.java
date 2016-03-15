package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.UserSurveyAnswer;
import com.hillrom.vest.web.rest.dto.FiveDaySurveyReportVO;
import com.hillrom.vest.web.rest.dto.SurveyAnswerResultSetVO;
import com.hillrom.vest.web.rest.dto.ThirtyDaySurveyReportVO;

/**
 * Spring Data JPA repository for the SurveyQuestion entity.
 */

public interface UserSurveyAnswerRepository extends JpaRepository<UserSurveyAnswer, Long> {
	@Query("select count(usa) from UserSurveyAnswer usa where usa.user.id =?1 and usa.survey.id =?2")
	Integer findCountByUserIdAndSurveyId(Long userId, Long SurveyId);

	@Query(name = "fiveDaySurveyReport")
	List<FiveDaySurveyReportVO> fiveDaySurveyReport(@Param("from")String fromDateTime, @Param("to")String toDateTime,@Param("questionIds")List<Long> questionIds);

	@Query(name = "thirtyDaySurveyReport")
	List<ThirtyDaySurveyReportVO> thirtyDaySurveyReport(@Param("from")String fromDateTime, @Param("to")String toDateTime,@Param("questionIds")List<Long> questionIds);
	
	@Query("Select COUNT(distinct usa.user) from UserSurveyAnswer usa where usa.survey.id =?1 and usa.completionDate between ?2 and ?3")
	Integer findSurveyCountByDateRange(Long surveyId, DateTime dateTime, DateTime dateTime2);
	
	@Query("from UserSurveyAnswer usa where usa.survey.id =?1 and usa.completionDate between ?2 and ?3")
	List<UserSurveyAnswer> findSurveyBySurveyIdAndCompletionDate(Long surveyId, DateTime fromDateTime, DateTime toDateTime);
	
	@Query("from UserSurveyAnswer usa where usa.surveyQuestion.id =?1")
	List<UserSurveyAnswer> findSurveyAnswerByQuestionId(Long surveyQuestionId);
	
	@Query(name = "nintyDaySurveyReport")
	List<SurveyAnswerResultSetVO> nintyDaySurveyReport(String fromDateTime, String toDateTime);
	
	@Query(name = "fiveDaySurveyReportView")
	List<SurveyAnswerResultSetVO> fiveDaySurveyViewReport(Long questionId, String fromDateTime, String toDateTime);
}

