package com.hillrom.vest.domain;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;

/**
 * A Clinic.
 */
@Entity
@Table(name = "CLINIC_PATIENTS_ASSOC")
@SQLDelete(sql="UPDATE CLINIC_PATIENTS_ASSOC SET is_deleted = 1 WHERE id = ?")
public class ClinicPatientAssoc implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PATIENT_ID") 
    private PatientInfo patient;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CLINIC_ID") 
    private Clinic clinic;
    
    @Column(name="mrn_id", nullable = false)
    private String mrnId;
    
    @Column(name="is_deleted", nullable = false)
    private boolean deleted = false;

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

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public String getMrnId() {
		return mrnId;
	}

	public void setMrnId(String mrnId) {
		this.mrnId = mrnId;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClinicPatientAssoc clinicPatientAssoc = (ClinicPatientAssoc) o;

        if ( ! Objects.equals(id, clinicPatientAssoc.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	@Override
	public String toString() {
		return "ClinicPatientAssoc [id=" + id + ", patient=" + patient
				+ ", clinic=" + clinic + ", mrnId=" + mrnId + ", deleted="
				+ deleted + "]";
	}

}
