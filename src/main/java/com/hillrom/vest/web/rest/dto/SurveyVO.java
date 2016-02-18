package com.hillrom.vest.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

import com.hillrom.vest.domain.SurveyQuestion;

public final class SurveyVO {

	private Long surveyId;
	private String surveyName;
	private List<SurveyQuestionVO> questions = new ArrayList<>();

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public String getSurveyName() {
		return surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	public List<SurveyQuestionVO> getQuestions() {
		return questions;
	}

	public void setQuestions(List<SurveyQuestionVO> questions) {
		this.questions = questions;
	}
}
