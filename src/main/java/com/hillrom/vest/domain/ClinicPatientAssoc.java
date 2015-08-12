package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * A Clinic.
 */
@Entity
@Table(name = "CLINIC_PATIENT_ASSOC")
@AssociationOverrides({
    @AssociationOverride(name = "clinicPatientAssocPK.clinic",
        joinColumns = @JoinColumn(name = "CLINIC_ID", referencedColumnName="id")),
    @AssociationOverride(name = "clinicPatientAssocPK.patient",
        joinColumns = @JoinColumn(name = "PATIENT_ID", referencedColumnName="id")) })
public class ClinicPatientAssoc implements Serializable {

	@EmbeddedId
	private ClinicPatientAssocPK clinicPatientAssocPK;
    
    @Column(name="mrn_id", nullable = false)
    private String mrnId;
    
    @Column(name="notes")
    private String notes;
    
	public ClinicPatientAssoc() {
		super();
	}

	public ClinicPatientAssoc(ClinicPatientAssocPK clinicPatientAssocPK,
			String mrnId, String notes) {
		super();
		this.clinicPatientAssocPK = clinicPatientAssocPK;
		this.mrnId = mrnId;
		this.notes = notes;
	}

	public ClinicPatientAssocPK getClinicPatientAssocPK() {
		return clinicPatientAssocPK;
	}

	public void setClinicPatientAssocPK(ClinicPatientAssocPK clinicPatientAssocPK) {
		this.clinicPatientAssocPK = clinicPatientAssocPK;
	}

	public PatientInfo getPatient() {
		return getClinicPatientAssocPK().getPatient();
	}

	public void setPatient(PatientInfo patient) {
		getClinicPatientAssocPK().setPatient(patient);
	}

	public Clinic getClinic() {
		return getClinicPatientAssocPK().getClinic();
	}

	public void setClinic(Clinic clinic) {
		getClinicPatientAssocPK().setClinic(clinic);
	}

	public String getMrnId() {
		return mrnId;
	}

	public void setMrnId(String mrnId) {
		this.mrnId = mrnId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((clinicPatientAssocPK == null) ? 0 : clinicPatientAssocPK
						.hashCode());
		result = prime * result + ((mrnId == null) ? 0 : mrnId.hashCode());
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
		ClinicPatientAssoc other = (ClinicPatientAssoc) obj;
		if (clinicPatientAssocPK == null) {
			if (other.clinicPatientAssocPK != null)
				return false;
		} else if (!clinicPatientAssocPK.equals(other.clinicPatientAssocPK))
			return false;
		if (mrnId == null) {
			if (other.mrnId != null)
				return false;
		} else if (!mrnId.equals(other.mrnId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClinicPatientAssoc [clinicPatientAssocPK="
				+ clinicPatientAssocPK + ", mrnId=" + mrnId + "]";
	}

}
