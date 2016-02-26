package com.hillrom.vest.repository;

public class NintyDaySurveyReportVO {
	
	public NintyDaySurveyReportVO(String pName, String dob, String pPhoneNumber, String pEmail, String hoursOfUse,
			String serNumver) {
		super();
		this.pName = pName;
		this.dob = dob;
		this.pPhoneNumber = pPhoneNumber;
		this.pEmail = pEmail;
		this.hoursOfUse = hoursOfUse;
		this.serNumver = serNumver;
	}
	/*
	 *All the fields are String as BD has String 
	 *Value in no manipulation done based on datatype
	 **/
	private String pName;
	private String dob;
	private String pPhoneNumber;
	private String pEmail;
	private String hoursOfUse;
	private String serNumver;
	public String getpName() {
		return pName;
	}
	public void setpName(String pName) {
		this.pName = pName;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getpPhoneNumber() {
		return pPhoneNumber;
	}
	public void setpPhoneNumber(String pPhoneNumber) {
		this.pPhoneNumber = pPhoneNumber;
	}
	public String getpEmail() {
		return pEmail;
	}
	public void setpEmail(String pEmail) {
		this.pEmail = pEmail;
	}
	public String getHoursOfUse() {
		return hoursOfUse;
	}
	public void setHoursOfUse(String hoursOfUse) {
		this.hoursOfUse = hoursOfUse;
	}
	public String getSerNumver() {
		return serNumver;
	}
	public void setSerNumver(String serNumver) {
		this.serNumver = serNumver;
	}
	@Override
	public String toString() {
		return "NintyDaySurveyReportVO [pName=" + pName + ", dob=" + dob + ", pPhoneNumber=" + pPhoneNumber
				+ ", pEmail=" + pEmail + ", hoursOfUse=" + hoursOfUse + ", serNumver=" + serNumver + "]";
	}
	

}
