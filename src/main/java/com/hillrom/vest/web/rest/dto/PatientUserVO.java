package com.hillrom.vest.web.rest.dto;

import java.util.Date;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.service.util.RandomUtil;

public class PatientUserVO {
	/**
	 * user.id,user.email,user.first_name as firstName,user.last_name as
	 * lastName" +
	 * " user.is_deleted as isDeleted,user.zipcode,user.address,user.city,user.dob,user.gender,user.title,patInfo.hillrom_id "
	 */
	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private Boolean isDeleted;
	private Integer zipcode;
	private String address;
	private String city;
	private Date dob;
	private String gender;
	private String title;
	private String hillromId;
	private String langKey;
	private String middleName;
	private String state;
	private Long mobilePhone;
	private Long primaryPhone;
	
	public PatientUserVO(Long id, String email, String firstName,
			String lastName, Boolean isDeleted, Integer zipcode,
			String address, String city, Date dob, String gender, String title,
			String hillromId) {
		super();
		this.id = id;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isDeleted = isDeleted;
		this.zipcode = zipcode;
		this.address = address;
		this.city = city;
		this.dob = dob;
		this.gender = gender;
		this.title = title;
		this.hillromId = hillromId;
	}

	public PatientUserVO(UserExtension user, PatientInfo patientInfo) {
		this.id = user.getId();
		this.email = RandomUtil.isValidEmail(user.getEmail())? user.getEmail():null;
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.isDeleted = user.isDeleted();
		this.zipcode = user.getZipcode();
		this.dob = user.getDob() != null ?user.getDob().toDate(): null;
		this.title = user.getTitle();
		this.langKey = user.getLangKey();
		this.middleName = user.getMiddleName();
		this.mobilePhone = user.getMobilePhone();
		this.primaryPhone = user.getPrimaryPhone();
		if(null != patientInfo){			
			this.state = patientInfo.getState();
			this.hillromId = patientInfo.getHillromId();
			this.gender = patientInfo.getGender();
			this.city = patientInfo.getCity();
			this.address = patientInfo.getAddress();
		}
		
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

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
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

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHillromId() {
		return hillromId;
	}

	public void setHillromId(String hillromId) {
		this.hillromId = hillromId;
	}

	public String getLangKey() {
		return langKey;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(Long mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Long getPrimaryPhone() {
		return primaryPhone;
	}

	public void setPrimaryPhone(Long primaryPhone) {
		this.primaryPhone = primaryPhone;
	}

	
}
