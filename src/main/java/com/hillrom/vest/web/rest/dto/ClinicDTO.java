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
    private Long phoneNumber;

	@Size(max = 50)
    private Long faxNumber;

	@Size(max = 50)
    private String hillromId;
	
	@Size(max = 50)
    private Long clinicAdminId;
	
	private List<Map<String, String>> childClinicList = new ArrayList<Map<String,String>>();
	
	private Boolean isParent;

	@Size(max = 50)
    private Map<String, Long> parentClinic = new HashMap<>();

	public ClinicDTO() {
		super();
	}

	public ClinicDTO(String name, String address, Integer zipcode, String city,
			String state, Long phoneNumber, Long faxNumber, Long clinicAdminId,
			Boolean isParent, String hillromId) {
		super();
		this.name = name;
		this.address = address;
		this.zipcode = zipcode;
		this.city = city;
		this.state = state;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.clinicAdminId = clinicAdminId;
		this.isParent = isParent;
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

	public String getHillromId() {
		return hillromId;
	}

	public void setHillromId(String hillromId) {
		this.hillromId = hillromId;
	}

	public Map<String, Long> getParentClinic() {
		return parentClinic;
	}

	public void setParentClinic(Map<String, Long> parentClinic) {
		this.parentClinic = parentClinic;
	}

	public Long getClinicAdminId() {
		return clinicAdminId;
	}

	public void setClinicAdminId(Long clinicAdminId) {
		this.clinicAdminId = clinicAdminId;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public List<Map<String, String>> getChildClinicList() {
		return childClinicList;
	}

	public void setChildClinicList(List<Map<String, String>> childClinicList) {
		this.childClinicList = childClinicList;
	}
}
