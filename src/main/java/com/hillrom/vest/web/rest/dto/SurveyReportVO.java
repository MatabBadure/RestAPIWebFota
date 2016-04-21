package com.hillrom.vest.web.rest.dto;

public class SurveyReportVO {

	private Long Id;
	private String questionText;
	
	public SurveyReportVO() {
		super();
	}
	public SurveyReportVO(Long id, String questionText) {
		super();
		Id = id;
		this.questionText = questionText;
	}
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	
	
}
