package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.ISO8601LocalDateDeserializer;
import com.hillrom.vest.domain.util.MMDDYYYYLocalDateSerializer;

@Entity
@Audited
@Table(name = "USER_SURVEY_ANSWERS")
public class UserSurveyAnswer implements Serializable {

	/**
	 * 
	 */

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

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = MMDDYYYYLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "compl_date")
	private LocalDate completionDate = LocalDate.now();

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

	public LocalDate getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(LocalDate completionDate) {
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
