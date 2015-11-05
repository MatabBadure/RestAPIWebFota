package com.hillrom.vest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name="PROTOCOL_CONSTANTS")
public class ProtocolConstants {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="treatments_per_day")
	private Integer treatmentsPerDay;
	
	@Column(name="min_minutes_per_treatment")
	private Integer minMinutesPerTreatment;
	
	@Column(name="max_minutes_per_treatment")
	private Integer maxMinutesPerTreatment;
	
	@Column(name="min_frequency")
	private Integer minFrequency;
	
	@Column(name="max_frequency")
	private Integer maxFrequency;
	
	@Column(name="min_pressure")
	private Integer minPressure;
	
	@Column(name="max_pressure")
	private Integer maxPressure;

	@Transient
	private Integer minDuration;

	@Transient
	private Integer maxDuration;
	
	public ProtocolConstants(Integer maxFrequency, Integer minFrequency,
			Integer maxPressure, Integer minPressure, Integer treatmentsPerDay,
			Integer minDuration, Integer maxDuration) {
		this.maxFrequency = maxFrequency;
		this.minFrequency = minFrequency;
		this.maxPressure = maxPressure;
		this.minPressure = minPressure;
		this.treatmentsPerDay = treatmentsPerDay;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		this.minMinutesPerTreatment = minDuration/treatmentsPerDay;
		this.maxMinutesPerTreatment = maxDuration/treatmentsPerDay;
	}
	
	public ProtocolConstants() {
		super();
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getTreatmentsPerDay() {
		return treatmentsPerDay;
	}

	public void setTreatmentsPerDay(Integer treatmentsPerDay) {
		this.treatmentsPerDay = treatmentsPerDay;
	}

	public Integer getMinMinutesPerTreatment() {
		return minMinutesPerTreatment;
	}

	public void setMinMinutesPerTreatment(Integer minMinutesPerTreatment) {
		this.minMinutesPerTreatment = minMinutesPerTreatment;
	}

	public Integer getMaxMinutesPerTreatment() {
		return maxMinutesPerTreatment;
	}

	public void setMaxMinutesPerTreatment(Integer maxMinutesPerTreatment) {
		this.maxMinutesPerTreatment = maxMinutesPerTreatment;
	}

	public Integer getMinFrequency() {
		return minFrequency;
	}

	public void setMinFrequency(Integer minFrequency) {
		this.minFrequency = minFrequency;
	}

	public Integer getMaxFrequency() {
		return maxFrequency;
	}

	public void setMaxFrequency(Integer maxFrequency) {
		this.maxFrequency = maxFrequency;
	}

	public Integer getMinPressure() {
		return minPressure;
	}

	public void setMinPressure(Integer minPressure) {
		this.minPressure = minPressure;
	}

	public Integer getMaxPressure() {
		return maxPressure;
	}

	public void setMaxPressure(Integer maxPressure) {
		this.maxPressure = maxPressure;
	}

	public void setMinDuration(Integer minDuration) {
		this.minDuration = minDuration;
	}

	public void setMaxDuration(Integer maxDuration) {
		this.maxDuration = maxDuration;
	}

	public Integer getMinDuration() {
		return this.minDuration;
	}

	public Integer getMaxDuration() {
		return this.maxDuration;
	}
}