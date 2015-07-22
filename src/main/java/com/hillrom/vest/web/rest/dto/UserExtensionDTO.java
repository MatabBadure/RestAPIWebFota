package com.hillrom.vest.web.rest.dto;

import javax.persistence.Column;
import javax.validation.constraints.Size;

import org.joda.time.LocalDate;

public class UserExtensionDTO extends UserDTO {

    @Size(max = 50)
    private String speciality;

    @Size(max = 50)
    private String credentials;

    @Size(min = 5, max = 100)
    private Long primaryPhone;

    @Size(min = 2, max = 100)
    private Long mobilePhone;

    @Size(min = 2, max = 100)
    private Long faxNumber;
    
    @Size(min = 2, max = 100)
    private String address;

    @Size(min = 2, max = 100)
    private String city;

    @Size(min = 2, max = 100)
    private String state;
    
    @Size(min = 2, max = 100)
    private String npiNumber;
    
    @Size(min = 2, max = 100)
    private String role;
    
    @Size(min = 2, max = 100)
    private String clinicName;
    
    @Size(min = 2, max = 100)
    private String hillromId;
    
    @Column(name = "dob")
    private LocalDate dob;

    public UserExtensionDTO() {
    }

	public UserExtensionDTO(String speciality, String credentials,
			Long primaryPhone, Long mobilePhone, Long faxNumber,
			String address, String city, String state, String npiNumber,
			String role, String clinicName) {
		super();
		this.speciality = speciality;
		this.credentials = credentials;
		this.primaryPhone = primaryPhone;
		this.mobilePhone = mobilePhone;
		this.faxNumber = faxNumber;
		this.address = address;
		this.city = city;
		this.state = state;
		this.npiNumber = npiNumber;
		this.role = role;
		this.clinicName = clinicName;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public Long getPrimaryPhone() {
		return primaryPhone;
	}

	public void setPrimaryPhone(Long primaryPhone) {
		this.primaryPhone = primaryPhone;
	}

	public Long getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(Long mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Long getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(Long faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getNpiNumber() {
		return npiNumber;
	}

	public void setNpiNumber(String npiNumber) {
		this.npiNumber = npiNumber;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getClinicName() {
		return clinicName;
	}

	public void setClinicName(String clinicName) {
		this.clinicName = clinicName;
	}

	public String getHillromId() {
		return hillromId;
	}

	public void setHillromId(String hillromId) {
		this.hillromId = hillromId;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	@Override
	public String toString() {
		return "UserExtensionDTO [speciality=" + speciality + ", credentials="
				+ credentials + ", primaryPhone=" + primaryPhone
				+ ", mobilePhone=" + mobilePhone + ", faxNumber=" + faxNumber
				+ ", address=" + address + ", city=" + city + ", state="
				+ state + ", npiNumber=" + npiNumber + ", role=" + role
				+ ", clinicName=" + clinicName + "]";
	}

}
