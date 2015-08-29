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
@Table(name="PATIENT_VEST_THERAPY_DATA")
public class TherapySession {
	
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
	
	@Column(name="duration_in_minutes")
	private Long durationInMinutes;
	
	@Column(name="programmed_caugh_pauses")
	private Integer programmedCaughPauses;
	
	@Column(name="normal_caugh_pauses")
	private Integer normalCaughPauses;
	
	@Column(name="caugh_pause_duration")
	private Integer caughPauseDuration;
	
	@Column(name="hmr")
	private Double hmr;

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

	public Long getDurationInMinutes() {
		return durationInMinutes;
	}

	public void setDurationInMinutes(Long durationInSeconds) {
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

	@Override
	public String toString() {
		return "TherapySession [id=" + id + ", date=" + date
				+ ", sessionNo=" + sessionNo + ", sessionType=" + sessionType
				+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", frequency=" + frequency + ", pressure=" + pressure
				+ ", durationInSeconds=" + durationInMinutes
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
	public long getDurationLongValue(){
		return this.durationInMinutes.longValue();
	}
	
	@JsonIgnore
	public int getDayOfTheWeek(){
		return this.date.getDayOfWeek();
	}
	
	@JsonIgnore
	public int getWeekOfYear(){
		return this.date.getWeekyear();
	}
	
	@JsonIgnore
	public int getMonthOfTheYear(){
		return this.date.getMonthOfYear();
	}
}
