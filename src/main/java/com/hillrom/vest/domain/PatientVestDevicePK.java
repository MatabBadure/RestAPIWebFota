package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class PatientVestDevicePK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PATIENT_ID", referencedColumnName="id") 
	private PatientInfo patient;
	
	@Column(name = "serial_number")
	private String serialNumber;
	
	public PatientVestDevicePK() {
		super();
	}

	public PatientVestDevicePK(PatientInfo patient, String serialNumber) {
		super();
		this.patient = patient;
		this.serialNumber = serialNumber;
	}

	public PatientInfo getPatient() {
		return patient;
	}

	public void setPatient(PatientInfo patient) {
		this.patient = patient;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((patient == null) ? 0 : patient.hashCode());
		result = prime * result
				+ ((serialNumber == null) ? 0 : serialNumber.hashCode());
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
		PatientVestDevicePK other = (PatientVestDevicePK) obj;
		if (patient == null) {
			if (other.patient != null)
				return false;
		} else if (!patient.equals(other.patient))
			return false;
		if (serialNumber == null) {
			if (other.serialNumber != null)
				return false;
		} else if (!serialNumber.equals(other.serialNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatientVestDevicePK [patient=" + patient + ", serialNumber="
				+ serialNumber + "]";
	}
}
