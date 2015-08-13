package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * A Clinic.
 */
@Entity
@Table(name = "PATIENT_PROTOCOL_DATA")
@AssociationOverrides({
    @AssociationOverride(name = "patientProtocolDataPK.patient",
        joinColumns = @JoinColumn(name = "PATIENT_ID", referencedColumnName="id")) })
public class PatientProtocolData implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PatientProtocolDataPK patientProtocolDataPK;
	
	@Column(name = "treatments_per_day")
	private int treatmentsPerDay;
	
	@Column(name = "minutes_per_treatment")
	private int minutesPerTreatment;
	
	@Column(name = "frequencies")
	private String frequencies;
	
	@Column(name = "minimum_minutes_of_use_per_day")
	private int minimumMinutesOfUsePerDay;
	
	@Column(name = "is_active")
	private Boolean active = false;
	
	@Column(name = "type")
	private String type;

	public PatientProtocolData() {
		super();
	}

	public PatientProtocolData(PatientProtocolDataPK patientProtocolDataPK,
			int treatmentsPerDay, int minutesPerTreatment, String frequencies,
			int minimumMinutesOfUsePerDay, Boolean active, String type) {
		super();
		this.patientProtocolDataPK = patientProtocolDataPK;
		this.treatmentsPerDay = treatmentsPerDay;
		this.minutesPerTreatment = minutesPerTreatment;
		this.frequencies = frequencies;
		this.minimumMinutesOfUsePerDay = minimumMinutesOfUsePerDay;
		this.active = active;
		this.type = type;
	}

	public PatientProtocolDataPK getPatientProtocolDataPK() {
		return patientProtocolDataPK;
	}

	public void setPatientProtocolDataPK(PatientProtocolDataPK patientProtocolDataPK) {
		this.patientProtocolDataPK = patientProtocolDataPK;
	}
	
	public PatientInfo getPatient() {
		return getPatientProtocolDataPK().getPatient();
	}

	public void setPatient(PatientInfo patient) {
		getPatientProtocolDataPK().setPatient(patient);
	}

	public Long getId() {
		return getPatientProtocolDataPK().getId();
	}

	public void setId(Long id) {
		getPatientProtocolDataPK().setId(id);
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

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result
				+ ((frequencies == null) ? 0 : frequencies.hashCode());
		result = prime * result + minimumMinutesOfUsePerDay;
		result = prime * result + minutesPerTreatment;
		result = prime
				* result
				+ ((patientProtocolDataPK == null) ? 0 : patientProtocolDataPK
						.hashCode());
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
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (frequencies == null) {
			if (other.frequencies != null)
				return false;
		} else if (!frequencies.equals(other.frequencies))
			return false;
		if (minimumMinutesOfUsePerDay != other.minimumMinutesOfUsePerDay)
			return false;
		if (minutesPerTreatment != other.minutesPerTreatment)
			return false;
		if (patientProtocolDataPK == null) {
			if (other.patientProtocolDataPK != null)
				return false;
		} else if (!patientProtocolDataPK.equals(other.patientProtocolDataPK))
			return false;
		if (treatmentsPerDay != other.treatmentsPerDay)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatientProtocolData [patientProtocolDataPK="
				+ patientProtocolDataPK + ", treatmentsPerDay="
				+ treatmentsPerDay + ", minutesPerTreatment="
				+ minutesPerTreatment + ", frequencies=" + frequencies
				+ ", minimumMinutesOfUsePerDay=" + minimumMinutesOfUsePerDay
				+ ", active=" + active + "]";
	}

}
