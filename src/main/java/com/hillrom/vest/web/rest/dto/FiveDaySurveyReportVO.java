package com.hillrom.vest.web.rest.dto;

import java.util.Objects;

public class FiveDaySurveyReportVO extends SurveyReportVO{
	
	private int yesCount;
	private int noCount;
	
	public FiveDaySurveyReportVO(Long id, String questionText, 
			Integer yesCount, Integer noCount){
		super(Objects.nonNull(id)?id:0,questionText);
		this.yesCount = Objects.nonNull(yesCount)?yesCount:0;
		this.noCount = Objects.nonNull(noCount)?noCount:0;	
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
		return "FiveDaySurveyReportVO [Id=" + getId() + ", questionText=" + getQuestionText() + ", yesCount=" + yesCount
				+ ", noCount=" + noCount + "]";
	}	
}
