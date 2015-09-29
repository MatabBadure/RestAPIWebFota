package com.hillrom.vest.web.rest.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hillrom.vest.domain.Authority;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.UserExtension;

public class HcpClinicsVO implements Serializable{

    private Long id;

    private String title;
    
    private String firstName;

    private String middleName;

    private String lastName;

    private String email;
    
    private String gender;
    
    private Integer zipcode;

    private boolean activated = false;

    private String langKey;

    private boolean termsConditionAccepted = false;

    private DateTime termsConditionAcceptedDate = null;

    private Set<Authority> authorities = new HashSet<>();
    
    private boolean deleted = false;
    
    private DateTime lastLoggedInAt;

    private String speciality;

    private String credentials;

    private String primaryPhone;

    private String mobilePhone;

    private String faxNumber;
    
    private String address;

    private String city;

    private String state;
    
    private String npiNumber;
    
    private Set<Clinic> clinics = new HashSet<>();
    
    private String hillromId;
    
    private LocalDate dob;
    
    private boolean missedTherapyNotification;

    private boolean nonHMRNotification;
    
    private boolean  settingDeviationNotification;

    
	@JsonIgnore
	private UserExtension hcpUsers;
	
	public HcpClinicsVO(UserExtension hcp, Set<Clinic> clinicsHcpAssociated) {
		super();
		this.hcpUsers = hcp;
		this.clinics = clinicsHcpAssociated;
		build(hcp);
	}
	
	public void build(UserExtension hcp){
		this.id = hcp.getId();
		this.title = hcp.getTitle();
		this.firstName = hcp.getFirstName();
		this.middleName = hcp.getMiddleName();
		this.lastName = hcp.getLastName();
		this.email = hcp.getEmail();
		this.zipcode = hcp.getZipcode();
		this.activated = hcp.getActivated();
		this.langKey = hcp.getLangKey();
		this.termsConditionAccepted = hcp.getTermsConditionAccepted();
		this.termsConditionAcceptedDate = hcp.getTermsConditionAcceptedDate();
		this.authorities = hcp.getAuthorities();
	    this.deleted = hcp.isDeleted();
	    this.lastLoggedInAt = hcp.getLastLoggedInAt();
	    this.speciality = hcp.getSpeciality();
	    this.credentials = hcp.getCredentials();
	    this.primaryPhone = hcp.getPrimaryPhone();
	    this.mobilePhone = hcp.getMobilePhone();
	    this.faxNumber = hcp.getFaxNumber();
	    this.address = hcp.getAddress();
	    this.city = hcp.getCity(); 
	    this.state = hcp.getState();
	    this.npiNumber = hcp.getNpiNumber();
	    this.hillromId = hcp.getHillromId();
	    this.dob = hcp.getDob();
	    this.missedTherapyNotification = hcp.isMissedTherapyNotification();
	    this.nonHMRNotification = hcp.isNonHMRNotification();
	    this.settingDeviationNotification = hcp.isSettingDeviationNotification();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getZipcode() {
		return zipcode;
	}

	public void setZipcode(Integer zipcode) {
		this.zipcode = zipcode;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getLangKey() {
		return langKey;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	public boolean isTermsConditionAccepted() {
		return termsConditionAccepted;
	}

	public void setTermsConditionAccepted(boolean termsConditionAccepted) {
		this.termsConditionAccepted = termsConditionAccepted;
	}

	public DateTime getTermsConditionAcceptedDate() {
		return termsConditionAcceptedDate;
	}

	public void setTermsConditionAcceptedDate(DateTime termsConditionAcceptedDate) {
		this.termsConditionAcceptedDate = termsConditionAcceptedDate;
	}

	public Set<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public DateTime getLastLoggedInAt() {
		return lastLoggedInAt;
	}

	public void setLastLoggedInAt(DateTime lastLoggedInAt) {
		this.lastLoggedInAt = lastLoggedInAt;
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

	public Set<Clinic> getClinics() {
		return clinics;
	}

	public void setClinics(Set<Clinic> clinics) {
		this.clinics = clinics;
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

	public boolean isMissedTherapyNotification() {
		return missedTherapyNotification;
	}

	public void setMissedTherapyNotification(boolean missedTherapyNotification) {
		this.missedTherapyNotification = missedTherapyNotification;
	}

	public boolean isNonHMRNotification() {
		return nonHMRNotification;
	}

	public void setNonHMRNotification(boolean nonHMRNotification) {
		this.nonHMRNotification = nonHMRNotification;
	}

	public boolean isSettingDeviationNotification() {
		return settingDeviationNotification;
	}

	public void setSettingDeviationNotification(boolean settingDeviationNotification) {
		this.settingDeviationNotification = settingDeviationNotification;
	}

	public UserExtension getHcpUsers() {
		return hcpUsers;
	}

	public void setHcpUsers(UserExtension hcpUsers) {
		this.hcpUsers = hcpUsers;
	}

}
