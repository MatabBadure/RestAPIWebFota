package com.hillrom.vest.web.rest.dto;

import java.util.Date;

public class AdvancedPatientDTO {
	
		private String name;

	    private String hillromId;

	    private String email;

	    private String gender;

	    private Integer age;

	    private String country;

	    private String city;

	    private String state;

	    private Integer zipcode;

	    private String clinicLevelStatus;

	    private String diagnosis;

	    private Integer adherenceScoreRange;

	    private String deviceType;

	    private String deviceStatus;
		
	    private Date deviceActiveDateFrom;

	    private Date deviceActiveDateTo;

	    private Integer serialNo;

	    private Integer minHMRRange;

	    private Integer maxHMRRange;
		
	    private String adherenceReset;

	    private String noTransmissionRecorded;

	    private String belowFrequencySetting;

	    private String belowTherapyMin;

	    private String missedTherapyDays;
		
		private Boolean parent;
		
		private Boolean deleted;

		public AdvancedPatientDTO() {
			super();
		}

		public AdvancedPatientDTO(String name, String hillromId, String email,
				String gender, Integer age, String country, String city,
				String state, Integer zipcode, String clinicLevelStatus,
				String diagnosis, Integer adherenceScoreRange,
				String deviceType, String deviceStatus,
				Date deviceActiveDateFrom, Date deviceActiveDateTo,
				Integer serialNo, Integer minHMRRange, Integer maxHMRRange,
				String adherenceReset, String noTransmissionRecorded,
				String belowFrequencySetting, String belowTherapyMin,
				String missedTherapyDays) {
			super();
			this.name = name;
			this.hillromId = hillromId;
			this.email = email;
			this.gender = gender;
			this.age = age;
			this.country = country;
			this.city = city;
			this.state = state;
			this.zipcode = zipcode;
			this.clinicLevelStatus = clinicLevelStatus;
			this.diagnosis = diagnosis;
			this.adherenceScoreRange = adherenceScoreRange;
			this.deviceType = deviceType;
			this.deviceStatus = deviceStatus;
			this.deviceActiveDateFrom = deviceActiveDateFrom;
			this.deviceActiveDateTo = deviceActiveDateTo;
			this.serialNo = serialNo;
			this.minHMRRange = minHMRRange;
			this.maxHMRRange = maxHMRRange;
			this.adherenceReset = adherenceReset;
			this.noTransmissionRecorded = noTransmissionRecorded;
			this.belowFrequencySetting = belowFrequencySetting;
			this.belowTherapyMin = belowTherapyMin;
			this.missedTherapyDays = missedTherapyDays;
		}
	    
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getHillromId() {
			return hillromId;
		}

		public void setHillromId(String hillromId) {
			this.hillromId = hillromId;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public Integer getZipcode() {
			return zipcode;
		}

		public void setZipcode(Integer zipcode) {
			this.zipcode = zipcode;
		}

		public String getClinicLevelStatus() {
			return clinicLevelStatus;
		}

		public void setClinicLevelStatus(String clinicLevelStatus) {
			this.clinicLevelStatus = clinicLevelStatus;
		}

		public String getDiagnosis() {
			return diagnosis;
		}

		public void setDiagnosis(String diagnosis) {
			this.diagnosis = diagnosis;
		}

		public Integer getAdherenceScoreRange() {
			return adherenceScoreRange;
		}

		public void setAdherenceScoreRange(Integer adherenceScoreRange) {
			this.adherenceScoreRange = adherenceScoreRange;
		}

		public String getDeviceType() {
			return deviceType;
		}

		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}

		public String getDeviceStatus() {
			return deviceStatus;
		}

		public void setDeviceStatus(String deviceStatus) {
			this.deviceStatus = deviceStatus;
		}

		public Date getDeviceActiveDateFrom() {
			return deviceActiveDateFrom;
		}

		public void setDeviceActiveDateFrom(Date deviceActiveDateFrom) {
			this.deviceActiveDateFrom = deviceActiveDateFrom;
		}

		public Date getDeviceActiveDateTo() {
			return deviceActiveDateTo;
		}

		public void setDeviceActiveDateTo(Date deviceActiveDateTo) {
			this.deviceActiveDateTo = deviceActiveDateTo;
		}

		public Integer getSerialNo() {
			return serialNo;
		}

		public void setSerialNo(Integer serialNo) {
			this.serialNo = serialNo;
		}

		public Integer getMinHMRRange() {
			return minHMRRange;
		}

		public void setMinHMRRange(Integer minHMRRange) {
			this.minHMRRange = minHMRRange;
		}

		public Integer getMaxHMRRange() {
			return maxHMRRange;
		}

		public void setMaxHMRRange(Integer maxHMRRange) {
			this.maxHMRRange = maxHMRRange;
		}

		public String getAdherenceReset() {
			return adherenceReset;
		}

		public void setAdherenceReset(String adherenceReset) {
			this.adherenceReset = adherenceReset;
		}

		public String getNoTransmissionRecorded() {
			return noTransmissionRecorded;
		}

		public void setNoTransmissionRecorded(String noTransmissionRecorded) {
			this.noTransmissionRecorded = noTransmissionRecorded;
		}

		public String getBelowFrequencySetting() {
			return belowFrequencySetting;
		}

		public void setBelowFrequencySetting(String belowFrequencySetting) {
			this.belowFrequencySetting = belowFrequencySetting;
		}

		public String getBelowTherapyMin() {
			return belowTherapyMin;
		}

		public void setBelowTherapyMin(String belowTherapyMin) {
			this.belowTherapyMin = belowTherapyMin;
		}

		public String getMissedTherapyDays() {
			return missedTherapyDays;
		}

		public void setMissedTherapyDays(String missedTherapyDays) {
			this.missedTherapyDays = missedTherapyDays;
		}

		
}
