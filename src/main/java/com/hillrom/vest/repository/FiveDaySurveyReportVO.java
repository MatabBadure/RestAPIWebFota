package com.hillrom.vest.repository;

import java.util.Objects;

public class FiveDaySurveyReportVO {
	
	private Long Id;
	private String questionText;
	private int yesCount;
	private int noCount;
	
	public FiveDaySurveyReportVO(Long id, String questionText, 
			Integer yesCount, Integer noCount){
		this.Id = Objects.nonNull(id)?id:0;
		this.questionText = questionText;
		this.yesCount = Objects.nonNull(yesCount)?yesCount:0;
		this.noCount = Objects.nonNull(noCount)?noCount:0;	
	}
	
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getQuestion() {
		return questionText;
	}
	public void setQuestion(String question) {
		this.questionText = question;
	}
	public int getYesCount() {
		return yesCount;
	}
	public void setYesCount(int yesCount) {
		this.yesCount = yesCount;
	}
	public int getNoCount() {
		return noCount;
	}
	public void setNoCount(int noCount) {
		this.noCount = noCount;
	}
	@Override
	public String toString() {
		return "FiveDaySurveyReportVO [Id=" + Id + ", questionText=" + questionText + ", yesCount=" + yesCount
				+ ", noCount=" + noCount + "]";
	}	
}
