package com.hillrom.vest.web.rest.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hillrom.vest.domain.UserPatientAssoc;

public class ClinicVO implements Serializable,Comparable<ClinicVO> {

    private String id;

    private String name;

    private String address;
    
    private String address2;

    private String zipcode;

    private String city;

    private String state;
    
    private String country;

    private String phoneNumber;

    private String faxNumber;
    
    private String speciality;

    private String hillromId;
    
    private Long clinicAdminId;

    private String parentClinicId;
    
    private ClinicVO parentClinic;
 
    private boolean deleted = false;
    
    private boolean parent = false;

    private DateTime createdAt;
    
    private Integer adherenceSetting;

    //start: HILL-2004
    private DateTime adherenceSettingModifiedDte;
    
    private Boolean adherenceSettingFlag;
    //end: HILL-2004
    
    Boolean isMessageOpted;
    
    @JsonIgnore
    private List<ClinicVO> childClinicVOs = new LinkedList<>();

    public ClinicVO(String id,String name, String address, String address2, String zipcode, String city,
			String state, String phoneNumber, String faxNumber, String speciality, Long clinicAdminId,
			Boolean parent, String hillromId,Boolean deleted,DateTime createdAt,Integer adherenceSetting, 
			DateTime adherenceSettingModifiedDte
			) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.address2 = address2;
		this.zipcode = zipcode;
		this.city = city;
		this.state = state;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.speciality = speciality;
		this.clinicAdminId = clinicAdminId;
		this.parent = parent;
		this.hillromId = hillromId;
		this.deleted = deleted;
		this.createdAt = createdAt;
		this.adherenceSetting = adherenceSetting;
		//start: HILL-2004
		this.adherenceSettingModifiedDte = adherenceSettingModifiedDte;
		this.adherenceSettingFlag = adherenceSettingFlag;
		//end: HILL-2004
				
	}
    
    public ClinicVO(String id,String name, String address, String address2, String zipcode, String city,
			String state, String phoneNumber, String faxNumber, String speciality, Long clinicAdminId,
			Boolean parent, String hillromId,Boolean deleted,DateTime createdAt,Integer adherenceSetting, 
			DateTime adherenceSettingModifiedDte, Boolean isMessageOpted
			) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.address2 = address2;
		this.zipcode = zipcode;
		this.city = city;
		this.state = state;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.speciality = speciality;
		this.clinicAdminId = clinicAdminId;
		this.parent = parent;
		this.hillromId = hillromId;
		this.deleted = deleted;
		this.createdAt = createdAt;
		this.adherenceSetting = adherenceSetting;
		//start: HILL-2004
		this.adherenceSettingModifiedDte = adherenceSettingModifiedDte;
		this.adherenceSettingFlag = adherenceSettingFlag;
		//end: HILL-2004
		this.isMessageOpted=isMessageOpted;
				
	}
    
    public ClinicVO(String id,String name, String address, String address2, String zipcode, String city,
			String state, String country, String phoneNumber, String faxNumber, String speciality, Long clinicAdminId,
			Boolean parent,String parentClinicId, String hillromId,Boolean deleted,DateTime createdAt,Integer adherenceSetting, DateTime adherenceSettingModifiedDte
			) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.address2 = address2;
		this.zipcode = zipcode;
		this.city = city;
		this.state = state;
		this.country = country;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.speciality = speciality;
		this.clinicAdminId = clinicAdminId;
		this.parent = parent;
		this.parentClinicId = parentClinicId;
		this.hillromId = hillromId;
		this.deleted = deleted;
		this.createdAt = createdAt;
		this.adherenceSetting = adherenceSetting;
		//start: HILL-2004
		this.adherenceSettingModifiedDte = adherenceSettingModifiedDte;
		this.adherenceSettingFlag = adherenceSettingFlag;
		//end: HILL-2004
				
	}
    
    public ClinicVO(String id, String name, String address,
			String address2, String zipcode, String country,String city, String state,
			String phoneNumber, String faxNumber, String speciality,
			String clinicAdminId, Boolean parent, String parentClinicId, String hillromId,
			Boolean deleted, DateTime createdAt, Integer adherenceSetting,
			DateTime adherenceSettingModifiedDte) {
    	super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.address2 = address2;
		this.zipcode = zipcode;
		this.city = city;
		this.state = state;
		this.country = country;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.speciality = speciality;
		this.clinicAdminId = Objects.nonNull(clinicAdminId)?Long.parseLong(clinicAdminId):null;
		this.parent = parent;
		this.parentClinicId = parentClinicId;
		this.hillromId = hillromId;
		this.deleted = deleted;
		this.createdAt = createdAt;
		this.adherenceSetting = adherenceSetting;
		//start: HILL-2004
		this.adherenceSettingModifiedDte = adherenceSettingModifiedDte;
		//end: HILL-2004
				
		// TODO Auto-generated constructor stub
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

    public String getZipcode() {
        return zipcode;
    }

    public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public void setZipcode(String zipcode) {
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
    
    

    /**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
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

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
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

	public Integer getAdherenceSetting() {
        return adherenceSetting;
    }
	
	public void setAdherenceSetting(Integer adherenceSetting) {
        this.adherenceSetting = adherenceSetting;
    }
	
	//start: HILL-2004
	 public DateTime getAdherenceSettingModifiedDte() {
			return adherenceSettingModifiedDte;
		}

		public void setAdherenceSettingModifiedDte(DateTime adherenceSettingModifiedDte) {
			this.adherenceSettingModifiedDte = adherenceSettingModifiedDte;
		}
		
		public Boolean getAdherenceSettingFlag() {
			return adherenceSettingFlag;
		}

		public void setAdherenceSettingFlag(Boolean adherenceSettingFlag) {
			this.adherenceSettingFlag = adherenceSettingFlag;
		}
		
  //end: HILL-2004
		
		/**
		 * @return the parentClinicId
		 */
		public String getParentClinicId() {
			return parentClinicId;
		}

		/**
		 * @param parentClinicId the parentClinicId to set
		 */
		public void setParentClinicId(String parentClinicId) {
			this.parentClinicId = parentClinicId;
		}
	
		
		public Boolean getIsMessageOpted() {
			return isMessageOpted;
		}

		public void setIsMessageOpted(Boolean isMessageOpted) {
			this.isMessageOpted = isMessageOpted;
		}

		@Override
		public int compareTo(ClinicVO clinicVO) {
			
			
		    if (clinicVO.getAdherenceSettingModifiedDte() == null) {
		        return (this.getAdherenceSettingModifiedDte() == null) ? 0 : -1;
		    }
		    if (this.getAdherenceSettingModifiedDte() == null) {
		        return 1;
		    }
			 
			return this.getAdherenceSettingModifiedDte().compareTo(clinicVO.getAdherenceSettingModifiedDte());
		}


}