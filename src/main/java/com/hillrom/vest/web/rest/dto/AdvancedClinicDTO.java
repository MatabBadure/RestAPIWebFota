package com.hillrom.vest.web.rest.dto;

import java.util.List;

public class AdvancedClinicDTO {

	private String name;
	
	private String clinicType;
	
	private String specialty;
	
	private String country;
	
	private List<String> city;
	
	private List<String> state;
	
	private String zipcode;
	
	private String adherenceWindowSelected;
	
	private String status;

	public AdvancedClinicDTO() {
		super();
	}

	public AdvancedClinicDTO(String name, String clinicType, String specialty,
			String country, List<String> city, List<String> state, String zipcode,
			String adherenceWindowSelected, String status) {
		super();
		this.name = name;
		this.clinicType = clinicType;
		this.specialty = specialty;
		this.country = country;
		this.city = city;
		this.state = state;
		this.zipcode = zipcode;
		this.adherenceWindowSelected = adherenceWindowSelected;
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClinicType() {
		return clinicType;
	}

	public void setClinicType(String clinicType) {
		this.clinicType = clinicType;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
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

	public void setZip(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getAdherenceWindowSelected() {
		return adherenceWindowSelected;
	}

	public void setAdherenceWindowSelected(String adherenceWindowSelected) {
		this.adherenceWindowSelected = adherenceWindowSelected;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
