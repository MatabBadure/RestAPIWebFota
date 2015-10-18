package com.hillrom.vest.web.rest.dto;

import java.math.BigInteger;

public class PatientStatsVO {

	private BigInteger patientUserid;
	private String patientFirstname;
	private String patientLastname;
	private int missedTherapyCount;
	private boolean isSettingsDeviated;
	private boolean isHMRCompliant;
	
	public PatientStatsVO(BigInteger patientUserid, String patientFirstname, String patientLastname,
			int missedTherapyCount, boolean isSettingsDeviated, boolean isHMRCompliant) {
		super();
		this.patientUserid = patientUserid;
		this.patientFirstname = patientFirstname;
		this.patientLastname = patientLastname;
		this.missedTherapyCount = missedTherapyCount;
		this.isSettingsDeviated = isSettingsDeviated;
		this.isHMRCompliant = isHMRCompliant;
	}
	
	public BigInteger getPatientUserid() {
		return patientUserid;
	}
	public void setPatientUserid(BigInteger patientUserid) {
		this.patientUserid = patientUserid;
	}
	public String getPatientFirstname() {
		return patientFirstname;
	}
	public void setPatientFirstname(String patientFirstname) {
		this.patientFirstname = patientFirstname;
	}
	public String getPatientLastname() {
		return patientLastname;
	}
	public void setPatientLastname(String patientLastname) {
		this.patientLastname = patientLastname;
	}
	public int getMissedTherapyCount() {
		return missedTherapyCount;
	}
	public void setMissedTherapyCount(int missedTherapyCount) {
		this.missedTherapyCount = missedTherapyCount;
	}
	public boolean isSettingsDeviated() {
		return isSettingsDeviated;
	}
	public void setSettingsDeviated(boolean isSettingsDeviated) {
		this.isSettingsDeviated = isSettingsDeviated;
	}
	public boolean isHMRCompliant() {
		return isHMRCompliant;
	}
	public void setHMRCompliant(boolean isHMRCompliant) {
		this.isHMRCompliant = isHMRCompliant;
	}
	
	
}
