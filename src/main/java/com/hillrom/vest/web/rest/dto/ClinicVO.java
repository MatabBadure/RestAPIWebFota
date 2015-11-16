package com.hillrom.vest.web.rest.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ClinicVO implements Serializable {

    private String id;

    private String name;

    private String address1;
    
    private String address2;

    private Integer zipcode;

    private String city;

    private String state;

    private String phoneNumber;

    private String faxNumber;

    private String hillromId;
    
    private Long clinicAdminId;

    private ClinicVO parentClinic;
 
    private boolean deleted = false;
    
    private boolean parent = false;

    private DateTime createdAt;

    @JsonIgnore
    private List<ClinicVO> childClinicVOs = new LinkedList<>();

    public ClinicVO(String id,String name, String address1, String address2, Integer zipcode, String city,
			String state, String phoneNumber, String faxNumber, Long clinicAdminId,
			Boolean parent, String hillromId,Boolean deleted,DateTime createdAt) {
		super();
		this.id = id;
		this.name = name;
		this.address1 = address1;
		this.address2 = address2;
		this.zipcode = zipcode;
		this.city = city;
		this.state = state;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.clinicAdminId = clinicAdminId;
		this.parent = parent;
		this.hillromId = hillromId;
		this.deleted = deleted;
		this.createdAt = createdAt;
	}
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getZipcode() {
        return zipcode;
    }

    public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
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

    public Long getClinicAdminId() {
		return clinicAdminId;
	}

	public void setClinicAdminId(Long clinicAdminId) {
		this.clinicAdminId = clinicAdminId;
	}

    public ClinicVO getParentClinic() {
		return parentClinic;
	}

	public void setParentClinic(ClinicVO parentClinic) {
		this.parentClinic = parentClinic;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.deleted = isDeleted;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<ClinicVO> getChildClinicVOs() {
		return childClinicVOs;
	}

	public void setChildClinicVOs(List<ClinicVO> childClinicVOs) {
		this.childClinicVOs = childClinicVOs;
	}

	
}
