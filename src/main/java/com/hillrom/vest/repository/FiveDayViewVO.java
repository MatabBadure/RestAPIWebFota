package com.hillrom.vest.repository;

public class FiveDayViewVO {
	private String patientName;
	private String serialNumber;
	private String date;
	private String comment;
	public FiveDayViewVO(String patientName, String serialNumber, String date, String comment) {
		super();
		this.patientName = patientName;
		this.serialNumber = serialNumber;
		this.date = date;
		this.comment = comment;
	}

	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}	
}
