package com.hillrom.vest.web.rest.dto;

import java.util.List;

public class AdvancedClinicDTO {

	private String clinicName;
	
	private String clinicType;
	
	private String clinicSpecialty;
	
	private List<String> country;
	
	private List<String> city;
	
	private List<String> state;
	
	private String zipcode;
	
	private String adherenceWindowSelected;
	
	private String clinicStatus;

	public AdvancedClinicDTO() {
		super();
	}

	public AdvancedClinicDTO(String name, String clinicType, String specialty,
			List<String> country, List<String> city, List<String> state, String zipcode,
			String adherenceWindowSelected, String status) {
		super();
		this.clinicName = name;
		this.clinicType = clinicType;
		this.clinicSpecialty = specialty;
		this.country = country;
		this.city = city;
		this.state = state;
		this.zipcode = zipcode;
		this.adherenceWindowSelected = adherenceWindowSelected;
		this.clinicStatus = status;
	}

	

	public String getClinicType() {
		return clinicType;
	}

	public void setClinicType(String clinicType) {
		this.clinicType = clinicType;
	}

	

	public List<String> getCountry() {
		return country;
	}

	public void setCountry(List<String> country) {
		this.country = country;
	}

	public List<String> getCity() {
		return city;
	}

	public void setCity(List<String> city) {
		this.city = city;
	}

	public List<String> getState() {
		return state;
	}

	public void setState(List<String> state) {
		this.state = state;
	}
	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getAdherenceWindowSelected() {
		return adherenceWindowSelected;
	}

	public void setAdherenceWindowSelected(String adherenceWindowSelected) {
		this.adherenceWindowSelected = adherenceWindowSelected;
	}

	public String getClinicName() {
		return clinicName;
	}

	public void setClinicName(String clinicName) {
		this.clinicName = clinicName;
	}

	public String getClinicSpecialty() {
		return clinicSpecialty;
	}

	public void setClinicSpecialty(String clinicSpecialty) {
		this.clinicSpecialty = clinicSpecialty;
	}

	public String getClinicStatus() {
		return clinicStatus;
	}

	public void setClinicStatus(String clinicStatus) {
		this.clinicStatus = clinicStatus;
	}

	
}
