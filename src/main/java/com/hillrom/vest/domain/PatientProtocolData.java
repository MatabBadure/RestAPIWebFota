package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * A PATIENT_PROTOCOL_DATA.
 */
@Entity
@Table(name = "PATIENT_PROTOCOL_DATA")
public class PatientProtocolData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@OneToOne
    @JoinColumn(name = "PATIENT_ID") 
    private PatientInfo patient;
	
	@Column(name = "treatments_per_day")
	private int treatmentsPerDay;
	
	@Column(name = "minutes_per_treatment")
	private int minutesPerTreatment;
	
	@Column(name = "frequencies")
	private String frequencies;
	
	@Column(name = "minimum_minutes_of_use_per_day")
	private int minimumMinutesOfUsePerDay;
	
	public PatientProtocolData() {
		super();
	}

	public PatientProtocolData(PatientInfo patient,
			int treatmentsPerDay, int minutesPerTreatment, String frequencies,
			int minimumMinutesOfUsePerDay) {
		super();
		this.patient = patient;
		this.treatmentsPerDay = treatmentsPerDay;
		this.minutesPerTreatment = minutesPerTreatment;
		this.frequencies = frequencies;
		this.minimumMinutesOfUsePerDay = minimumMinutesOfUsePerDay;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PatientInfo getPatient() {
		return patient;
	}

	public void setPatient(PatientInfo patient) {
		this.patient = patient;
	}
	
	public int getTreatmentsPerDay() {
		return treatmentsPerDay;
	}

	public void setTreatmentsPerDay(int treatmentsPerDay) {
		this.treatmentsPerDay = treatmentsPerDay;
	}

	public int getMinutesPerTreatment() {
		return minutesPerTreatment;
	}

	public void setMinutesPerTreatment(int minutesPerTreatment) {
		this.minutesPerTreatment = minutesPerTreatment;
	}

	public String getFrequencies() {
		return frequencies;
	}

	public void setFrequencies(String frequencies) {
		this.frequencies = frequencies;
	}

	public int getMinimumMinutesOfUsePerDay() {
		return minimumMinutesOfUsePerDay;
	}

	public void setMinimumMinutesOfUsePerDay(int minimumMinutesOfUsePerDay) {
		this.minimumMinutesOfUsePerDay = minimumMinutesOfUsePerDay;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((frequencies == null) ? 0 : frequencies.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + minimumMinutesOfUsePerDay;
		result = prime * result + minutesPerTreatment;
		result = prime * result + ((patient == null) ? 0 : patient.hashCode());
		result = prime * result + treatmentsPerDay;
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
		PatientProtocolData other = (PatientProtocolData) obj;
		if (frequencies == null) {
			if (other.frequencies != null)
				return false;
		} else if (!frequencies.equals(other.frequencies))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (minimumMinutesOfUsePerDay != other.minimumMinutesOfUsePerDay)
			return false;
		if (minutesPerTreatment != other.minutesPerTreatment)
			return false;
		if (patient == null) {
			if (other.patient != null)
				return false;
		} else if (!patient.equals(other.patient))
			return false;
		if (treatmentsPerDay != other.treatmentsPerDay)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatientProtocolData [id=" + id + ", patient=" + patient
				+ ", treatmentsPerDay=" + treatmentsPerDay
				+ ", minutesPerTreatment=" + minutesPerTreatment
				+ ", frequencies=" + frequencies
				+ ", minimumMinutesOfUsePerDay=" + minimumMinutesOfUsePerDay
				+ "]";
	}

}
