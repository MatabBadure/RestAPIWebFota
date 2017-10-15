package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.CustomLocalDateSerializer;
import com.hillrom.vest.domain.util.ISO8601LocalDateDeserializer;


/**
 * Patient Devices Assoc for Charger Device.
 */
@Entity
@Audited
@Table(name = "PATIENT_DEVICES_ASSOC")
public class PatientDevicesAssoc implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "patient_id")
    private String patientId;
    
    @Column(name = "device_type")
    private String deviceType;
    
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "serial_number")
    private String serialNumber;
    
    @Column(name = "hillrom_id")
    private String hillromId = null;
    
    @Column(name="created_date")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate createdDate;
    
    @Column(name = "old_patient_id")
    private String oldPatientId;
    

    @Column(name = "patient_type")
    private String patientType;

    
    @Column(name="modified_date")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate modifiedDate;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name="swapped_date")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate swappedDate;    
    
    @Column(name = "swapped_patient_id")
    private String swappedPatientId;
    
  //Garments changes
    @Column(name = "garment_type")
    private String garmentType;
    
    @Column(name = "garment_size")
    private String garmentSize;
    
    @Column(name = "garment_color")
    private String garmentColor;

    public PatientDevicesAssoc() {
		super();
	}

	public PatientDevicesAssoc(String patientId, String deviceType,Boolean isActive, String serialNumber) {
		super();
		this.patientId = patientId;
		this.deviceType = deviceType;
		this.isActive = isActive;
		this.serialNumber = serialNumber;

		this.hillromId = hillromId;


		this.patientType = "SD";
		this.createdDate = LocalDate.now();
		this.createdBy = "APP";
	}
	
	public PatientDevicesAssoc(String patientId, String deviceType, String patientType, Boolean isActive, String serialNumber, String hillromId) {
		super();
		this.patientId = patientId;
		this.deviceType = deviceType;
		this.patientType = patientType;
		this.isActive = isActive;
		this.serialNumber = serialNumber;
		this.hillromId = hillromId;
		this.createdDate = LocalDate.now();

		this.createdBy = "APP";

	}
    
	
	public String getGarmentType() {
		return garmentType;
	}

	public void setGarmentType(String garmentType) {
		this.garmentType = garmentType;
	}

	public String getGarmentSize() {
		return garmentSize;
	}

	public void setGarmentSize(String garmentSize) {
		this.garmentSize = garmentSize;
	}

	public String getGarmentColor() {
		return garmentColor;
	}

	public void setGarmentColor(String garmentColor) {
		this.garmentColor = garmentColor;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the patientId
	 */
	public String getPatientId() {
		return patientId;
	}

	/**
	 * @param patientId the patientId to set
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	/**
	 * @return the deviceType
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * @return the hillromId
	 */
	public String getHillromId() {
		return hillromId;
	}

	/**
	 * @param hillromId the hillromId to set
	 */
	public void setHillromId(String hillromId) {
		this.hillromId = hillromId;
	}
	
		/**
	 * @return the createdDate
	 */
	public LocalDate getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the oldPatientId
	 */
	public String getOldPatientId() {
		return oldPatientId;
	}

	/**
	 * @param patientId the patientId to set
	 */
	public void setOldPatientId(String oldPatientId) {
		this.oldPatientId = oldPatientId;
	}
	

	/**
	 * @return the patientType
	 */
	public String getPatientType() {
		return patientType;
	}

	/**
	 * @param patientType the patientType to set
	 */
	public void setPatientType(String patientType) {
		this.patientType = patientType;
	}


	/**
	 * @return the modifiedDate
	 */
	public LocalDate getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(LocalDate modifiedDate) {
		this.modifiedDate = modifiedDate;
	}


	/**
	 * @return the swappedDate
	 */
	public LocalDate getSwappedDate() {
		return swappedDate;
	}

	/**
	 * @param swappedDate the swappedDate to set
	 */
	public void setSwappedDate(LocalDate swappedDate) {
		this.swappedDate = swappedDate;
	}

	/**
	 * @return the swappedPatientId
	 */
	public String getSwappedPatientId() {
		return swappedPatientId;
	}

	/**
	 * @param swappedPatientId the swappedPatientId to set
	 */
	public void setSwappedPatientId(String swappedPatientId) {
		this.swappedPatientId = swappedPatientId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((deviceType == null) ? 0 : deviceType.hashCode());
		result = prime * result
				+ ((garmentColor == null) ? 0 : garmentColor.hashCode());
		result = prime * result
				+ ((garmentSize == null) ? 0 : garmentSize.hashCode());
		result = prime * result
				+ ((garmentType == null) ? 0 : garmentType.hashCode());
		result = prime * result
				+ ((hillromId == null) ? 0 : hillromId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((isActive == null) ? 0 : isActive.hashCode());
		result = prime * result
				+ ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result
				+ ((oldPatientId == null) ? 0 : oldPatientId.hashCode());
		result = prime * result
				+ ((patientId == null) ? 0 : patientId.hashCode());
		result = prime * result
				+ ((patientType == null) ? 0 : patientType.hashCode());
		result = prime * result
				+ ((serialNumber == null) ? 0 : serialNumber.hashCode());
		result = prime * result
				+ ((swappedDate == null) ? 0 : swappedDate.hashCode());
		result = prime
				* result
				+ ((swappedPatientId == null) ? 0 : swappedPatientId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatientDevicesAssoc other = (PatientDevicesAssoc) obj;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (deviceType == null) {
			if (other.deviceType != null)
				return false;
		} else if (!deviceType.equals(other.deviceType))
			return false;
		if (garmentColor == null) {
			if (other.garmentColor != null)
				return false;
		} else if (!garmentColor.equals(other.garmentColor))
			return false;
		if (garmentSize == null) {
			if (other.garmentSize != null)
				return false;
		} else if (!garmentSize.equals(other.garmentSize))
			return false;
		if (garmentType == null) {
			if (other.garmentType != null)
				return false;
		} else if (!garmentType.equals(other.garmentType))
			return false;
		if (hillromId == null) {
			if (other.hillromId != null)
				return false;
		} else if (!hillromId.equals(other.hillromId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isActive == null) {
			if (other.isActive != null)
				return false;
		} else if (!isActive.equals(other.isActive))
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (oldPatientId == null) {
			if (other.oldPatientId != null)
				return false;
		} else if (!oldPatientId.equals(other.oldPatientId))
			return false;
		if (patientId == null) {
			if (other.patientId != null)
				return false;
		} else if (!patientId.equals(other.patientId))
			return false;
		if (patientType == null) {
			if (other.patientType != null)
				return false;
		} else if (!patientType.equals(other.patientType))
			return false;
		if (serialNumber == null) {
			if (other.serialNumber != null)
				return false;
		} else if (!serialNumber.equals(other.serialNumber))
			return false;
		if (swappedDate == null) {
			if (other.swappedDate != null)
				return false;
		} else if (!swappedDate.equals(other.swappedDate))
			return false;
		if (swappedPatientId == null) {
			if (other.swappedPatientId != null)
				return false;
		} else if (!swappedPatientId.equals(other.swappedPatientId))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PatientDevicesAssoc [id=" + id + ", patientId=" + patientId
				+ ", deviceType=" + deviceType + ", isActive=" + isActive
				+ ", serialNumber=" + serialNumber + ", hillromId=" + hillromId
				+ ", createdDate=" + createdDate + ", oldPatientId="
				+ oldPatientId + ", patientType=" + patientType
				+ ", modifiedDate=" + modifiedDate + ", createdBy=" + createdBy
				+ ", swappedDate=" + swappedDate + ", swappedPatientId="
				+ swappedPatientId + ", garmentType=" + garmentType
				+ ", garmentSize=" + garmentSize + ", garmentColor="
				+ garmentColor + "]";
	}


}
