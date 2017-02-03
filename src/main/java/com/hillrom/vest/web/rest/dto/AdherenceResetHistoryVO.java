package com.hillrom.vest.web.rest.dto;

//Hill-2133
public class AdherenceResetHistoryVO {
	private String resetStartDate;
	private String resetDate;
	private String resetTime;
	private String justification;
	private String comments;
	
	public AdherenceResetHistoryVO(String resetStartDate,String resetDate,String resetTime, String justification,
			String comments){
		super();
		this.resetStartDate=resetStartDate;
		this.resetDate=resetDate;
		this.resetTime=resetTime;
		this.justification=justification;
		this.comments=comments;
	}
	
	public String getResetStartDate() {
		return resetStartDate;
	}
	public void setResetStartDate(String resetStartDate) {
		this.resetStartDate = resetStartDate;
	}
	public String getResetDate() {
		return resetDate;
	}
	public void setResetDate(String resetDate) {
		this.resetDate = resetDate;
	}
	public String getResetTime() {
		return resetTime;
	}
	public void setResetTime(String resetTime) {
		this.resetTime = resetTime;
	}
	public String getJustification() {
		return justification;
	}
	public void setJustification(String justification) {
		this.justification = justification;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
}
