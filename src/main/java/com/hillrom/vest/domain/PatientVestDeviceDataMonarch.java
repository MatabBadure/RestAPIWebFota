package com.hillrom.vest.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@IdClass(PatientVestDeviceDataMonarchPK.class)
@Entity
@Table(name = "PATIENT_VEST_DEVICE_DATA_MONARCH")
public class PatientVestDeviceDataMonarch implements Serializable,Comparable<PatientVestDeviceDataMonarch>,Cloneable {

	private static final long serialVersionUID = 1L;
	@Id
	private Long timestamp;

	@Column(name = "sequence_number")
	private Integer sequenceNumber;

	@Id
	@Column(name="event_code")
	private String eventCode;

	@Column(name = "serial_number")
	private String serialNumber;
	
	@Id
	@Column(name = "bluetooth_id")
	private String bluetoothId;
	
	
	private Double hmr;
	
	private Integer frequency;
	
	private Integer intensity;
	
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
	
	@Column(name = "frag_total")
	private Integer fragTotal;
	
	@Column(name = "frag_current")
	private Integer fragCurrent;
	
	@Column(name = "therapy_index")
	private Integer therapyIndex;
	
	@Column(name = "start_battery_level")
	private Integer startBatteryLevel;
	
	@Column(name = "end_battery_level")
	private Integer endBatteryLevel;
	
	@Column(name = "number_of_events")
	private Integer numberOfEvents;
	
	@Column(name = "number_of_pods")
	private Integer numberOfPods;
	
	@Column(name = "dev_wifi")
	private String devWifi;
	
	@Column(name = "dev_lte")
	private String devLte;
	
	@Column(name = "dev_version")
	private String devVersion;
	
	
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}
	
	public String getBluetoothId() {
		return bluetoothId;
	}

	public void setBluetoothId(String bluetoothId) {
		this.bluetoothId = bluetoothId;
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




	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
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

	public Integer getIntensity() {
		return intensity;
	}

	public void setIntensity(Integer intensity) {
		this.intensity = intensity;
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

	public Integer getTherapyIndex() {
		return therapyIndex;
	}

	public void setTherapyIndex(Integer therapyIndex) {
		this.therapyIndex = therapyIndex;
	}

	public Integer getStartBatteryLevel() {
		return startBatteryLevel;
	}

	public void setStartBatteryLevel(Integer startBatteryLevel) {
		this.startBatteryLevel = startBatteryLevel;
	}

	public Integer getEndBatteryLevel() {
		return endBatteryLevel;
	}

	public void setEndBatteryLevel(Integer endBatteryLevel) {
		this.endBatteryLevel = endBatteryLevel;
	}

	public Integer getNumberOfEvents() {
		return numberOfEvents;
	}

	public void setNumberOfEvents(Integer numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
	}

	public Integer getNumberOfPods() {
		return numberOfPods;
	}

	public void setNumberOfPods(Integer numberOfPods) {
		this.numberOfPods = numberOfPods;
	}

	public String getDevWifi() {
		return devWifi;
	}

	public void setDevWifi(String devWifi) {
		this.devWifi = devWifi;
	}

	public String getDevLte() {
		return devLte;
	}

	public void setDevLte(String devLte) {
		this.devLte = devLte;
	}

	public String getDevVersion() {
		return devVersion;
	}

	public void setDevVersion(String devVersion) {
		this.devVersion = devVersion;
	}

	public Integer getFragTotal() {
		return fragTotal;
	}

	public void setFragTotal(Integer fragTotal) {
		this.fragTotal = fragTotal;
	}

	public Integer getFragCurrent() {
		return fragCurrent;
	}

	public void setFragCurrent(Integer fragCurrent) {
		this.fragCurrent = fragCurrent;
	}

	@JsonIgnore
	public DateTime getDate(){
		return new DateTime(this.timestamp);
	}
	
	@JsonIgnore
	public double getHmrInHours(){
		if(Objects.nonNull(hmr))
			return new BigDecimal(this.hmr/(60*60) ).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		else
			return 0;
	}
	
	@JsonIgnore
	public String getPatientBlueToothAddress(){
		return "PAT_ID:BT:"+this.bluetoothId;
	}
  
	@Override
	public int compareTo(PatientVestDeviceDataMonarch o) {
		return this.getTimestamp().compareTo(o.getTimestamp());
	}
	
	public Object clone()throws CloneNotSupportedException{  
		return super.clone();  
	}
}
