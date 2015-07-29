package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;

@IdClass(PatientVestDeviceDataPK.class)
@Entity
@Table(name = "PATIENT_VEST_DEVICE_DATA")
public class PatientVestDeviceData implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	private DateTime timestamp;
	@Id
	@Column(name = "sequence_number")
	private Integer sequenceNumber;

	@Column(name = "serial_number")
	private String serialNumber;
	
	@Column(name = "bluetooth_id")
	private String bluetoothId;
	
	@Column(name="hub_id")
	private String hubId;
	
	@Column(name="event_id")
	private String eventId;
	
	private Double hmr;
	
	private Integer frequency;
	
	private Integer pressure;
	
	private Integer duration;

	@ManyToOne(optional=false,targetEntity=PatientInfo.class)
	@JoinColumn(name="patient_id",referencedColumnName="id")
	private PatientInfo patient;
	
	public DateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(DateTime timestamp) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sequenceNumber == null) ? 0 : sequenceNumber.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
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
		PatientVestDeviceData other = (PatientVestDeviceData) obj;
		if (sequenceNumber == null) {
			if (other.sequenceNumber != null)
				return false;
		} else if (!sequenceNumber.equals(other.sequenceNumber))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "\nPatientVestDeviceData [timestamp=" + timestamp
				+ ", sequenceNumber=" + sequenceNumber + ", serialNumber="
				+ serialNumber + ", bluetoothId=" + bluetoothId + ", hubId="
				+ hubId + ", eventId=" + eventId + ", hmr=" + hmr
				+ ", frequency=" + frequency + ", pressure=" + pressure
				+ ", duration=" + duration + "]";
	}
	
	

}
