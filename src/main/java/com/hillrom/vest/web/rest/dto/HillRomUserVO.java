package com.hillrom.vest.web.rest.dto;

import org.joda.time.DateTime;

public class HillRomUserVO {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String role;
	private boolean isDeleted;
	private DateTime createdAt;
	private boolean isActivated;
	private String hillromId;
	private String mobilePhone;
	
	public HillRomUserVO(Long id, String firstName, String lastName,
			String email, String role,boolean isDeleted,DateTime createdAt,boolean isActivated,String hillromId, String mobilePhone) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.role = role;
		this.isDeleted = isDeleted;
		this.createdAt = createdAt;
		this.isActivated = isActivated;
		this.hillromId = hillromId;
		this.mobilePhone = mobilePhone;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
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

	public String getHillromId() {
		return hillromId;
	}

	public void setHillromId(String hillromId) {
		this.hillromId = hillromId;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	
	
}
