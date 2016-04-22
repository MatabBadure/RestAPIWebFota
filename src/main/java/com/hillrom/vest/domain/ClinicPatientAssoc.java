package com.hillrom.vest.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

import com.hillrom.vest.web.rest.dto.BenchmarkResultVO;
import com.hillrom.vest.web.rest.dto.ClinicDiseaseStatisticsResultVO;

/**
 * A Clinic.
 */
@Entity
@Audited
@Table(name = "CLINIC_PATIENT_ASSOC")
@AssociationOverrides({
    @AssociationOverride(name = "clinicPatientAssocPK.clinic",
        joinColumns = @JoinColumn(name = "CLINIC_ID", referencedColumnName="id")),
    @AssociationOverride(name = "clinicPatientAssocPK.patient",
        joinColumns = @JoinColumn(name = "PATIENT_ID", referencedColumnName="id")) })
@SqlResultSetMappings({
@SqlResultSetMapping(name = "clinicAndDiseaseStatsByAgeorClinicSize", classes = @ConstructorResult(targetClass = ClinicDiseaseStatisticsResultVO.class, columns = {
		@ColumnResult(name = "totalPatients", type = BigInteger.class),
		@ColumnResult(name = "ageRangeLabel",type = String.class),
		@ColumnResult(name = "clinicSizeRangeLabel",type = String.class),
		@ColumnResult(name = "state",type = String.class),
		@ColumnResult(name = "city",type = String.class)})),
@SqlResultSetMapping(name = "clinicAndDiseaseStatsByState", classes = @ConstructorResult(targetClass = ClinicDiseaseStatisticsResultVO.class, columns = {
		@ColumnResult(name = "totalPatients", type = BigInteger.class),
		@ColumnResult(name = "state",type = String.class),
		@ColumnResult(name = "city",type = String.class)}))})
public class ClinicPatientAssoc extends AbstractAuditingEntity implements Serializable {

	@EmbeddedId
	private ClinicPatientAssocPK clinicPatientAssocPK;
    
    @Column(name="mrn_id")
    private String mrnId;
    
    @Column(name="notes")
    private String notes;
    
    @Column(name="is_active")
    private Boolean isActive = true;
    
    @Column(name = "expired")
    private boolean expired = false;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "expiration_date", nullable = true)
    private DateTime expirationDate = null;
    
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

	public Boolean getActive() {
		return isActive;
	}

	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	public Boolean getExpired() {
		return Objects.nonNull(expired)? expired : false;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}
	
	public DateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(DateTime expirationDate) {
		this.expirationDate = expirationDate;
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
