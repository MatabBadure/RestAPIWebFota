package com.hillrom.vest.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.CustomDateTimeDeserializer;
import com.hillrom.vest.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


/**
 * A PatientVestDeviceRawLog.
 */
@Entity
@Table(name = "PATIENTVESTDEVICERAWLOG")
public class PatientVestDeviceRawLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "device_model_type")
    private String deviceModelType;

    @Column(name = "device_data")
    private String deviceData;

    @Column(name = "device_serial_no")
    private String deviceSerialNo;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "hub_id")
    private String hubId;

    @Column(name = "air_interface_type")
    private String airInterfaceType;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "time_zone")
    private String timeZone;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "sp_receive_time")
    private DateTime spReceiveTime;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "hub_receive_time")
    private DateTime hubReceiveTime;

    @Column(name = "device_address")
    private String deviceAddress;

    @Column(name = "hub_receive_time_offset")
    private Integer hubReceiveTimeOffset;

    @Column(name = "cuc_version")
    private String cucVersion;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "raw_message")
    private String rawMessage;

    @Column(name = "raw_hexa_data")
    private String rawHexaData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDeviceSerialNo() {
        return deviceSerialNo;
    }

    public void setDeviceSerialNo(String deviceSerialNo) {
        this.deviceSerialNo = deviceSerialNo;
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

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public DateTime getSpReceiveTime() {
        return spReceiveTime;
    }

    public void setSpReceiveTime(DateTime spReceiveTime) {
        this.spReceiveTime = spReceiveTime;
    }

    public DateTime getHubReceiveTime() {
        return hubReceiveTime;
    }

    public void setHubReceiveTime(DateTime hubReceiveTime) {
        this.hubReceiveTime = hubReceiveTime;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public Integer getHubReceiveTimeOffset() {
        return hubReceiveTimeOffset;
    }

    public void setHubReceiveTimeOffset(Integer hubReceiveTimeOffset) {
        this.hubReceiveTimeOffset = hubReceiveTimeOffset;
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

    public String getRawHexaData() {
        return rawHexaData;
    }

    public void setRawHexaData(String rawHexaData) {
        this.rawHexaData = rawHexaData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PatientVestDeviceRawLog patientVestDeviceRawLog = (PatientVestDeviceRawLog) o;

        if ( ! Objects.equals(id, patientVestDeviceRawLog.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PatientVestDeviceRawLog{" +
                "id=" + id +
                ", deviceModelType='" + deviceModelType + "'" +
                ", deviceData='" + deviceData + "'" +
                ", deviceSerialNo='" + deviceSerialNo + "'" +
                ", deviceType='" + deviceType + "'" +
                ", hubId='" + hubId + "'" +
                ", airInterfaceType='" + airInterfaceType + "'" +
                ", customerName='" + customerName + "'" +
                ", timeZone='" + timeZone + "'" +
                ", spReceiveTime='" + spReceiveTime + "'" +
                ", hubReceiveTime='" + hubReceiveTime + "'" +
                ", deviceAddress='" + deviceAddress + "'" +
                ", hubReceiveTimeOffset='" + hubReceiveTimeOffset + "'" +
                ", cucVersion='" + cucVersion + "'" +
                ", customerId='" + customerId + "'" +
                ", rawMessage='" + rawMessage + "'" +
                ", rawHexaData='" + rawHexaData + "'" +
                '}';
    }
}
