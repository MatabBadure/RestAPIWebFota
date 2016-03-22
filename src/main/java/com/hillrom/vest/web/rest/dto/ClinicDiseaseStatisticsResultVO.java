package com.hillrom.vest.web.rest.dto;

import java.math.BigInteger;
import java.util.Objects;

public class ClinicDiseaseStatisticsResultVO {

	private int totalPatients;
	private String ageGroupLabel;
	private String clinicSizeLabel;
	private String state;
	private String city;
	
	public ClinicDiseaseStatisticsResultVO(BigInteger totalPatientCount,String ageGroupLabel,
			String clinicSizeLabel,String state, String city) {
		super();
		this.totalPatients = Objects.nonNull(totalPatientCount) ? totalPatientCount.intValue() : 0;
		this.ageGroupLabel = ageGroupLabel;
		this.clinicSizeLabel = clinicSizeLabel;
		this.state = state;
		this.city = city;
	}
	
	public ClinicDiseaseStatisticsResultVO(BigInteger totalPatientCount,String state, String city) {
		super();
		this.totalPatients = Objects.nonNull(totalPatientCount) ? totalPatientCount.intValue() : 0;
		this.state = state;
		this.city = city;
	}

	public int getTotalPatients() {
		return totalPatients;
	}

	public void setTotalPatients(int totalPatients) {
		this.totalPatients = totalPatients;
	}

	public String getAgeGroupLabel() {
		return ageGroupLabel;
	}

	public void setAgeGroupLabel(String ageGroupLabel) {
		this.ageGroupLabel = ageGroupLabel;
	}

	public String getClinicSizeLabel() {
		return clinicSizeLabel;
	}

	public void setClinicSizeLabel(String clinicSizeLabel) {
		this.clinicSizeLabel = clinicSizeLabel;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
