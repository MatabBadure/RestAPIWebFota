package com.hillrom.vest.web.rest.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Size;

public class UserExtensionDTO extends UserDTO {

    @Size(max = 50)
    private String speciality;

    @Size(max = 50)
    private String credentials;

    @Size(min = 5, max = 100)
    private String primaryPhone;

    @Size(min = 2, max = 100)
    private String mobilePhone;

    @Size(min = 2, max = 100)
    private String faxNumber;
    
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
    private List<Map<String, String>> clinicList = new ArrayList<>();
    
    @Size(min = 2, max = 100)
    private String hillromId;
    
    private String dob;
    
    private String relationship;
    
    private Map<String,String> clinicMRNId = new HashMap<>();
    
    private boolean expired;

    public UserExtensionDTO() {
    }

	public UserExtensionDTO(String speciality, String credentials,
			String primaryPhone, String mobilePhone, String faxNumber,
			String address, String city, String state, String npiNumber,
			String role, List<Map<String, String>> clinicList) {
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
		this.clinicList = clinicList;
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

	public String getPrimaryPhone() {
		return primaryPhone;
	}

	public void setPrimaryPhone(String primaryPhone) {
		this.primaryPhone = primaryPhone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
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

	public List<Map<String, String>> getClinicList() {
		return clinicList;
	}

	public void setClinicList(List<Map<String, String>> clinicList) {
		this.clinicList = clinicList;
	}

	public String getHillromId() {
		return hillromId;
	}

	public void setHillromId(String hillromId) {
		this.hillromId = hillromId;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public Map<String, String> getClinicMRNId() {
		return clinicMRNId;
	}

	public void setClinicMRNId(Map<String, String> clinicMRNId) {
		this.clinicMRNId = clinicMRNId;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	@Override
	public String toString() {
		return "UserExtensionDTO [speciality=" + speciality + ", credentials="
				+ credentials + ", primaryPhone=" + primaryPhone
				+ ", mobilePhone=" + mobilePhone + ", faxNumber=" + faxNumber
				+ ", address=" + address + ", city=" + city + ", state="
				+ state + ", npiNumber=" + npiNumber + ", role=" + role +"]";
	}

}
