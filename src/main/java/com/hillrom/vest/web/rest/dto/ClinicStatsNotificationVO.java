package com.hillrom.vest.web.rest.dto;

import java.math.BigInteger;
import java.util.Objects;

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
	private String caFirstname;
	private String caLastname;
	private int missedTherapyCount;
	private boolean isSettingsDeviated;
	private boolean isHMRCompliant;
	private boolean isHcpAcceptHMRNotification;
	private boolean isHcpAcceptSettingsNotification;
	private boolean isHcpAcceptTherapyNotification;
	private String hcpEmail;
	private String caEmail;
	private boolean isCAAcceptHMRNotification;
	private boolean isCAAcceptSettingsNotification;
	private boolean isCAAcceptTherapyNotification;
	private Integer adherenceSetting;

	public ClinicStatsNotificationVO(BigInteger patientUserid,
			String patientFirstname,String patientLastname,
			BigInteger hcpId,BigInteger hcpUserId, String hname,
			String clinicId,String clinicName,BigInteger clinicAdminId ,
			int missedTherapyCount, boolean isSettingsDeviated,
			boolean isHMRCompliant, boolean isHcpAcceptHMRNotification,
			boolean isHcpAcceptSettingsNotification,
			boolean isHcpAcceptTherapyNotification,
			String hcpEmail,
			String caName,
			Integer isCAAcceptHMRNotification,
			Integer isCAAcceptSettingsNotification,
			Integer isCAAcceptTherapyNotification,
			String caEmail,
			Integer adherenceSetting
			) {
		super();
		this.patientUserid = patientUserid;
		this.patientFirstname = patientFirstname;
		this.patientLastname = patientLastname;
		this.hcpId = hcpId;
		this.hcpUserId = hcpUserId;
		this.hcpFirstname = hname.split(" ")[1];
		this.hcpLastname = hname.split(" ")[0];
		this.clinicId = clinicId;
		this.clinicAdminId = clinicAdminId;
		this.clinicName = clinicName;
		this.missedTherapyCount = missedTherapyCount;
		this.isSettingsDeviated = isSettingsDeviated;
		this.isHMRCompliant = isHMRCompliant;
		this.isHcpAcceptHMRNotification = isHcpAcceptHMRNotification;
		this.isHcpAcceptSettingsNotification = isHcpAcceptSettingsNotification;
		this.isHcpAcceptTherapyNotification = isHcpAcceptTherapyNotification;
		this.hcpEmail = hcpEmail;
		this.caFirstname = Objects.nonNull(caName)? caName.split(" ")[1]:"";
		this.caLastname = Objects.nonNull(caName)?caName.split(" ")[0]:"";
		this.isCAAcceptHMRNotification = convertIntegerToBoolean(isCAAcceptHMRNotification);
		this.isCAAcceptSettingsNotification = convertIntegerToBoolean(isCAAcceptSettingsNotification);
		this.isCAAcceptTherapyNotification = convertIntegerToBoolean(isCAAcceptTherapyNotification);
		this.caEmail = caEmail;
		this.adherenceSetting = adherenceSetting;
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


	public String getCaFirstname() {
		return caFirstname;
	}


	public void setCaFirstname(String caFirstname) {
		this.caFirstname = caFirstname;
	}


	public String getCaLastname() {
		return caLastname;
	}


	public void setCaLastname(String caLastname) {
		this.caLastname = caLastname;
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


	public String getHcpEmail() {
		return hcpEmail;
	}


	public void setHcpEmail(String hcpEmail) {
		this.hcpEmail = hcpEmail;
	}


	public String getCaEmail() {
		return caEmail;
	}


	public void setCaEmail(String caEmail) {
		this.caEmail = caEmail;
	}


	public boolean isCAAcceptHMRNotification() {
		return isCAAcceptHMRNotification;
	}


	public void setCAAcceptHMRNotification(boolean isCAAcceptHMRNotification) {
		this.isCAAcceptHMRNotification = isCAAcceptHMRNotification;
	}


	public boolean isCAAcceptSettingsNotification() {
		return isCAAcceptSettingsNotification;
	}


	public void setCAAcceptSettingsNotification(
			boolean isCAAcceptSettingsNotification) {
		this.isCAAcceptSettingsNotification = isCAAcceptSettingsNotification;
	}


	public boolean isCAAcceptTherapyNotification() {
		return isCAAcceptTherapyNotification;
	}


	public void setCAAcceptTherapyNotification(boolean isCAAcceptTherapyNotification) {
		this.isCAAcceptTherapyNotification = isCAAcceptTherapyNotification;
	}
	
	public Integer getAdherenceSetting() {
		return adherenceSetting;
	}


	public void setAdherenceSetting(Integer adherenceSetting) {
		this.adherenceSetting = adherenceSetting;
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
				+ isHcpAcceptTherapyNotification
				+ ", adherenceSetting="
				+ adherenceSetting+ "]";
	}

	public boolean convertIntegerToBoolean(Integer i){
		if(Objects.nonNull(i))
			return i == 0 ? false : true;
		return false;
	}
}
