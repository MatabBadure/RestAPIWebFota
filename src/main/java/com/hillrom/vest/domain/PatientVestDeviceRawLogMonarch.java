package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * A PATIENT_VEST_DEVICE_RAW_LOGS_MONARCH.
 */
@Entity
@Table(name = "PATIENT_VEST_DEVICE_RAW_LOGS_MONARCH")
public class PatientVestDeviceRawLogMonarch implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	

    @NotNull
    @Column(name = "device_address", nullable = false)
    private String deviceAddress;

    @Column(name = "device_model_type")
    private String deviceModelType;

    @Column(name = "device_data")
    private String deviceData;

    @Column(name = "device_serial_number")
    private String deviceSerialNumber;

    @Column(name = "device_type")
    private String deviceType;


    @Column(name = "air_interface_type")
    private String airInterfaceType;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "timezone")
    private String timezone;


    @Column(name = "cuc_version")
    private String cucVersion;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "raw_message")
    private String rawMessage;
    
    @Column(name = "checksum")
    private String checksum;
    
    @Column(name = "total_fragments")
    private String totalFragments;
    
    @Column(name = "current_fragment")
    private String currentFragment;

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
	 * @return the deviceAddress
	 */
	public String getDeviceAddress() {
		return deviceAddress;
	}

	/**
	 * @param deviceAddress the deviceAddress to set
	 */
	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

	/**
	 * @return the deviceModelType
	 */
	public String getDeviceModelType() {
		return deviceModelType;
	}

	/**
	 * @param deviceModelType the deviceModelType to set
	 */
	public void setDeviceModelType(String deviceModelType) {
		this.deviceModelType = deviceModelType;
	}

	/**
	 * @return the deviceData
	 */
	public String getDeviceData() {
		return deviceData;
	}

	/**
	 * @param deviceData the deviceData to set
	 */
	public void setDeviceData(String deviceData) {
		this.deviceData = deviceData;
	}

	/**
	 * @return the deviceSerialNumber
	 */
	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}

	/**
	 * @param deviceSerialNumber the deviceSerialNumber to set
	 */
	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
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
	 * @return the airInterfaceType
	 */
	public String getAirInterfaceType() {
		return airInterfaceType;
	}

	/**
	 * @param airInterfaceType the airInterfaceType to set
	 */
	public void setAirInterfaceType(String airInterfaceType) {
		this.airInterfaceType = airInterfaceType;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the cucVersion
	 */
	public String getCucVersion() {
		return cucVersion;
	}

	/**
	 * @param cucVersion the cucVersion to set
	 */
	public void setCucVersion(String cucVersion) {
		this.cucVersion = cucVersion;
	}

	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the rawMessage
	 */
	public String getRawMessage() {
		return rawMessage;
	}

	/**
	 * @param rawMessage the rawMessage to set
	 */
	public void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	/**
	 * @return the checksum
	 */
	public String getChecksum() {
		return checksum;
	}

	/**
	 * @param checksum the checksum to set
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	/**
	 * @return the totalFragments
	 */
	public String getTotalFragments() {
		return totalFragments;
	}

	/**
	 * @param totalFragments the totalFragments to set
	 */
	public void setTotalFragments(String totalFragments) {
		this.totalFragments = totalFragments;
	}

	/**
	 * @return the currentFragment
	 */
	public String getCurrentFragment() {
		return currentFragment;
	}

	/**
	 * @param currentFragment the currentFragment to set
	 */
	public void setCurrentFragment(String currentFragment) {
		this.currentFragment = currentFragment;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((airInterfaceType == null) ? 0 : airInterfaceType.hashCode());
		result = prime * result + ((checksum == null) ? 0 : checksum.hashCode());
		result = prime * result + ((cucVersion == null) ? 0 : cucVersion.hashCode());
		result = prime * result + ((currentFragment == null) ? 0 : currentFragment.hashCode());
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((customerName == null) ? 0 : customerName.hashCode());
		result = prime * result + ((deviceAddress == null) ? 0 : deviceAddress.hashCode());
		result = prime * result + ((deviceData == null) ? 0 : deviceData.hashCode());
		result = prime * result + ((deviceModelType == null) ? 0 : deviceModelType.hashCode());
		result = prime * result + ((deviceSerialNumber == null) ? 0 : deviceSerialNumber.hashCode());
		result = prime * result + ((deviceType == null) ? 0 : deviceType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((rawMessage == null) ? 0 : rawMessage.hashCode());
		result = prime * result + ((timezone == null) ? 0 : timezone.hashCode());
		result = prime * result + ((totalFragments == null) ? 0 : totalFragments.hashCode());
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
		PatientVestDeviceRawLogMonarch other = (PatientVestDeviceRawLogMonarch) obj;
		if (airInterfaceType == null) {
			if (other.airInterfaceType != null)
				return false;
		} else if (!airInterfaceType.equals(other.airInterfaceType))
			return false;
		if (checksum == null) {
			if (other.checksum != null)
				return false;
		} else if (!checksum.equals(other.checksum))
			return false;
		if (cucVersion == null) {
			if (other.cucVersion != null)
				return false;
		} else if (!cucVersion.equals(other.cucVersion))
			return false;
		if (currentFragment == null) {
			if (other.currentFragment != null)
				return false;
		} else if (!currentFragment.equals(other.currentFragment))
			return false;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		if (customerName == null) {
			if (other.customerName != null)
				return false;
		} else if (!customerName.equals(other.customerName))
			return false;
		if (deviceAddress == null) {
			if (other.deviceAddress != null)
				return false;
		} else if (!deviceAddress.equals(other.deviceAddress))
			return false;
		if (deviceData == null) {
			if (other.deviceData != null)
				return false;
		} else if (!deviceData.equals(other.deviceData))
			return false;
		if (deviceModelType == null) {
			if (other.deviceModelType != null)
				return false;
		} else if (!deviceModelType.equals(other.deviceModelType))
			return false;
		if (deviceSerialNumber == null) {
			if (other.deviceSerialNumber != null)
				return false;
		} else if (!deviceSerialNumber.equals(other.deviceSerialNumber))
			return false;
		if (deviceType == null) {
			if (other.deviceType != null)
				return false;
		} else if (!deviceType.equals(other.deviceType))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (rawMessage == null) {
			if (other.rawMessage != null)
				return false;
		} else if (!rawMessage.equals(other.rawMessage))
			return false;
		if (timezone == null) {
			if (other.timezone != null)
				return false;
		} else if (!timezone.equals(other.timezone))
			return false;
		if (totalFragments == null) {
			if (other.totalFragments != null)
				return false;
		} else if (!totalFragments.equals(other.totalFragments))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PatientVestDeviceRawLogMonarch [id=" + id + ", deviceAddress=" + deviceAddress + ", deviceModelType="
				+ deviceModelType + ", deviceData=" + deviceData + ", deviceSerialNumber=" + deviceSerialNumber
				+ ", deviceType=" + deviceType + ", airInterfaceType=" + airInterfaceType + ", customerName="
				+ customerName + ", timezone=" + timezone + ", cucVersion=" + cucVersion + ", customerId=" + customerId
				+ ", rawMessage=" + rawMessage + ", checksum=" + checksum + ", totalFragments=" + totalFragments
				+ ", currentFragment=" + currentFragment + "]";
	}


 
	
}
