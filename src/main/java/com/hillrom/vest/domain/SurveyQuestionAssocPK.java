package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class SurveyQuestionAssocPK implements Serializable{
	private static final long serialVersionUID = 1L;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_id", referencedColumnName="id")
    private Survey survey;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id", referencedColumnName="id") 
    private SurveyQuestion surveyQuestion;
	
	public SurveyQuestionAssocPK() {
		super();
	}

	public SurveyQuestionAssocPK(Survey survey, SurveyQuestion surveyQuestion) {
		super();
		this.survey = survey;
		this.surveyQuestion = surveyQuestion;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((survey == null) ? 0 : survey.hashCode());
		result = prime * result + ((surveyQuestion == null) ? 0 : surveyQuestion.hashCode());
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
		SurveyQuestionAssocPK other = (SurveyQuestionAssocPK) obj;
		if (survey == null) {
			if (other.survey != null)
				return false;
		} else if (!survey.equals(other.survey))
			return false;
		if (surveyQuestion == null) {
			if (other.surveyQuestion != null)
				return false;
		} else if (!surveyQuestion.equals(other.surveyQuestion))
			return false;
		return true;
	}

	
}
