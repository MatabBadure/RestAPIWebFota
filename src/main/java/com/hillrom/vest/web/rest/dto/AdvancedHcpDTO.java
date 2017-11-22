package com.hillrom.vest.web.rest.dto;

public class AdvancedHcpDTO {

	private String name;
	
	private String specialty;
	
	private String credentials;
	
	private String country;
	
	private String city;
	
	private String state;
	
	private Integer zipcode;
	
	private String status;

	public AdvancedHcpDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AdvancedHcpDTO(String name, String specialty, String credentials,
			String country, String city, String state, Integer zipcode,
			String status) {
		super();
		this.name = name;
		this.specialty = specialty;
		this.credentials = credentials;
		this.country = country;
		this.city = city;
		this.state = state;
		this.zipcode = zipcode;
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
