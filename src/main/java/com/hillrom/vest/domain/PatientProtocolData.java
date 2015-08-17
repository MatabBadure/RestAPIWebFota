package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.SQLDelete;

/**
 * A PATIENT_PROTOCOL_DATA.
 */
@Entity
@Table(name = "PATIENT_PROTOCOL_DATA")
@SQLDelete(sql="UPDATE PATIENT_PROTOCOL  SET is_deleted = 1 where id = ?")
public class PatientProtocolData extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "type")
	@Size(min=6,max=7)
	private String type;
	
	@ManyToOne
    @JoinColumn(name = "PATIENT_ID") 
    private PatientInfo patient;
	
	@ManyToOne
    @JoinColumn(name = "USER_ID")
	private User patientUser;
	
	@Column(name = "treatments_per_day")
	private int treatmentsPerDay;
	
	@Column(name = "min_minutes_per_treatment")
	private int minMinutesPerTreatment;
	
	@Column(name = "max_minutes_per_treatment")
	private int maxMinutesPerTreatment;
	
	@Column(name = "treatment_label")
	private String treatmentLabel;
	
	@Column(name = "min_frequency")
	private Integer minFrequency;
	
	@Column(name = "max_frequency")
	private Integer maxFrequency;
	
	@Column(name = "min_pressure")
	private Integer minPressure;
	
	@Column(name = "max_pressure")
	private Integer maxPressure;
	
	@Column(name = "is_deleted")
	private boolean deleted = false;
	
    @Column(name="protocol_key")
    private String protocolKey;
 
	public PatientProtocolData() {
		super();
	}

	public PatientProtocolData(String type, PatientInfo patient,
			User patientUser, int treatmentsPerDay, int minMinutesPerTreatment,
			int maxMinutesPerTreatment, String treatmentLabel,
			Integer minFrequency, Integer maxFrequency, Integer minPressure,
			Integer maxPressure) {
		super();
		this.type = type;
		this.patient = patient;
		this.patientUser = patientUser;
		this.treatmentsPerDay = treatmentsPerDay;
		this.minMinutesPerTreatment = minMinutesPerTreatment;
		this.maxMinutesPerTreatment = maxMinutesPerTreatment;
		this.treatmentLabel = treatmentLabel;
		this.minFrequency = minFrequency;
		this.maxFrequency = maxFrequency;
		this.minPressure = minPressure;
		this.maxPressure = maxPressure;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMaxMinutesPerTreatment() {
		return maxMinutesPerTreatment;
	}


	public void setMaxMinutesPerTreatment(int maxMinutesPerTreatment) {
		this.maxMinutesPerTreatment = maxMinutesPerTreatment;
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

	public int getTreatmentsPerDay() {
		return treatmentsPerDay;
	}

	public void setTreatmentsPerDay(int treatmentsPerDay) {
		this.treatmentsPerDay = treatmentsPerDay;
	}

	public int getMinMinutesPerTreatment() {
		return minMinutesPerTreatment;
	}

	public void setMinMinutesPerTreatment(int minMinutesPerTreatment) {
		this.minMinutesPerTreatment = minMinutesPerTreatment;
	}

	public String getTreatmentLabel() {
		return treatmentLabel;
	}

	public void setTreatmentLabel(String treatmentLabel) {
		this.treatmentLabel = treatmentLabel;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getProtocolKey() {
		return protocolKey;
	}

	public void setProtocolKey(String protocolKey) {
		this.protocolKey = protocolKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + minMinutesPerTreatment;
		result = prime * result
				+ ((patientUser == null) ? 0 : patientUser.hashCode());
		result = prime * result
				+ ((protocolKey == null) ? 0 : protocolKey.hashCode());
		result = prime * result
				+ ((treatmentLabel == null) ? 0 : treatmentLabel.hashCode());
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
		if (minMinutesPerTreatment != other.minMinutesPerTreatment)
			return false;
		if (patientUser == null) {
			if (other.patientUser != null)
				return false;
		} else if (!patientUser.equals(other.patientUser))
			return false;
		if (protocolKey == null) {
			if (other.protocolKey != null)
				return false;
		} else if (!protocolKey.equals(other.protocolKey))
			return false;
		if (treatmentLabel == null) {
			if (other.treatmentLabel != null)
				return false;
		} else if (!treatmentLabel.equals(other.treatmentLabel))
			return false;
		if (treatmentsPerDay != other.treatmentsPerDay)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatientProtocolData [id=" + id + ", treatmentsPerDay="
				+ treatmentsPerDay + ", minMinutesPerTreatment="
				+ minMinutesPerTreatment + ", treatmentLabel=" + treatmentLabel
				+ ", minFrequency=" + minFrequency + ", maxFrequency="
				+ maxFrequency + ", minPressure=" + minPressure
				+ ", maxPressure=" + maxPressure + ", deleted=" + deleted
				+ ", protocolKey=" + protocolKey + "]";
	}

	
}
