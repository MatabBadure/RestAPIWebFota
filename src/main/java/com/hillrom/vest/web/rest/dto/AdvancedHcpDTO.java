package com.hillrom.vest.web.rest.dto;


import java.util.List;


public class AdvancedHcpDTO {

	private String name;
	
	private String specialty;
	
	private String credentials;
	
	private String country;
	

	private List<String> city;
	
	private List<String> state;
	
	private String zipcode;

	
	private String status;

	public AdvancedHcpDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AdvancedHcpDTO(String name, String specialty, String credentials,

			String country, List<String> city, List<String> state, String zipcode,

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
