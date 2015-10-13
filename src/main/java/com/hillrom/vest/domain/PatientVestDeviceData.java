package com.hillrom.vest.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@IdClass(PatientVestDeviceDataPK.class)
@Entity
@Table(name = "PATIENT_VEST_DEVICE_DATA")
public class PatientVestDeviceData implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	private Long timestamp;

	@Column(name = "sequence_number")
	private Integer sequenceNumber;

	@Id
	@Column(name="event_id")
	private String eventId;

	@Column(name = "serial_number")
	private String serialNumber;
	
	@Id
	@Column(name = "bluetooth_id")
	private String bluetoothId;
	
	@Column(name="hub_id")
	private String hubId;
	
	
	private Double hmr;
	
	private Integer frequency;
	
	private Integer pressure;
	
	private Integer duration;

	@JsonIgnore
	@ManyToOne(optional=false,targetEntity=PatientInfo.class)
	@JoinColumn(name="patient_id",referencedColumnName="id")
	private PatientInfo patient;

	private Integer checksum;
	
	@JsonIgnore
	@ManyToOne(optional=false,targetEntity=User.class)
	@JoinColumn(name="user_id",referencedColumnName="id")
	private User patientUser;
	
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getBluetoothId() {
		return bluetoothId;
	}

	public void setBluetoothId(String bluetoothId) {
		this.bluetoothId = bluetoothId;
	}

	public String getHubId() {
		return hubId;
	}

	public void setHubId(String hubId) {
		this.hubId = hubId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	public Double getHmr() {
		return hmr;
	}

	public void setHmr(Double hmr) {
		this.hmr = hmr;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public Integer getPressure() {
		return pressure;
	}

	public void setPressure(Integer pressure) {
		this.pressure = pressure;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public PatientInfo getPatient() {
		return patient;
	}

	public void setPatient(PatientInfo patient) {
		this.patient = patient;
	}

	public Integer getChecksum() {
		return checksum;
	}

	public void setChecksum(Integer checksum) {
		this.checksum = checksum;
	}

	public User getPatientUser() {
		return patientUser;
	}

	public void setPatientUser(User patientUser) {
		this.patientUser = patientUser;
	}

	@Override
	public String toString() {
		return "PatientVestDeviceData [timestamp=" + timestamp
				+ ", sequenceNumber=" + sequenceNumber + ", eventId=" + eventId
				+ ", serialNumber=" + serialNumber + ", bluetoothId="
				+ bluetoothId + ", hubId=" + hubId + ", hmr=" + hmr
				+ ", frequency=" + frequency + ", pressure=" + pressure
				+ ", duration=" + duration + ", patient=" + patient
				+ ", checksum=" + checksum + "]";
	}	

	@JsonIgnore
	public LocalDate getDate(){
		return LocalDate.fromDateFields(new Date(this.timestamp));
	}
	
	@JsonIgnore
	public double getHmrInMinutes(){
		return this.hmr/60;
	}
}
