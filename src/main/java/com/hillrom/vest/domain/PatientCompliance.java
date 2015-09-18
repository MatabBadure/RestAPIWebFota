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
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.CustomLocalDateSerializer;
import com.hillrom.vest.domain.util.ISO8601LocalDateDeserializer;

@Entity
@Table(name="PATIENT_COMPLIANCE")
public class PatientCompliance {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="compliance_score")
	private Integer score;
	
	@Column
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate date;
	
	@JsonIgnore
	@ManyToOne(optional=false,targetEntity=PatientInfo.class)
	@JoinColumn(name="patient_id",referencedColumnName="id")
	private PatientInfo patient;
	
	@JsonIgnore
	@ManyToOne(optional=false,targetEntity=User.class)
	@JoinColumn(name="user_id",referencedColumnName="id")
	private User patientUser;
	
	@Column(name="hmr_run_rate")
	private Integer hmrRunRate;
	
	@Column(name="is_settings_deviated")
	private boolean isSettingsDeviated = false;
	
	@Column(name="is_hmr_compliant")
	private boolean isHmrCompliant = false;
	
	@Column(name="missed_therapy_count")
	private int missedTherapyCount;

	@Column(name="last_therapy_session_date")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate latestTherapyDate;
	
	public PatientCompliance() {
		super();
	}

	public PatientCompliance(Integer score, LocalDate date,
			PatientInfo patient, User patientUser,Integer hmrRunRate,Boolean isHMRCompliant,
			Boolean isSettingsDeviated) {
		super();
		this.score = score;
		this.date = date;
		this.patient = patient;
		this.patientUser = patientUser;
		this.hmrRunRate = hmrRunRate;
		this.isHmrCompliant = isHMRCompliant;
		this.isSettingsDeviated = isSettingsDeviated;
		this.missedTherapyCount = 0;
		this.latestTherapyDate = date;
	}

	public PatientCompliance(LocalDate date,
			PatientInfo patient, User patientUser,Integer hmrRunRate,Integer missedTherapyCount,LocalDate lastTherapySessionDate) {
		this.date = date;
		this.patient = patient;
		this.patientUser = patientUser;
		this.hmrRunRate = hmrRunRate;
		this.missedTherapyCount = missedTherapyCount;
		this.latestTherapyDate = lastTherapySessionDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public PatientInfo getPatient() {
		return patient;
	}

	public void setPatient(PatientInfo patient) {
		this.patient = patient;
	}

	public User getPatientUser() {
		return patientUser;
	}

	public void setPatientUser(User patientUser) {
		this.patientUser = patientUser;
	}

	public Integer getHmrRunRate() {
		return hmrRunRate;
	}

	public void setHmrRunRate(Integer hmrRunRate) {
		this.hmrRunRate = hmrRunRate;
	}

	public boolean isSettingsDeviated() {
		return isSettingsDeviated;
	}

	public void setSettingsDeviated(boolean isSettingsDeviated) {
		this.isSettingsDeviated = isSettingsDeviated;
	}

	public boolean isHmrCompliant() {
		return isHmrCompliant;
	}

	public void setHmrCompliant(boolean isHmrCompliant) {
		this.isHmrCompliant = isHmrCompliant;
	}

	public int getMissedTherapyCount() {
		return missedTherapyCount;
	}

	public void setMissedTherapyCount(int missedTherapyCount) {
		this.missedTherapyCount = missedTherapyCount;
	}

	public LocalDate getLatestTherapyDate() {
		return latestTherapyDate;
	}

	public void setLatestTherapyDate(LocalDate latestTherapyDate) {
		this.latestTherapyDate = latestTherapyDate;
	}

	@JsonIgnore
	public int getDayOfTheWeek(){
		return this.date.getDayOfWeek();
	}
	
	@JsonIgnore
	public int getWeekOfWeekyear(){
		return this.date.getWeekOfWeekyear();
	}
	
	@JsonIgnore
	public int getMonthOfTheYear(){
		return this.date.getMonthOfYear();
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
		PatientCompliance other = (PatientCompliance) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatientCompliance [id=" + id + ", score=" + score + ", date="
				+ date + ", hmrRunRate=" + hmrRunRate + "]";
	}

}
