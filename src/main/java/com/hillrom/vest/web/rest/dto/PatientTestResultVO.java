package com.hillrom.vest.web.rest.dto;

import java.util.Date;

public class PatientTestResultVO {
	
	
	private Long id;
	private Long user;
	private String patientInfo;
	private Date testResultDate;
	private float FVC_L;
	private float FEV1_L;
	private float PEF_L_Min;
	private double FVC_P;
	private double FEV1_P;
	private float PEF_P;
	private float FEV1_TO_FVC_RATIO;
	private String comments;	
	private String lastUpdatedBy;
	
	
	public PatientTestResultVO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PatientTestResultVO(Long id, String patientInfo, Long user,
			Date testResultDate, float fVC_L, float fEV1_L,
			float pEF_L_Min, double fVC_P, double fEV1_P, float pEF_P,
			float fEV1_TO_FVC_RATIO, String comments, String lastUpdatedBy) {
		super();
		this.id = id;
		this.user = user;
		this.patientInfo = patientInfo;
		this.testResultDate = testResultDate;
		FVC_L = fVC_L;
		FEV1_L = fEV1_L;
		PEF_L_Min = pEF_L_Min;
		FVC_P = fVC_P;
		FEV1_P = fEV1_P;
		PEF_P = pEF_P;
		FEV1_TO_FVC_RATIO = fEV1_TO_FVC_RATIO;
		this.comments = comments;
		this.lastUpdatedBy = lastUpdatedBy;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUser() {
		return user;
	}
	public void setUser(Long user) {
		this.user = user;
	}
	public String getPatientInfo() {
		return patientInfo;
	}
	public void setPatientInfo(String patientInfo) {
		this.patientInfo = patientInfo;
	}
	public Date getTestResultDate() {
		return testResultDate;
	}
	public void setTestResultDate(Date testResultDate) {
		this.testResultDate = testResultDate;
	}
	public float getFVC_L() {
		return FVC_L;
	}
	public void setFVC_L(float fVC_L) {
		FVC_L = fVC_L;
	}
	public float getFEV1_L() {
		return FEV1_L;
	}
	public void setFEV1_L(float fEV1_L) {
		FEV1_L = fEV1_L;
	}
	public float getPEF_L_Min() {
		return PEF_L_Min;
	}
	public void setPEF_L_Min(float pEF_L_Min) {
		PEF_L_Min = pEF_L_Min;
	}
	public double getFVC_P() {
		return FVC_P;
	}
	public void setFVC_P(float fVC_P) {
		FVC_P = fVC_P;
	}
	public double getFEV1_P() {
		return FEV1_P;
	}
	public void setFEV1_P(float fEV1_P) {
		FEV1_P = fEV1_P;
	}
	public float getPEF_P() {
		return PEF_P;
	}
	public void setPEF_P(float pEF_P) {
		PEF_P = pEF_P;
	}
	public float getFEV1_TO_FVC_RATIO() {
		return FEV1_TO_FVC_RATIO;
	}
	public void setFEV1_TO_FVC_RATIO(float fEV1_TO_FVC_RATIO) {
		FEV1_TO_FVC_RATIO = fEV1_TO_FVC_RATIO;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	@Override
	public String toString() {
		return "PatientTestResultVO [id=" + id + ", user=" + user
				+ ", patientInfo=" + patientInfo + ", testResultDate="
				+ testResultDate + ", FVC_L=" + FVC_L + ", FEV1_L=" + FEV1_L
				+ ", PEF_L_Min=" + PEF_L_Min + ", FVC_P=" + FVC_P + ", FEV1_P="
				+ FEV1_P + ", PEF_P=" + PEF_P + ", FEV1_TO_FVC_RATIO="
				+ FEV1_TO_FVC_RATIO + ", comments=" + comments
				+ ", lastUpdatedBy=" + lastUpdatedBy + "]";
	}
}
