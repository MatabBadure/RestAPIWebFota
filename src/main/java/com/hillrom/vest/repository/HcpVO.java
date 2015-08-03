package com.hillrom.vest.repository;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

public class HcpVO {

	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private Integer zipcode;
	private String address;
	private String city;
	private String credentials;
	private String faxNumber;
	private String primaryPhone;
	private String mobilePhone;
	private String speciality;
	private String state;
	private List<Map<String,String>> clinics = new LinkedList<>();
	private boolean isDeleted;
	private DateTime createdAt;
	private boolean isActivated;
	
	public HcpVO(Long id, String firstName, String lastName, String email,boolean isDeleted,
			Integer zipcode, String address, String city, String credentials,
			String faxNumber, String primaryPhone, String mobilePhone,
			String speciality, String state,DateTime createdAt,boolean isActivated) {
		super();
		this.id = id;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isDeleted = isDeleted;
		this.zipcode = zipcode;
		this.address = address;
		this.city = city;
		this.credentials = credentials;
		this.faxNumber = faxNumber;
		this.primaryPhone = primaryPhone;
		this.mobilePhone = mobilePhone;
		this.speciality = speciality;
		this.state = state;
		this.createdAt = createdAt;
		this.isActivated = isActivated;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Integer getZipcode() {
		return zipcode;
	}
	public void setZipcode(Integer zipcode) {
		this.zipcode = zipcode;
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
	public String getCredentials() {
		return credentials;
	}
	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}
	
	public String getFaxNumber() {
		return faxNumber;
	}
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
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
	public String getSpeciality() {
		return speciality;
	}
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public List<Map<String, String>> getClinics() {
		return clinics;
	}
	public void setClinics(List<Map<String, String>> clinicNames) {
		this.clinics = clinicNames;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public DateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}
	public boolean isActivated() {
		return isActivated;
	}
	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}
	
	
}
