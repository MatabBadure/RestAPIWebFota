package com.hillrom.vest.web.rest.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Size;

public class ClinicDTO {
	
	@Size(max = 50)
	private String name;

	@Size(max = 50)
    private String address;

	@Size(max = 50)
    private Integer zipcode;

	@Size(max = 50)
    private String city;

	@Size(max = 50)
    private String state;

	@Size(max = 50)
    private String phoneNumber;

	@Size(max = 50)
    private String faxNumber;

	@Size(max = 50)
    private String hillromId;
	
	@Size(max = 50)
    private Long clinicAdminId;
	
	private List<Map<String, String>> childClinicList = new ArrayList<Map<String,String>>();
	
	private Boolean parent;

	@Size(max = 50)
    private Map<String, String> parentClinic = new HashMap<>();

	public ClinicDTO() {
		super();
	}

	public ClinicDTO(String name, String address, Integer zipcode, String city,
			String state, String phoneNumber, String faxNumber, Long clinicAdminId,
			Boolean parent, String hillromId) {
		super();
		this.name = name;
		this.address = address;
		this.zipcode = zipcode;
		this.city = city;
		this.state = state;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.clinicAdminId = clinicAdminId;
		this.parent = parent;
		this.hillromId = hillromId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getHillromId() {
		return hillromId;
	}

	public void setHillromId(String hillromId) {
		this.hillromId = hillromId;
	}
	
	public Map<String, String> getParentClinic() {
		return parentClinic;
	}

	public void setParentClinic(Map<String, String> parentClinic) {
		this.parentClinic = parentClinic;
	}

	public Long getClinicAdminId() {
		return clinicAdminId;
	}

	public void setClinicAdminId(Long clinicAdminId) {
		this.clinicAdminId = clinicAdminId;
	}

	public Boolean getParent() {
		return parent;
	}

	public void setParent(Boolean parent) {
		this.parent = parent;
	}

	public List<Map<String, String>> getChildClinicList() {
		return childClinicList;
	}

	public void setChildClinicList(List<Map<String, String>> childClinicList) {
		this.childClinicList = childClinicList;
	}
}
