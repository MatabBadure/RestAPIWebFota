package com.hillrom.vest.web.rest.dto;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.hillrom.vest.domain.Clinic;

public class ClinicDTO {
	
	@Size(max = 50)
	private String parentClinicName;

	@Size(max = 50)
    private String address;

	@Size(max = 50)
    private Integer zipcode;

	@Size(max = 50)
    private String city;

	@Size(max = 50)
    private String state;

	@Size(max = 50)
    private Long phoneNumber;

	@Size(max = 50)
    private Long faxNumber;

	@Size(max = 50)
    private String clinicAdminId;

	@Size(max = 50)
    private ArrayList<String> childClinicList;

	@Size(max = 50)
    private String npiNumber;

	public ClinicDTO() {
		super();
	}

	public ClinicDTO(String parentClinicName, String address, Integer zipcode, String city,
			String state, Long phoneNumber, Long faxNumber, String clinicAdminId,
			ArrayList<String> childClinicList, String npiNumber) {
		super();
		this.parentClinicName = parentClinicName;
		this.address = address;
		this.zipcode = zipcode;
		this.city = city;
		this.state = state;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.clinicAdminId = clinicAdminId;
		this.childClinicList = childClinicList;
		this.npiNumber = npiNumber;
	}

	public String getParentClinicName() {
		return parentClinicName;
	}

	public void setParentClinicName(String parentClinicName) {
		this.parentClinicName = parentClinicName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getZipcode() {
		return zipcode;
	}

	public void setZipcode(Integer zipcode) {
		this.zipcode = zipcode;
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

	public Long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(Long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Long getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(Long faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getClinicAdminId() {
		return clinicAdminId;
	}

	public void setClinicAdminId(String clinicAdminId) {
		this.clinicAdminId = clinicAdminId;
	}

	public ArrayList<String> getChildClinicList() {
		return childClinicList;
	}

	public void setChildClinicList(ArrayList<String> childClinicList) {
		this.childClinicList = childClinicList;
	}

	public String getNpiNumber() {
		return npiNumber;
	}

	public void setNpiNumber(String npiNumber) {
		this.npiNumber = npiNumber;
	}
}
