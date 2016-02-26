package com.hillrom.vest.repository;

public class NintyDaysResultSetVO {
	public NintyDaysResultSetVO(Long userId, Long questionId, String questionText, String answerValue) {
		super();
		this.userId = userId;
		this.questionId = questionId;
		this.questionText = questionText;
		this.answerValue = answerValue;
	}

	private Long userId;
	private Long questionId;
	private String questionText;
	private String answerValue;
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
	@Override
	public String toString() {
		return "NightyDaysResultSetVO [userId=" + userId + ", questionId=" + questionId + ", questionText="
				+ questionText + ", answerValue=" + answerValue + "]";
	}	
}
