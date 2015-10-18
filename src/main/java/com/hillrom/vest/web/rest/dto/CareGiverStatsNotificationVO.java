package com.hillrom.vest.web.rest.dto;

import java.math.BigInteger;

public class CareGiverStatsNotificationVO {
	
	public CareGiverStatsNotificationVO(BigInteger patientUserid, String patientFirstname, String patientLastname,
			BigInteger careGiverId, String careGiverName, int missedTherapyCount, boolean isSettingsDeviated,
			boolean isHMRCompliant, String cGEmail, boolean isHcpAcceptHMRNotification,
			boolean isHcpAcceptSettingsNotification, boolean isHcpAcceptTherapyNotification) {
		this.patientUserid = patientUserid;
		this.patientFirstname = patientFirstname;
		this.patientLastname = patientLastname;
		this.careGiverId = careGiverId;
		this.careGiverName = careGiverName;
		this.missedTherapyCount = missedTherapyCount;
		this.isSettingsDeviated = isSettingsDeviated;
		this.isHMRCompliant = isHMRCompliant;
		this.CGEmail = cGEmail;
		this.isHcpAcceptHMRNotification = isHcpAcceptHMRNotification;
		this.isHcpAcceptSettingsNotification = isHcpAcceptSettingsNotification;
		this.isHcpAcceptTherapyNotification = isHcpAcceptTherapyNotification;
	}
	
	private BigInteger patientUserid;
	private String patientFirstname;
	private String patientLastname;
	private BigInteger careGiverId;
	private String careGiverName;
	
	private int missedTherapyCount;
	private boolean isSettingsDeviated;
	private boolean isHMRCompliant;
	
	private String CGEmail;
	
	private boolean isHcpAcceptHMRNotification;
	private boolean isHcpAcceptSettingsNotification;
	private boolean isHcpAcceptTherapyNotification;
	
	
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
	public BigInteger getCareGiverId() {
		return careGiverId;
	}
	public void setCareGiverId(BigInteger careGiverId) {
		this.careGiverId = careGiverId;
	}
	public String getCareGiverName() {
		return careGiverName;
	}
	public void setCareGiverName(String careGiverName) {
		this.careGiverName = careGiverName;
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
	public boolean isHcpAcceptHMRNotification() {
		return isHcpAcceptHMRNotification;
	}
	public void setHcpAcceptHMRNotification(boolean isHcpAcceptHMRNotification) {
		this.isHcpAcceptHMRNotification = isHcpAcceptHMRNotification;
	}
	public boolean isHcpAcceptSettingsNotification() {
		return isHcpAcceptSettingsNotification;
	}
	public void setHcpAcceptSettingsNotification(boolean isHcpAcceptSettingsNotification) {
		this.isHcpAcceptSettingsNotification = isHcpAcceptSettingsNotification;
	}
	public boolean isHcpAcceptTherapyNotification() {
		return isHcpAcceptTherapyNotification;
	}
	public void setHcpAcceptTherapyNotification(boolean isHcpAcceptTherapyNotification) {
		this.isHcpAcceptTherapyNotification = isHcpAcceptTherapyNotification;
	}
	public String getCGEmail() {
		return CGEmail;
	}
	public void setCGEmail(String cGEmail) {
		CGEmail = cGEmail;
	}
	
}
