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
 * A PATIENT_VEST_DEVICE_RAW_LOGS.
 */
@Entity
@Table(name = "PATIENT_VEST_DEVICE_RAW_LOGS")
public class PatientVestDeviceRawLog implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@NotNull
    @Column(name = "hub_receive_time", nullable = false)
    private Long hubReceiveTime;

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

    @Column(name = "hub_id")
    private String hubId;

    @Column(name = "air_interface_type")
    private String airInterfaceType;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "timezone")
    private String timezone;

    @NotNull
    @Column(name = "sp_receive_time", nullable = false)
    private Long spReceiveTime;

    @Column(name = "cuc_version")
    private String cucVersion;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "raw_message")
    private String rawMessage;

	public Long getHubReceiveTime() {
		return hubReceiveTime;
	}

	public void setHubReceiveTime(Long hubReceiveTime) {
		this.hubReceiveTime = hubReceiveTime;
	}

	public String getDeviceAddress() {
		return deviceAddress;
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

	public String getDeviceModelType() {
		return deviceModelType;
	}

	public void setDeviceModelType(String deviceModelType) {
		this.deviceModelType = deviceModelType;
	}

	public String getDeviceData() {
		return deviceData;
	}

	public void setDeviceData(String deviceData) {
		this.deviceData = deviceData;
	}

	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}

	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getHubId() {
		return hubId;
	}

	public void setHubId(String hubId) {
		this.hubId = hubId;
	}

	public String getAirInterfaceType() {
		return airInterfaceType;
	}

	public void setAirInterfaceType(String airInterfaceType) {
		this.airInterfaceType = airInterfaceType;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public Long getSpReceiveTime() {
		return spReceiveTime;
	}

	public void setSpReceiveTime(Long spReceiveTime) {
		this.spReceiveTime = spReceiveTime;
	}

	public String getCucVersion() {
		return cucVersion;
	}

	public void setCucVersion(String cucVersion) {
		this.cucVersion = cucVersion;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getRawMessage() {
		return rawMessage;
	}

	public void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatientVestDeviceRawLog other = (PatientVestDeviceRawLog) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatientVestDeviceRawLog [hubReceiveTime=" + hubReceiveTime
				+ ", deviceAddress=" + deviceAddress + ", deviceModelType="
				+ deviceModelType + ", deviceData=" + deviceData
				+ ", deviceSerialNumber=" + deviceSerialNumber
				+ ", deviceType=" + deviceType + ", hubId=" + hubId
				+ ", airInterfaceType=" + airInterfaceType + ", customerName="
				+ customerName + ", timezone=" + timezone + ", spReceiveTime="
				+ spReceiveTime + ", cucVersion=" + cucVersion
				+ ", customerId=" + customerId + ", rawMessage=" + rawMessage
				+ "]";
	}

	
}
