package com.hillrom.vest.web.rest.dto;

import java.math.BigInteger;

public class ClinicStatsNotificationVO {

	private BigInteger patientUserid;
	private String patientFirstname;
	private String patientLastname;
	private BigInteger hcpId;
	private BigInteger hcpUserId;
	private String hcpFirstname;
	private String hcpLastname;
	private String clinicId;
	private String clinicName;
	private BigInteger clinicAdminId;
	private int missedTherapyCount;
	private boolean isSettingsDeviated;
	private boolean isHMRCompliant;
	private boolean isHcpAcceptHMRNotification;
	private boolean isHcpAcceptSettingsNotification;
	private boolean isHcpAcceptTherapyNotification;
	private String email;

	public ClinicStatsNotificationVO(BigInteger patientUserid,
			String patientFirstname, String patientLastname, BigInteger hcpId,
			BigInteger hcpUserId, String hcpFirstname, String hcpLastname,
			String clinicId,String clinicName,BigInteger clinicAdminId ,
			int missedTherapyCount, boolean isSettingsDeviated,
			boolean isHMRCompliant, boolean isHcpAcceptHMRNotification,
			boolean isHcpAcceptSettingsNotification,
			boolean isHcpAcceptTherapyNotification,
			String email) {
		super();
		this.patientUserid = patientUserid;
		this.patientFirstname = patientFirstname;
		this.patientLastname = patientLastname;
		this.hcpId = hcpId;
		this.hcpUserId = hcpUserId;
		this.hcpFirstname = hcpFirstname;
		this.hcpLastname = hcpLastname;
		this.clinicId = clinicId;
		this.clinicAdminId = clinicAdminId;
		this.clinicName = clinicName;
		this.missedTherapyCount = missedTherapyCount;
		this.isSettingsDeviated = isSettingsDeviated;
		this.isHMRCompliant = isHMRCompliant;
		this.isHcpAcceptHMRNotification = isHcpAcceptHMRNotification;
		this.isHcpAcceptSettingsNotification = isHcpAcceptSettingsNotification;
		this.isHcpAcceptTherapyNotification = isHcpAcceptTherapyNotification;
		this.email = email;
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

	public BigInteger getHcpId() {
		return hcpId;
	}

	public void setHcpId(BigInteger hcpId) {
		this.hcpId = hcpId;
	}

	public BigInteger getHcpUserId() {
		return hcpUserId;
	}

	public void setHcpUserId(BigInteger hcpUserId) {
		this.hcpUserId = hcpUserId;
	}

	public String getHcpFirstname() {
		return hcpFirstname;
	}

	public void setHcpFirstname(String hcpFirstname) {
		this.hcpFirstname = hcpFirstname;
	}

	public String getHcpLastname() {
		return hcpLastname;
	}

	public void setHcpLastname(String hcpLastname) {
		this.hcpLastname = hcpLastname;
	}
	
	public String getClinicId() {
		return clinicId;
	}

	public void setClinicId(String clinicId) {
		this.clinicId = clinicId;
	}

	public String getClinicName() {
		return clinicName;
	}

	public void setClinicName(String clinicName) {
		this.clinicName = clinicName;
	}

	public BigInteger getClinicAdminId() {
		return clinicAdminId;
	}

	public void setClinicAdminId(BigInteger clinicAdminId) {
		this.clinicAdminId = clinicAdminId;
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

	public void setHcpAcceptSettingsNotification(
			boolean isHcpAcceptSettingsNotification) {
		this.isHcpAcceptSettingsNotification = isHcpAcceptSettingsNotification;
	}

	public boolean isHcpAcceptTherapyNotification() {
		return isHcpAcceptTherapyNotification;
	}

	public void setHcpAcceptTherapyNotification(
			boolean isHcpAcceptTherapyNotification) {
		this.isHcpAcceptTherapyNotification = isHcpAcceptTherapyNotification;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "ClinicStatsNotificationVO [patientUserid=" + patientUserid
				+ ", patientFirstname=" + patientFirstname
				+ ", patientLastname=" + patientLastname + ", hcpId=" + hcpId
				+ ", hcpUserId=" + hcpUserId + ", hcpFirstname=" + hcpFirstname
				+ ", hcpLastname=" + hcpLastname + ", clinicAdminId="
				+ clinicAdminId + ", clinicName=" + clinicName
				+ ", missedTherapyCount=" + missedTherapyCount
				+ ", isSettingsDeviated=" + isSettingsDeviated
				+ ", isHMRCompliant=" + isHMRCompliant
				+ ", isHcpAcceptHMRNotification=" + isHcpAcceptHMRNotification
				+ ", isHcpAcceptSettingsNotification="
				+ isHcpAcceptSettingsNotification
				+ ", isHcpAcceptTherapyNotification="
				+ isHcpAcceptTherapyNotification + "]";
	}
	
}
