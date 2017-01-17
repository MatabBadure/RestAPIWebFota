package com.hillrom.vest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Audited
@Entity
@Table(name="PROTOCOL_CONSTANTS_MONARCH")
public class ProtocolConstantsMonarch {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="treatments_per_day")
	private Integer treatmentsPerDay;
	
	@Column(name="min_minutes_per_treatment")
	private Integer minMinutesPerTreatment;
	
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

	public ProtocolConstantsMonarch(Integer maxFrequency, Integer minFrequency,
			Integer maxPressure, Integer minPressure, Integer treatmentsPerDay,
			Integer minDuration) {
		this.maxFrequency = maxFrequency;
		this.minFrequency = minFrequency;
		this.maxPressure = maxPressure;
		this.minPressure = minPressure;
		this.treatmentsPerDay = treatmentsPerDay;
		this.minDuration = minDuration;
		this.minMinutesPerTreatment = minDuration/treatmentsPerDay;
	}
	
	public ProtocolConstantsMonarch() {
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

	public Integer getMinDuration() {
		return this.minDuration;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProtocolConstantsMonarch [id=" + id + ", treatmentsPerDay=" + treatmentsPerDay
				+ ", minMinutesPerTreatment=" + minMinutesPerTreatment + ", minFrequency=" + minFrequency
				+ ", maxFrequency=" + maxFrequency + ", minPressure=" + minPressure + ", maxPressure=" + maxPressure
				+ ", minDuration=" + minDuration + "]";
	}
	
	
}