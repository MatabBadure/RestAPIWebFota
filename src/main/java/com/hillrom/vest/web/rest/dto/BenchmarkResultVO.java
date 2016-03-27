package com.hillrom.vest.web.rest.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import org.joda.time.LocalDate;

import com.hillrom.vest.service.util.DateUtil;

public class BenchmarkResultVO{
	private Long complianceId;
	private String patientId;
	private Long userId;
	private LocalDate dob;
	private Integer age;
	private Integer clinicSize;
	private String zipcode;
	private String city;
	private String state;
	private LocalDate lastTherapySessionDate;
	private BigDecimal cumulativeCompScore;
	private BigDecimal cumulativeNonAdherenceCount;
	private BigDecimal cumulativeSettingsDeviatedCount;
	private BigDecimal cumulativeMissedTherapyDaysCount;
	private BigDecimal cumulativeHMRRunrate;
	private String clinicName;
	private String ageRangeLabel;
	
	public BenchmarkResultVO(Long complianceId, String patientId, Long userId, LocalDate dob, String zipcode,
			String city, String state, LocalDate lastTherapySessionDate, BigDecimal cumulativeCompScore,
			BigDecimal cumulativeNonAdherenceCount, BigDecimal cumulativeSettingsDeviatedCount,
			BigDecimal cumulativeMissedTherapyDaysCount,BigDecimal cumulativeHMRRunrate) {
		super();
		this.complianceId = complianceId;
		this.patientId = patientId;
		this.userId = userId;
		this.dob = dob;
		this.zipcode = zipcode;
		this.city = city;
		this.state = state;
		this.lastTherapySessionDate = dob;
		this.cumulativeCompScore = cumulativeCompScore;
		this.cumulativeNonAdherenceCount = cumulativeNonAdherenceCount ;
		this.cumulativeSettingsDeviatedCount = cumulativeSettingsDeviatedCount;
		this.cumulativeMissedTherapyDaysCount = cumulativeMissedTherapyDaysCount;
		this.cumulativeHMRRunrate = cumulativeHMRRunrate;
	}
	
	public BenchmarkResultVO(Long complianceId, String patientId, Long userId, LocalDate dob, String zipcode,
			String city, String state, LocalDate lastTherapySessionDate, BigDecimal cumulativeCompScore,
			BigDecimal cumulativeNonAdherenceCount, BigDecimal cumulativeSettingsDeviatedCount,
			BigDecimal cumulativeMissedTherapyDaysCount,BigDecimal cumulativeHMRRunrate,BigInteger clinicSize) {
		this(complianceId,patientId,userId,dob,zipcode,city,state,lastTherapySessionDate,cumulativeCompScore,
				cumulativeNonAdherenceCount,cumulativeSettingsDeviatedCount,cumulativeMissedTherapyDaysCount,cumulativeHMRRunrate);
		this.clinicSize = Objects.nonNull(clinicSize) ? clinicSize.intValue() : 0;
	}
	public BenchmarkResultVO(Long complianceId, String patientId, Long userId, LocalDate dob, String zipcode,
			String city, String state, LocalDate lastTherapySessionDate, BigDecimal cumulativeCompScore,
			BigDecimal cumulativeNonAdherenceCount, BigDecimal cumulativeSettingsDeviatedCount,
			BigDecimal cumulativeMissedTherapyDaysCount, BigDecimal cumulativeHMRRunrate, String clinicName) {
		this(complianceId,patientId,userId,dob,zipcode,city,state,lastTherapySessionDate,cumulativeCompScore,
				cumulativeNonAdherenceCount,cumulativeSettingsDeviatedCount,cumulativeMissedTherapyDaysCount,cumulativeHMRRunrate);
		this.clinicName = clinicName;
	}
	// For HCP/Clinic Admin view
	public BenchmarkResultVO(String clinicName,String zipcode,String city, String state,String ageRangeLabel, BigDecimal cumulativeCompScore,
			BigDecimal cumulativeNonAdherenceCount, BigDecimal cumulativeSettingsDeviatedCount,
			BigDecimal cumulativeMissedTherapyDaysCount, BigDecimal cumulativeHMRRunrate) {
		this(null,"",null,null,zipcode,city,state,null,cumulativeCompScore,
				cumulativeNonAdherenceCount,cumulativeSettingsDeviatedCount,cumulativeMissedTherapyDaysCount,cumulativeHMRRunrate);
		this.clinicName = clinicName;
		this.ageRangeLabel = ageRangeLabel;
	}
	
	public Long getComplianceId() {
		return complianceId;
	}
	public void setComplianceId(Long complianceId) {
		this.complianceId = complianceId;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public LocalDate getDob() {
		return dob;
	}
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}
	public Integer getAge() {
		return DateUtil.getPeriodBetweenLocalDates(dob, LocalDate.now(), null);
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public LocalDate getLastTherapySessionDate() {
		return lastTherapySessionDate;
	}
	public void setLastTherapySessionDate(LocalDate lastTherapySessionDate) {
		this.lastTherapySessionDate = lastTherapySessionDate;
	}
	public BigDecimal getCumilativeComplience() {
		return cumulativeCompScore;
	}
	public void setCumilativeComplience(BigDecimal cumilativeComplience) {
		this.cumulativeCompScore = cumilativeComplience;
	}
	public BigDecimal getCumilativeNonAdherenceCount() {
		return cumulativeNonAdherenceCount;
	}
	public void setCumilativeNonAdherenceCount(BigDecimal cumilativeNonAdherenceCount) {
		this.cumulativeNonAdherenceCount = cumilativeNonAdherenceCount;
	}
	public BigDecimal getCumilativeSettingsDeviatedCount() {
		return cumulativeSettingsDeviatedCount;
	}
	public void setCumilativeSettingsDeviatedCount(BigDecimal cumilativeSettingsDeviatedCount) {
		this.cumulativeSettingsDeviatedCount = cumilativeSettingsDeviatedCount;
	}
	public BigDecimal getCumilativeMissedTherapyDaysCount() {
		return cumulativeMissedTherapyDaysCount;
	}
	public void setCumilativeMissedTherapyDaysCount(BigDecimal cumilativeMissedTherapyDaysCount) {
		this.cumulativeMissedTherapyDaysCount = cumilativeMissedTherapyDaysCount;
	}
	public Integer getClinicSize() {
		return Objects.nonNull(clinicSize)? clinicSize : 0;
	}
	public void setClinicSize(Integer clinicSize) {
		this.clinicSize = clinicSize;
	}
	
	public BigDecimal getCumulativeHMRRunrate() {
		return cumulativeHMRRunrate;
	}

	public void setCumulativeHMRRunrate(BigDecimal cumulativeHMRRunrate) {
		this.cumulativeHMRRunrate = cumulativeHMRRunrate;
	}

	public String getClinicName() {
		return clinicName;
	}
	public void setClinicName(String clinicName) {
		this.clinicName = clinicName;
	}
	public String getAgeRangeLabel() {
		return ageRangeLabel;
	}
	public void setAgeRangeLabel(String ageRangeLabel) {
		this.ageRangeLabel = ageRangeLabel;
	}

	@Override
	public String toString() {
		return "BenchmarkVO [complianceId=" + complianceId + ", patientId=" + patientId + ", userId=" + userId
				+ ", dob=" + dob + ", age=" + age + ", zipcode=" + zipcode + ", city=" + city + ", state=" + state
				+ ", lastTherapySessionDate=" + lastTherapySessionDate + ", cumilativeComplience="
				+ cumulativeCompScore + ", cumilativeNonAdherenceCount=" + cumulativeNonAdherenceCount
				+ ", cumilativeSettingsDeviatedCount=" + cumulativeSettingsDeviatedCount
				+ ", cumilativeMissedTherapyDaysCount=" + cumulativeMissedTherapyDaysCount + "]";
	}
	
	
}
