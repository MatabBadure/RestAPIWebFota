package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

import com.hillrom.vest.repository.FiveDaySurveyReportVO;
import com.hillrom.vest.repository.NintyDaysResultSetVO;
import com.hillrom.vest.repository.ThirtyDaySurveyReportVO;

@Entity
@Audited
@Table(name = "USER_SURVEY_ANSWERS")
@NamedNativeQueries({
		@NamedNativeQuery(name = "fiveDaySurveyReport", query = "select ques.id as id, ques.question_text as questionText, ROUND (( "
				+ "LENGTH(group_concat(answer_value_1)) "
				+ "- LENGTH( REPLACE ( group_concat(answer_value_1), 'Yes', '') ) " + ") / LENGTH('Yes')) AS yesCount,"
				+ "ROUND((LENGTH(group_concat(answer_value_1)) "
				+ "- LENGTH( REPLACE ( group_concat(answer_value_1), 'No', '')) "
				+ ") / LENGTH('No')) AS noCount , compl_date as compDate "
				+ "from QUESTIONS ques left outer join USER_SURVEY_ANSWERS usa "
				+ "on ques.id = usa.question_id and usa.survey_id = 1 " + "and DATE(usa.compl_date) between ? and ? "
				+ "where ques.id in (6,7,8,9,10,11,12)  "
				+ "group by ques.id ", resultSetMapping = "fiveDaySurveyReportMapping"),

		@NamedNativeQuery(name = "thirtyDaySurveyReport", query = "select ques.id as id, ques.question_text as questionText,"
				+ "ROUND (( LENGTH(group_concat(answer_value_1)) - LENGTH( REPLACE ( group_concat(answer_value_1),  "
				+ "'Strongly disagree', '') ) ) / LENGTH('Strongly disagree')) AS stronglyDisagreeCount, "
				+ "ROUND((LENGTH(group_concat(answer_value_1)) - LENGTH( REPLACE ( group_concat(answer_value_1),  "
				+ "'Somewhat disagree', '')) ) / LENGTH('Somewhat disagree')) AS somewhatDisagreeCount , "
				+ "ROUND((LENGTH(group_concat(answer_value_1)) - LENGTH( REPLACE ( group_concat(answer_value_1),  "
				+ "'Neutral', '')) ) / LENGTH('Neutral')) AS neutralCount , "
				+ "ROUND((LENGTH(group_concat(answer_value_1)) - LENGTH( REPLACE ( group_concat(answer_value_1),  "
				+ "'Somewhat agree', '')) ) / LENGTH('Somewhat agree')) AS somewhatAgreeCount,"
				+ "ROUND (( LENGTH(group_concat(answer_value_1)) - LENGTH( REPLACE ( group_concat(answer_value_1),  "
				+ "'Strongly Agree', '') ) ) / LENGTH('Strongly Agree')) AS stronglyAgreeCount,  "
				+ "ROUND (( LENGTH(group_concat(answer_value_1)) - LENGTH( REPLACE ( group_concat(answer_value_1),  "
				+ "'Unable to access', '') ) ) / LENGTH('Unable to access')) AS unableToAccessCount, "
				+ "compl_date as compDate from QUESTIONS ques left outer join USER_SURVEY_ANSWERS usa "
				+ "on ques.id = usa.question_id and usa.survey_id = 2 and DATE(usa.compl_date) between ? and ? "
				+ "where ques.id in (27,28,29,30,31,32,33)  "
				+ "group by ques.id  ", resultSetMapping = "thirtyDaySurveyReportMapping"),
		@NamedNativeQuery(name = "nintyDaySurveyReport", query = "select usa.user_id as userId, usa.question_id as questionId, "
				+ "ques.question_text as questionText, usa.answer_value_1 as answerValue1 "
				+ "from USER_SURVEY_ANSWERS usa left outer join  QUESTIONS ques on ques.id = usa.question_id "
				+ "where usa.survey_id = 3 AND usa.question_id in (41,42,43,49,50,51) and  DATE(usa.compl_date) between ? and ? "
				+ "group by usa.user_id,usa.question_id", resultSetMapping = "nintyDaySurveyReportMapping") })
@SqlResultSetMappings({
		@SqlResultSetMapping(name = "fiveDaySurveyReportMapping", classes = @ConstructorResult(targetClass = FiveDaySurveyReportVO.class, columns = {
				@ColumnResult(name = "id", type = Long.class), @ColumnResult(name = "questionText"),
				@ColumnResult(name = "yesCount", type = Integer.class),
				@ColumnResult(name = "noCount", type = Integer.class) }) ),
		@SqlResultSetMapping(name = "thirtyDaySurveyReportMapping", classes = @ConstructorResult(targetClass = ThirtyDaySurveyReportVO.class, columns = {
				@ColumnResult(name = "id", type = Long.class), @ColumnResult(name = "questionText"),
				@ColumnResult(name = "stronglyDisagreeCount", type = Integer.class),
				@ColumnResult(name = "somewhatDisagreeCount", type = Integer.class),
				@ColumnResult(name = "neutralCount", type = Integer.class),
				@ColumnResult(name = "somewhatAgreeCount", type = Integer.class),
				@ColumnResult(name = "stronglyAgreeCount", type = Integer.class),
				@ColumnResult(name = "unableToAccessCount", type = Integer.class), }) ),
		@SqlResultSetMapping(name = "nintyDaySurveyReportMapping", classes = @ConstructorResult(targetClass = NintyDaysResultSetVO.class, columns = {
				@ColumnResult(name = "userId", type = Long.class),
				@ColumnResult(name = "questionId", type = Long.class),
				@ColumnResult(name = "questionText", type = String.class),
				@ColumnResult(name = "answerValue1", type = String.class) }) ) })

public class UserSurveyAnswer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "survey_id")
	private Survey survey;

	@ManyToOne
	@JoinColumn(name = "question_id")
	private SurveyQuestion surveyQuestion;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "answer_value_1")
	private String answerValue1;

	@Column(name = "answer_value_2")
	private String answerValue2;

	@Column(name = "answer_value_3")
	private String answerValue3;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(name = "compl_date")
	private DateTime completionDate = DateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public SurveyQuestion getSurveyQuestion() {
		return surveyQuestion;
	}

	public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
		this.surveyQuestion = surveyQuestion;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAnswerValue1() {
		return answerValue1;
	}

	public void setAnswerValue1(String answerValue1) {
		this.answerValue1 = answerValue1;
	}

	public String getAnswerValue2() {
		return answerValue2;
	}

	public void setAnswerValue2(String answerValue2) {
		this.answerValue2 = answerValue2;
	}

	public String getAnswerValue3() {
		return answerValue3;
	}

	public void setAnswerValue3(String answerValue3) {
		this.answerValue3 = answerValue3;
	}

	public DateTime getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(DateTime completionDate) {
		this.completionDate = completionDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((survey == null) ? 0 : survey.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserSurveyAnswer other = (UserSurveyAnswer) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (survey == null) {
			if (other.survey != null)
				return false;
		} else if (!survey.equals(other.survey))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserSurveyAnswer [id=" + id + ", survey=" + survey + ", surveyQuestion=" + surveyQuestion + ", user="
				+ user + ", answerValue1=" + answerValue1 + ", answerValue2=" + answerValue2 + ", answerValue3="
				+ answerValue3 + ", completionDate=" + completionDate + "]";
	}
}
