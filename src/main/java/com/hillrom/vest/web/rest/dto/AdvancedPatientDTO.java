package com.hillrom.vest.web.rest.dto;

import java.util.Date;
import java.util.List;

public class AdvancedPatientDTO {
	
		private String name;

	    private String hillromId;

	    private String email;

	    private String gender;

	    private List<String> age;

	    private String country;

	    private List<String> state;
	    
	    private List<String> city;

	    private String zipcode;

	    private String clinicLevelStatus;

	    private String diagnosis;

	    private List<String> adherenceScoreRange;

	    private String deviceType;

	    private String deviceStatus;
		
	    private String deviceActiveDateFrom;

	    private String deviceActiveDateTo;

	    private String serialNo;

	    private String minHMRRange;

	    private String maxHMRRange;
		
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
				String gender, List<String> age, String country, List<String> state,
				List<String> city, String zipcode, String clinicLevelStatus,
				String diagnosis, List<String> adherenceScoreRange,
				String deviceType, String deviceStatus,
				String deviceActiveDateFrom, String deviceActiveDateTo,
				String serialNo, String minHMRRange, String maxHMRRange,
				String adherenceReset, String noTransmissionRecorded,
				String belowFrequencySetting, String belowTherapyMin,
				String missedTherapyDays, Boolean parent, Boolean deleted) {
			super();
			this.name = name;
			this.hillromId = hillromId;
			this.email = email;
			this.gender = gender;
			this.age = age;
			this.country = country;
			this.state = state;
			this.city = city;
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
			this.parent = parent;
			this.deleted = deleted;
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

		public List<String> getAge() {
			return age;
		}

		public void setAge(List<String> age) {
			this.age = age;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public List<String> getState() {
			return state;
		}

		public void setState(List<String> state) {
			this.state = state;
		}

		public List<String> getCity() {
			return city;
		}

		public void setCity(List<String> city) {
			this.city = city;
		}

		public String getZipcode() {
			return zipcode;
		}

		public void setZipcode(String zipcode) {
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

		public List<String> getAdherenceScoreRange() {
			return adherenceScoreRange;
		}

		public void setAdherenceScoreRange(List<String> adherenceScoreRange) {
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

		public String getDeviceActiveDateFrom() {
			return deviceActiveDateFrom;
		}

		public void setDeviceActiveDateFrom(String deviceActiveDateFrom) {
			this.deviceActiveDateFrom = deviceActiveDateFrom;
		}

		public String getDeviceActiveDateTo() {
			return deviceActiveDateTo;
		}

		public void setDeviceActiveDateTo(String deviceActiveDateTo) {
			this.deviceActiveDateTo = deviceActiveDateTo;
		}

		public String getSerialNo() {
			return serialNo;
		}

		public void setSerialNo(String serialNo) {
			this.serialNo = serialNo;
		}

		public String getMinHMRRange() {
			return minHMRRange;
		}

		public void setMinHMRRange(String minHMRRange) {
			this.minHMRRange = minHMRRange;
		}

		public String getMaxHMRRange() {
			return maxHMRRange;
		}

		public void setMaxHMRRange(String maxHMRRange) {
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

		public Boolean getParent() {
			return parent;
		}

		public void setParent(Boolean parent) {
			this.parent = parent;
		}

		public Boolean getDeleted() {
			return deleted;
		}

		public void setDeleted(Boolean deleted) {
			this.deleted = deleted;
		}

		
}
