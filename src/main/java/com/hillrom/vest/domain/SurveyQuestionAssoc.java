package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "SURVEY_QUESTIONS_ASSOC")
@AssociationOverrides({
		@AssociationOverride(name = "surveyQuestionAssocPK.survey", joinColumns = @JoinColumn(name = "survey_id", referencedColumnName = "id") ),
		@AssociationOverride(name = "surveyQuestionAssocPK.surveyQuestion", joinColumns = @JoinColumn(name = "question_id", referencedColumnName = "id") ) })
public class SurveyQuestionAssoc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2117934385735676905L;
	
	public SurveyQuestionAssoc(){
		super();
	}

	@EmbeddedId
	private SurveyQuestionAssocPK surveyQuestionAssocPK;

	public SurveyQuestionAssoc(SurveyQuestionAssocPK surveyQuestionAssocPK) {
		this.surveyQuestionAssocPK = surveyQuestionAssocPK;
	}

	public SurveyQuestionAssoc(SurveyQuestion surveyQuestion, Survey survey) {
		this(new SurveyQuestionAssocPK(survey, surveyQuestion));
	}

	public SurveyQuestion getQuestion() {
		return surveyQuestionAssocPK.getSurveyQuestion();
	}

	public Survey getSurvey() {
		return surveyQuestionAssocPK.getSurvey();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((surveyQuestionAssocPK == null) ? 0 : surveyQuestionAssocPK.hashCode());
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
		SurveyQuestionAssoc other = (SurveyQuestionAssoc) obj;
		if (surveyQuestionAssocPK == null) {
			if (other.surveyQuestionAssocPK != null)
				return false;
		} else if (!surveyQuestionAssocPK.equals(other.surveyQuestionAssocPK))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SurveyQuestionAssoc [id=" + surveyQuestionAssocPK + ", survey=" + surveyQuestionAssocPK.getSurvey() + ", question="
				+ surveyQuestionAssocPK.getSurveyQuestion() + "]";
	}
}
