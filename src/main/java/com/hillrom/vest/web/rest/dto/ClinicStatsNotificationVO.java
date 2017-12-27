package com.hillrom.vest.web.rest.dto;

import java.math.BigInteger;
import java.util.Objects;

public class ClinicStatsNotificationVO {

	private BigInteger patientUserid;
	private String patientFirstname;
	private String patientLastname;
	private BigInteger hcpIdOrclinicAdminId;
	private String hcpFirstnameOrCaFirstname;
	private String hcpLastnameOrCaLastname;
	private String clinicId;
	private String clinicName;
	private BigInteger clinicAdminId;
	private String caFirstname;
	private String caLastname;
	private int missedTherapyCount;
	private boolean isSettingsDeviated;
	private boolean isHMRCompliant;
	private boolean isHcpOrIsCAAcceptHMRNotification;
	private boolean isHcpOrIsCAAcceptSettingsNotification;
	private boolean isHcpOrIsCAAcceptTherapyNotification;
	private String missedTherapyNotificationFreq;
	private String nonHmrNotificationFreq;
	private String settingDeviationNotificationFreq;
	private String hcpEmailOrCaEmail;
	private Integer adherenceSetting;

	public ClinicStatsNotificationVO(BigInteger patientUserid,
			String patientFirstname, String patientLastname,
			BigInteger hcpIdOrclinicAdminId, String hname,
			String clinicId,
			String clinicName, Integer missedTherapyCount,
			boolean isSettingsDeviated, boolean isHMRCompliant,
			boolean isHcpOrIsCAAcceptHMRNotification,
			boolean isHcpOrIsCAAcceptSettingsNotification,
			boolean isHcpOrIsCAAcceptTherapyNotification,
			String missedTherapyNotificationFreq,
			String nonHmrNotificationFreq,
			String settingDeviationNotificationFreq,String hcpEmailOrCaEmail,
			Integer adherenceSetting) {
		super();
		this.patientUserid = patientUserid;
		this.patientFirstname = patientFirstname;
		this.patientLastname = patientLastname;
		this.hcpIdOrclinicAdminId = hcpIdOrclinicAdminId;
		this.hcpFirstnameOrCaFirstname = hname.split(" ")[1];
		this.hcpLastnameOrCaLastname = hname.split(" ")[0];
		this.clinicId = clinicId;
		this.clinicName = clinicName;
		this.missedTherapyCount = missedTherapyCount;
		this.isSettingsDeviated = isSettingsDeviated;
		this.isHMRCompliant = isHMRCompliant;
		this.isHcpOrIsCAAcceptHMRNotification = isHcpOrIsCAAcceptHMRNotification;
		this.isHcpOrIsCAAcceptSettingsNotification = isHcpOrIsCAAcceptSettingsNotification;
		this.isHcpOrIsCAAcceptTherapyNotification = isHcpOrIsCAAcceptTherapyNotification;
		this.missedTherapyNotificationFreq= missedTherapyNotificationFreq;
		this.nonHmrNotificationFreq= nonHmrNotificationFreq;
		this.settingDeviationNotificationFreq= settingDeviationNotificationFreq;
		this.hcpEmailOrCaEmail=hcpEmailOrCaEmail;
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


	public BigInteger getHcpIdOrclinicAdminId() {
		return hcpIdOrclinicAdminId;
	}

	public void setHcpIdOrclinicAdminId(BigInteger hcpIdOrclinicAdminId) {
		this.hcpIdOrclinicAdminId = hcpIdOrclinicAdminId;
	}

	public String getHcpFirstnameOrCaFirstname() {
		return hcpFirstnameOrCaFirstname;
	}

	public void setHcpFirstnameOrCaFirstname(String hcpFirstnameOrCaFirstname) {
		this.hcpFirstnameOrCaFirstname = hcpFirstnameOrCaFirstname;
	}

	public String getHcpLastnameOrCaLastname() {
		return hcpLastnameOrCaLastname;
	}

	public void setHcpLastnameOrCaLastname(String hcpLastnameOrCaLastname) {
		this.hcpLastnameOrCaLastname = hcpLastnameOrCaLastname;
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


	public boolean isHcpOrIsCAAcceptHMRNotification() {
		return isHcpOrIsCAAcceptHMRNotification;
	}

	public void setHcpOrIsCAAcceptHMRNotification(
			boolean isHcpOrIsCAAcceptHMRNotification) {
		this.isHcpOrIsCAAcceptHMRNotification = isHcpOrIsCAAcceptHMRNotification;
	}

	public boolean isHcpOrIsCAAcceptSettingsNotification() {
		return isHcpOrIsCAAcceptSettingsNotification;
	}

	public void setHcpOrIsCAAcceptSettingsNotification(
			boolean isHcpOrIsCAAcceptSettingsNotification) {
		this.isHcpOrIsCAAcceptSettingsNotification = isHcpOrIsCAAcceptSettingsNotification;
	}

	public boolean isHcpOrIsCAAcceptTherapyNotification() {
		return isHcpOrIsCAAcceptTherapyNotification;
	}

	public void setHcpOrIsCAAcceptTherapyNotification(
			boolean isHcpOrIsCAAcceptTherapyNotification) {
		this.isHcpOrIsCAAcceptTherapyNotification = isHcpOrIsCAAcceptTherapyNotification;
	}

	public String getHcpEmailOrCaEmail() {
		return hcpEmailOrCaEmail;
	}

	public void setHcpEmailOrCaEmail(String hcpEmailOrCaEmail) {
		this.hcpEmailOrCaEmail = hcpEmailOrCaEmail;
	}

	public String getMissedTherapyNotificationFreq() {
		return missedTherapyNotificationFreq;
	}

	public void setMissedTherapyNotificationFreq(
			String missedTherapyNotificationFreq) {
		this.missedTherapyNotificationFreq = missedTherapyNotificationFreq;
	}

	public String getNonHmrNotificationFreq() {
		return nonHmrNotificationFreq;
	}

	public void setNonHmrNotificationFreq(String nonHmrNotificationFreq) {
		this.nonHmrNotificationFreq = nonHmrNotificationFreq;
	}

	public String getSettingDeviationNotificationFreq() {
		return settingDeviationNotificationFreq;
	}

	public void setSettingDeviationNotificationFreq(
			String settingDeviationNotificationFreq) {
		this.settingDeviationNotificationFreq = settingDeviationNotificationFreq;
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
				+ ", patientLastname=" + patientLastname
				+ ", hcpIdOrclinicAdminId=" + hcpIdOrclinicAdminId
				+ ", hcpFirstnameOrCaFirstname=" + hcpFirstnameOrCaFirstname
				+ ", hcpLastnameOrCaLastname=" + hcpLastnameOrCaLastname
				+ ", clinicId=" + clinicId + ", clinicName=" + clinicName
				+ ", clinicAdminId=" + clinicAdminId + ", caFirstname="
				+ caFirstname + ", caLastname=" + caLastname
				+ ", missedTherapyCount=" + missedTherapyCount
				+ ", isSettingsDeviated=" + isSettingsDeviated
				+ ", isHMRCompliant=" + isHMRCompliant
				+ ", isHcpOrIsCAAcceptHMRNotification="
				+ isHcpOrIsCAAcceptHMRNotification
				+ ", isHcpOrIsCAAcceptSettingsNotification="
				+ isHcpOrIsCAAcceptSettingsNotification
				+ ", isHcpOrIsCAAcceptTherapyNotification="
				+ isHcpOrIsCAAcceptTherapyNotification
				+ ", missedTherapyNotificationFreq="
				+ missedTherapyNotificationFreq + ", nonHmrNotificationFreq="
				+ nonHmrNotificationFreq
				+ ", settingDeviationNotificationFreq="
				+ settingDeviationNotificationFreq + ", hcpEmailOrCaEmail="
				+ hcpEmailOrCaEmail + ", adherenceSetting=" + adherenceSetting
				+ "]";
	}

	public boolean convertIntegerToBoolean(Integer i){
		if(Objects.nonNull(i))
			return i == 0 ? false : true;
		return false;
	}
}
