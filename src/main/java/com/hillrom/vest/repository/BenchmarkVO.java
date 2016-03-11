package com.hillrom.vest.repository;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class BenchmarkVO{
	private Long complianceId;
	private String patientId;
	private Long userId;
	private LocalDate dob;
	private Integer age;
	private String zipcode;
	private String city;
	private String state;
	private LocalDate lastTherapySessionDate;
	private BigDecimal cumulativeCompScore;
	private BigDecimal cumulativeNonAdherenceCount;
	private BigDecimal cumulativeSettingsDeviatedCount;
	private BigDecimal cumulativeMissedTherapyDaysCount;

	
	public BenchmarkVO(Long complianceId, String patientId, Long userId, LocalDate dob, Integer age, String zipcode,
			String city, String state, LocalDate lastTherapySessionDate, BigDecimal cumulativeCompScore,
			BigDecimal cumulativeNonAdherenceCount, BigDecimal cumulativeSettingsDeviatedCount,
			BigDecimal cumulativeMissedTherapyDaysCount) {
		super();
		this.complianceId = complianceId;
		this.patientId = patientId;
		this.userId = userId;
		this.dob = dob;
		this.age = age;
		this.zipcode = zipcode;
		this.city = city;
		this.state = state;
		this.lastTherapySessionDate = dob;
		this.cumulativeCompScore = cumulativeCompScore;
		this.cumulativeNonAdherenceCount = cumulativeNonAdherenceCount ;
		this.cumulativeSettingsDeviatedCount = cumulativeSettingsDeviatedCount;
		this.cumulativeMissedTherapyDaysCount = cumulativeMissedTherapyDaysCount;
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
		return age;
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
