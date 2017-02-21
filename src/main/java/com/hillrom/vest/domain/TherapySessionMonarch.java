package com.hillrom.vest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.CustomLocalDateSerializer;
import com.hillrom.vest.domain.util.ISO8601LocalDateDeserializer;

@Entity
@Table(name="PATIENT_VEST_THERAPY_DATA_MONARCH")
public class TherapySessionMonarch implements Comparable<TherapySessionMonarch>{
	
	@JsonIgnore
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@JsonIgnore
	@ManyToOne(optional=false,targetEntity=PatientInfo.class)
	@JoinColumn(name="patient_id",referencedColumnName="id")
	private PatientInfo patientInfo;
	
	@JsonIgnore
	@ManyToOne(optional=false,targetEntity=User.class)
	@JoinColumn(name="user_id",referencedColumnName="id")
	private User patientUser;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate date;
	
	@Column(name="session_no")
	private Integer sessionNo;
	
	@Column(name="session_type")
	private String sessionType;
	
	@Column(name="start_time")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime startTime;
	
	@Column(name="end_time")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime endTime;
	
	@Column(name="frequency")
	private Integer frequency;
	
	@Column(name="pressure")
	private Integer pressure;
	
	@Column(name="intensity")
	private Integer intensity;
	
	@Column(name="duration_in_minutes")
	private int durationInMinutes;
	
	@Column(name="programmed_caugh_pauses")
	private Integer programmedCaughPauses;
	
	@Column(name="normal_caugh_pauses")
	private Integer normalCaughPauses;
	
	@Column(name="caugh_pause_duration")
	private Integer caughPauseDuration;
	
	@Column(name="hmr")
	private Double hmr;
	
	@Column(name="serial_number")
	private String serialNumber;
	
    @Column(name = "bluetooth_id")
	private String bluetoothId;
    
    @Column(name="therapy_index")
	private Integer therapyIndex;
    
    @Column(name="start_battery_level")
	private Integer startBatteryLevel;
    
    @Column(name="end_battery_level")
	private Integer endBatteryLevel;
    
    @Column(name="number_of_events")
	private Integer numberOfEvents;
    
    @Column(name="number_of_pods")
	private Integer numberOfPods;
    
    @Column(name = "dev_wifi")
	private String devWifi;
    
    @Column(name = "dev_version")
	private String devVersion;
    

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PatientInfo getPatientInfo() {
		return patientInfo;
	}

	public void setPatientInfo(PatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}

	public User getPatientUser() {
		return patientUser;
	}

	public void setPatientUser(User patientUser) {
		this.patientUser = patientUser;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Integer getSessionNo() {
		return sessionNo;
	}

	public void setSessionNo(Integer sessionNo) {
		this.sessionNo = sessionNo;
	}

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
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

	public int getDurationInMinutes() {
		return durationInMinutes;
	}

	public void setDurationInMinutes(int durationInSeconds) {
		this.durationInMinutes = durationInSeconds;
	}

	public Integer getProgrammedCaughPauses() {
		return programmedCaughPauses;
	}

	public void setProgrammedCaughPauses(Integer programmedCaughPauses) {
		this.programmedCaughPauses = programmedCaughPauses;
	}

	public Integer getNormalCaughPauses() {
		return normalCaughPauses;
	}

	public void setNormalCaughPauses(Integer normalCaughPauses) {
		this.normalCaughPauses = normalCaughPauses;
	}

	public Integer getCaughPauseDuration() {
		return caughPauseDuration;
	}

	public void setCaughPauseDuration(Integer caughPauseDuration) {
		this.caughPauseDuration = caughPauseDuration;
	}

	public Double getHmr() {
		return hmr;
	}

	public void setHmr(Double hmr) {
		this.hmr = hmr;
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

	public String getDevVersion() {
		return devVersion;
	}

	public void setDevVersion(String devVersion) {
		this.devVersion = devVersion;
	}

	@Override
	public String toString() {
		return "TherapySession [id=" + id + ", date=" + date
				+ ", sessionNo=" + sessionNo + ", sessionType=" + sessionType
				+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", frequency=" + frequency + ", pressure=" + pressure
				+ ", durationInMinutes=" + durationInMinutes
				+ ", programmedCaughPauses=" + programmedCaughPauses
				+ ", normalCaughPauses=" + normalCaughPauses
				+ ", caughPauseDuration=" + caughPauseDuration 
				+ ", hmr= "+hmr	+ "]";
	}
	
	@JsonIgnore
	public int getTherapyDayOfTheYear(){
		return this.date.getDayOfYear();
	}
	
	@JsonIgnore
	public int getDayOfTheWeek(){
		return this.date.getDayOfWeek();
	}
	
	@JsonIgnore
	public int getWeekOfYear(){
		return this.date.getWeekOfWeekyear();
	}
	
	@JsonIgnore
	public int getMonthOfTheYear(){
		return this.date.getMonthOfYear();
	}
	
	@JsonIgnore
	public Long getTherapySessionByPatientUserId() {
		return patientUser.getId();
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
	
	public Integer getIntensity() {
		return intensity;
	}

	public void setIntensity(Integer intensity) {
		this.intensity = intensity;
	}

	@Override
	public int compareTo(TherapySessionMonarch o) {
		return this.endTime.compareTo(o.getEndTime());
	}

}
