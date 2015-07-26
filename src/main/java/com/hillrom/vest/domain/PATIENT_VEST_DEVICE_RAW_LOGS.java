package com.hillrom.vest.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.CustomDateTimeDeserializer;
import com.hillrom.vest.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A PATIENT_VEST_DEVICE_RAW_LOGS.
 */
@Entity
@Table(name = "PATIENT_VEST_DEVICE_RAW_LOGS")
public class PATIENT_VEST_DEVICE_RAW_LOGS implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "hub_receive_time", nullable = false)
    private DateTime hub_receive_time;

    @NotNull
    @Column(name = "device_address", nullable = false)
    private String device_address;

    @Column(name = "device_model_type")
    private String device_model_type;

    @Column(name = "device_data")
    private String device_data;

    @Column(name = "device_serial_number")
    private String device_serial_number;

    @Column(name = "device_type")
    private String device_type;

    @Column(name = "hub_id")
    private String hub_id;

    @Column(name = "air_interface_type")
    private String air_interface_type;

    @Column(name = "customer_name")
    private String customer_name;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "sp_receive_time")
    private String sp_receive_time;

    @Column(name = "hub_receive_time_offset")
    private Integer hub_receive_time_offset;

    @Column(name = "cuc_version")
    private String cuc_version;

    @Column(name = "customer_id")
    private String customer_id;

    @Column(name = "raw_message")
    private String raw_message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getHub_receive_time() {
        return hub_receive_time;
    }

    public void setHub_receive_time(DateTime hub_receive_time) {
        this.hub_receive_time = hub_receive_time;
    }

    public String getDevice_address() {
        return device_address;
    }

    public void setDevice_address(String device_address) {
        this.device_address = device_address;
    }

    public String getDevice_model_type() {
        return device_model_type;
    }

    public void setDevice_model_type(String device_model_type) {
        this.device_model_type = device_model_type;
    }

    public String getDevice_data() {
        return device_data;
    }

    public void setDevice_data(String device_data) {
        this.device_data = device_data;
    }

    public String getDevice_serial_number() {
        return device_serial_number;
    }

    public void setDevice_serial_number(String device_serial_number) {
        this.device_serial_number = device_serial_number;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getHub_id() {
        return hub_id;
    }

    public void setHub_id(String hub_id) {
        this.hub_id = hub_id;
    }

    public String getAir_interface_type() {
        return air_interface_type;
    }

    public void setAir_interface_type(String air_interface_type) {
        this.air_interface_type = air_interface_type;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getSp_receive_time() {
        return sp_receive_time;
    }

    public void setSp_receive_time(String sp_receive_time) {
        this.sp_receive_time = sp_receive_time;
    }

    public Integer getHub_receive_time_offset() {
        return hub_receive_time_offset;
    }

    public void setHub_receive_time_offset(Integer hub_receive_time_offset) {
        this.hub_receive_time_offset = hub_receive_time_offset;
    }

    public String getCuc_version() {
        return cuc_version;
    }

    public void setCuc_version(String cuc_version) {
        this.cuc_version = cuc_version;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getRaw_message() {
        return raw_message;
    }

    public void setRaw_message(String raw_message) {
        this.raw_message = raw_message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PATIENT_VEST_DEVICE_RAW_LOGS pATIENT_VEST_DEVICE_RAW_LOGS = (PATIENT_VEST_DEVICE_RAW_LOGS) o;

        if ( ! Objects.equals(id, pATIENT_VEST_DEVICE_RAW_LOGS.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PATIENT_VEST_DEVICE_RAW_LOGS{" +
                "id=" + id +
                ", hub_receive_time='" + hub_receive_time + "'" +
                ", device_address='" + device_address + "'" +
                ", device_model_type='" + device_model_type + "'" +
                ", device_data='" + device_data + "'" +
                ", device_serial_number='" + device_serial_number + "'" +
                ", device_type='" + device_type + "'" +
                ", hub_id='" + hub_id + "'" +
                ", air_interface_type='" + air_interface_type + "'" +
                ", customer_name='" + customer_name + "'" +
                ", timezone='" + timezone + "'" +
                ", sp_receive_time='" + sp_receive_time + "'" +
                ", hub_receive_time_offset='" + hub_receive_time_offset + "'" +
                ", cuc_version='" + cuc_version + "'" +
                ", customer_id='" + customer_id + "'" +
                ", raw_message='" + raw_message + "'" +
                '}';
    }
}
