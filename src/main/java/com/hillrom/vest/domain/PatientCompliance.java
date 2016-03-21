package com.hillrom.vest.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.CustomLocalDateSerializer;
import com.hillrom.vest.domain.util.ISO8601LocalDateDeserializer;
import com.hillrom.vest.web.rest.dto.BenchmarkResultVO;

@SqlResultSetMappings({
@SqlResultSetMapping(name = "avgBenchmarkResultSetMapping", classes = @ConstructorResult(targetClass = BenchmarkResultVO.class, columns = {
		@ColumnResult(name = "complainceId", type = Long.class),
		@ColumnResult(name = "patId",type = String.class),
		@ColumnResult(name = "userId",type = Long.class),
		@ColumnResult(name = "dob", type = org.jadira.usertype.dateandtime.joda.PersistentLocalDate.class),
		@ColumnResult(name = "zipcode",type = String.class),
		@ColumnResult(name = "city",type = String.class),
		@ColumnResult(name = "state",type = String.class),
		@ColumnResult(name = "lastTherapySessionDate", type = org.jadira.usertype.dateandtime.joda.PersistentLocalDate.class),
		@ColumnResult(name = "avgCompScore", type = BigDecimal.class),
		@ColumnResult(name = "avgNonAdherenceCount", type = BigDecimal.class),
		@ColumnResult(name = "avgSettingsDeviatedCount", type = BigDecimal.class),
		@ColumnResult(name = "avgMissedTherapyDaysCount", type = BigDecimal.class),
		@ColumnResult(name = "avgHMRRunrate", type = BigDecimal.class)})),
@SqlResultSetMapping(name = "avgBenchmarkByClinicSizeResultSetMapping", classes = @ConstructorResult(targetClass = BenchmarkResultVO.class, columns = {
		@ColumnResult(name = "complainceId", type = Long.class),
		@ColumnResult(name = "patId",type = String.class),
		@ColumnResult(name = "userId",type = Long.class),
		@ColumnResult(name = "dob", type = org.jadira.usertype.dateandtime.joda.PersistentLocalDate.class),
		@ColumnResult(name = "zipcode",type = String.class),
		@ColumnResult(name = "city",type = String.class),
		@ColumnResult(name = "state",type = String.class),
		@ColumnResult(name = "lastTherapySessionDate", type = org.jadira.usertype.dateandtime.joda.PersistentLocalDate.class),
		@ColumnResult(name = "avgCompScore", type = BigDecimal.class),
		@ColumnResult(name = "avgNonAdherenceCount", type = BigDecimal.class),
		@ColumnResult(name = "avgSettingsDeviatedCount", type = BigDecimal.class),
		@ColumnResult(name = "avgMissedTherapyDaysCount", type = BigDecimal.class),
		@ColumnResult(name = "avgHMRRunrate", type = BigDecimal.class),
		@ColumnResult(name = "clinicsize", type = BigInteger.class)}))})
@SqlResultSetMapping(name = "avgBenchmarkForClinicByAgeGroupResultSetMapping", classes = @ConstructorResult(targetClass = BenchmarkResultVO.class, columns = {
		@ColumnResult(name = "complainceId", type = Long.class),
		@ColumnResult(name = "patId",type = String.class),
		@ColumnResult(name = "userId",type = Long.class),
		@ColumnResult(name = "dob", type = org.jadira.usertype.dateandtime.joda.PersistentLocalDate.class),
		@ColumnResult(name = "zipcode",type = String.class),
		@ColumnResult(name = "city",type = String.class),
		@ColumnResult(name = "state",type = String.class),
		@ColumnResult(name = "lastTherapySessionDate", type = org.jadira.usertype.dateandtime.joda.PersistentLocalDate.class),
		@ColumnResult(name = "avgCompScore", type = BigDecimal.class),
		@ColumnResult(name = "avgNonAdherenceCount", type = BigDecimal.class),
		@ColumnResult(name = "avgSettingsDeviatedCount", type = BigDecimal.class),
		@ColumnResult(name = "avgMissedTherapyDaysCount", type = BigDecimal.class),
		@ColumnResult(name = "avgHMRRunrate", type = BigDecimal.class),
		@ColumnResult(name = "clinicName", type = String.class)}))
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
	
	@ManyToOne(optional=false,targetEntity=PatientInfo.class)
	@JoinColumn(name="patient_id",referencedColumnName="id")
	private PatientInfo patient;
	
	@ManyToOne(optional=false,targetEntity=User.class)
	@JoinColumn(name="user_id",referencedColumnName="id")
	private User patientUser;
	
	@Column(name="hmr_run_rate")
	private Integer hmrRunRate = 0;
	
	@Column(name="is_settings_deviated")
	private boolean isSettingsDeviated = false;
	
	@Column(name="is_hmr_compliant")
	private boolean isHmrCompliant = true;
	
	@Column(name="missed_therapy_count")
	private int missedTherapyCount;

	@Column(name="last_therapy_session_date")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate latestTherapyDate;
	
	private Double hmr = 0.0d;

	@Column(name="settings_deviated_days_count")
	private int settingsDeviatedDaysCount  = 0;
	
	@Column(name="global_missed_therapy_days_count")
	private int globalMissedTherapyCounter;
	
	@Column(name="global_hmr_non_adherence_count")
	private int globalHMRNonAdherenceCounter;
	
	@Column(name="global_settings_deviated_count")
	private int globalSettingsDeviationCounter;
	
	public PatientCompliance() {
		super();
	}

	public PatientCompliance(Integer score, LocalDate date,
			PatientInfo patient, User patientUser,Integer hmrRunRate,Boolean isHMRCompliant,
			Boolean isSettingsDeviated,double hmr) {
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
		this.hmr = hmr;
	}

	public PatientCompliance(LocalDate date,
			PatientInfo patient, User patientUser,Integer hmrRunRate,Integer missedTherapyCount,
			LocalDate lastTherapySessionDate,double hmr) {
		this.date = date;
		this.patient = patient;
		this.patientUser = patientUser;
		this.hmrRunRate = hmrRunRate;
		this.missedTherapyCount = missedTherapyCount;
		this.latestTherapyDate = lastTherapySessionDate;
		this.hmr = hmr;
	}

	public PatientCompliance(Integer score, LocalDate date,
			PatientInfo patient, User patientUser,Integer hmrRunRate,Boolean isHMRCompliant,
			Boolean isSettingsDeviated,Integer missedTherapyCount,
			LocalDate lastTherapySessionDate,double hmr) {
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
		this.hmr = hmr;
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

	public Double getHmr() {
		return hmr;
	}

	public void setHmr(Double hmr) {
		this.hmr = hmr;
	}

	public int getSettingsDeviatedDaysCount() {
		return settingsDeviatedDaysCount;
	}

	public void setSettingsDeviatedDaysCount(int settingsDeviatedDaysCount) {
		this.settingsDeviatedDaysCount = settingsDeviatedDaysCount;
	}

	public int getGlobalMissedTherapyCounter() {
		return globalMissedTherapyCounter;
	}

	public void setGlobalMissedTherapyCounter(int globalMissedTherapyCounter) {
		this.globalMissedTherapyCounter = globalMissedTherapyCounter;
	}

	public int getGlobalHMRNonAdherenceCounter() {
		return globalHMRNonAdherenceCounter;
	}

	public void setGlobalHMRNonAdherenceCounter(int globalHMRNonAdherenceCounter) {
		this.globalHMRNonAdherenceCounter = globalHMRNonAdherenceCounter;
	}

	public int getGlobalSettingsDeviationCounter() {
		return globalSettingsDeviationCounter;
	}

	public void setGlobalSettingsDeviationCounter(int globalSettingsDeviationCounter) {
		this.globalSettingsDeviationCounter = globalSettingsDeviationCounter;
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
				+ date + ", hmrRunRate=" + hmrRunRate
				+ ", isSettingsDeviated=" + isSettingsDeviated
				+ ", isHmrCompliant=" + isHmrCompliant
				+ ", missedTherapyCount=" + missedTherapyCount
				+ ", latestTherapyDate=" + latestTherapyDate + ", hmr=" + hmr
				+ "]";
	}

	
}
