package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.DecimalNumberSerializer;

@Entity
@Table(name = "PATIENT_VEST_DEVICE_HISTORY_MONARCH")
@EntityListeners(AuditingEntityListener.class)
@AssociationOverrides({
    @AssociationOverride(name = "patientVestDevicePK.patient",
        joinColumns = @JoinColumn(name = "PATIENT_ID", referencedColumnName="id")) })
public class PatientVestDeviceHistoryMonarch implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private PatientVestDevicePK patientVestDevicePK;

	@Column(name = "bluetooth_id")
	private String bluetoothId;
	
	@Column(name="hub_id")
	private String hubId;
	
	@Column(name="is_active")
	private Boolean active = false;
	
	@CreatedBy
    @NotNull
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    private String createdBy;

    @CreatedDate
    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "created_date", nullable = false)
    private DateTime createdDate = DateTime.now();

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "last_modified_date")
    private DateTime lastModifiedDate = DateTime.now();
    
    @Column(name="hmr")
	private Double hmr = 0d; // default value for HMR

	public PatientVestDeviceHistoryMonarch() {
		super();
	}

	public PatientVestDeviceHistoryMonarch(PatientVestDevicePK patientVestDevicePK,
			String bluetoothId, String hubId, Boolean active) {
		super();
		this.patientVestDevicePK = patientVestDevicePK;
		this.bluetoothId = bluetoothId;
		this.hubId = hubId;
		this.active = active;
	}

	public PatientVestDevicePK getPatientVestDevicePK() {
		return patientVestDevicePK;
	}

	public void setPatientVestDevicePK(PatientVestDevicePK patientVestDevicePK) {
		this.patientVestDevicePK = patientVestDevicePK;
	}

	public PatientInfo getPatient() {
		return getPatientVestDevicePK().getPatient();
	}

	public void setPatient(PatientInfo patient) {
		getPatientVestDevicePK().setPatient(patient);
	}

	public String getSerialNumber() {
		return getPatientVestDevicePK().getSerialNumber();
	}

	public void setSerialNumber(String serialNumber) {
		getPatientVestDevicePK().setSerialNumber(serialNumber);
	}
	
	public String getBluetoothId() {
		return bluetoothId;
	}

	public void setBluetoothId(String bluetoothId) {
		this.bluetoothId = bluetoothId;
	}

	public String getHubId() {
		return hubId;
	}

	public void setHubId(String hubId) {
		this.hubId = hubId;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public DateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public DateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(DateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	@JsonIgnore
	public Double getHmr() {
		return hmr;
	}

	public void setHmr(Double hmr) {
		this.hmr = hmr;
	}

	// This is used for sending hmr in Minutes
	@JsonProperty(value="hmr")
	@JsonSerialize(using=DecimalNumberSerializer.class)
	public Double getHmrInMinutes(){
		return hmr/(60);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result
				+ ((bluetoothId == null) ? 0 : bluetoothId.hashCode());
		result = prime * result + ((hubId == null) ? 0 : hubId.hashCode());
		result = prime
				* result
				+ ((patientVestDevicePK == null) ? 0 : patientVestDevicePK
						.hashCode());
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
		PatientVestDeviceHistoryMonarch other = (PatientVestDeviceHistoryMonarch) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (bluetoothId == null) {
			if (other.bluetoothId != null)
				return false;
		} else if (!bluetoothId.equals(other.bluetoothId))
			return false;
		if (hubId == null) {
			if (other.hubId != null)
				return false;
		} else if (!hubId.equals(other.hubId))
			return false;
		if (patientVestDevicePK == null) {
			if (other.patientVestDevicePK != null)
				return false;
		} else if (!patientVestDevicePK.equals(other.patientVestDevicePK))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PatientVestDeviceHistoryMonarch [patientVestDevicePK=" + patientVestDevicePK + ", bluetoothId="
				+ bluetoothId + ", hubId=" + hubId + ", active=" + active + ", createdBy=" + createdBy
				+ ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedDate="
				+ lastModifiedDate + ", hmr=" + hmr + "]";
	}
}