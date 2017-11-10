package com.hillrom.vest.web.rest.dto;

public class AdvancedClinicDTO {

	private String name;
	
	private String clinicType;
	
	private String specialty;
	
	private String country;
	
	private String city;
	
	private String state;
	
	private String zipcode;
	
	private String adherenceWindowSelected;
	
	private String status;

	public AdvancedClinicDTO() {
		super();
	}

	public AdvancedClinicDTO(String name, String clinicType, String specialty,
			String country, String city, String state, String zipcode,
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

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the clinicType
	 */
	public String getClinicType() {
		return clinicType;
	}

	/**
	 * @param clinicType the clinicType to set
	 */
	public void setClinicType(String clinicType) {
		this.clinicType = clinicType;
	}

	/**
	 * @return the specialty
	 */
	public String getSpecialty() {
		return specialty;
	}

	/**
	 * @param specialty the specialty to set
	 */
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the zipcode
	 */
	public String getZipcode() {
		return zipcode;
	}

	/**
	 * @param zipcode the zipcode to set
	 */
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * @return the adherenceWindowSelected
	 */
	public String getAdherenceWindowSelected() {
		return adherenceWindowSelected;
	}

	/**
	 * @param adherenceWindowSelected the adherenceWindowSelected to set
	 */
	public void setAdherenceWindowSelected(String adherenceWindowSelected) {
		this.adherenceWindowSelected = adherenceWindowSelected;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	
}
