package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A UserExtension.
 */
@Entity
@Table(name = "USER_EXTENSION")
public class UserExtension extends User implements Serializable {

    @Column(name = "speciality")
    private String speciality;

    @Column(name = "credentials")
    private String credentials;
    
    @Column(name = "primary_phone")
    private Long primaryPhone;

    @Column(name = "mobile_phone")
    private Long mobilePhone;

    @Column(name = "fax_number")
    private Long faxNumber;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "zipcode")
    private Integer zipcode;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "state")
    private String state;
    
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

	@Override
	public String toString() {
		return "UserExtension [speciality=" + speciality + ", credentials="
				+ credentials + ", primaryPhone=" + primaryPhone
				+ ", mobilePhone=" + mobilePhone + ", faxNumber=" + faxNumber
				+ ", address=" + address + ", zipcode=" + zipcode + ", city="
				+ city + ", state=" + state + "]";
	}
}
