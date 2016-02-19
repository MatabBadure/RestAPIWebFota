package com.hillrom.vest.web.rest.dto;

import java.util.LinkedList;
import java.util.List;

import com.hillrom.vest.domain.UserSurveyAnswer;

public class UserSurveyAnswerDTO {
	private Long surveyId;
	private Long userId;
	private List<UserSurveyAnswer> userSurveyAnswers = new LinkedList<>();

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<UserSurveyAnswer> getUserSurveyAnswer() {
		return userSurveyAnswers;
	}

	public void setUserSurveyAnswer(List<UserSurveyAnswer> userSurveyAnswers) {
		this.userSurveyAnswers = userSurveyAnswers;
	}

	@Override
	public String toString() {
		return "UserSurveyAnswerDTO [surveyId=" + surveyId + ", userId=" + userId + ", userSurveyAnswers="
				+ userSurveyAnswers + "]";
	}
	
}
