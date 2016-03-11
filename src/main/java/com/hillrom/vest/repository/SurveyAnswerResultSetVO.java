package com.hillrom.vest.repository;

public class SurveyAnswerResultSetVO {
	public SurveyAnswerResultSetVO(Long userId, Long questionId, String questionText, String answerValue, String answerValue2) {
		super();
		this.userId = userId;
		this.questionId = questionId;
		this.questionText = questionText;
		this.answerValue = answerValue;
		this.answerValue2 = answerValue2;
	}

	private Long userId;
	private Long questionId;
	private String questionText;
	private String answerValue;
	private String answerValue2;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getQuestionId() {
		return questionId;
	}
	
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public String getAnswerValue() {
		return answerValue;
	}
	public void setAnswerValue(String answerValue) {
		this.answerValue = answerValue;
	}
	public String getAnswerValue2() {
		return answerValue2;
	}
	public void setAnswerValue2(String answerValue2) {
		this.answerValue2 = answerValue2;
	}
	@Override
	public String toString() {
		return "NightyDaysResultSetVO [userId=" + userId + ", questionId=" + questionId + ", questionText="
				+ questionText + ", answerValue=" + answerValue + "]";
	}	
}
