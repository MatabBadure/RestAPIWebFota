package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Clinic.
 */
@Entity
@Audited
@Table(name = "CLINIC_USER_ASSOC")
@AssociationOverrides({
    @AssociationOverride(name = "clinicUserAssocPK.clinic",
        joinColumns = @JoinColumn(name = "CLINICS_ID", referencedColumnName="id")),
    @AssociationOverride(name = "clinicUserAssocPK.user",
        joinColumns = @JoinColumn(name = "USERS_ID", referencedColumnName="id")) })
public class ClinicUserAssoc extends AbstractAuditingEntity implements Serializable,Comparable<ClinicUserAssoc> {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ClinicUserAssocPK clinicUserAssocPK;


    public ClinicUserAssoc() {
		super();
	}

	public ClinicUserAssoc(ClinicUserAssocPK clinicUserAssocPK) {
		super();
		this.clinicUserAssocPK = clinicUserAssocPK;
	}

	public ClinicUserAssocPK getClinicUserAssocPK() {
		return clinicUserAssocPK;
	}

	public void setClinicUserAssocPK(ClinicUserAssocPK clinicUserAssocPK) {
		this.clinicUserAssocPK = clinicUserAssocPK;
	}
	
	public Clinic getClinic() {
		return getClinicUserAssocPK().getClinic();
	}

	public void setClinic(Clinic clinic) {
		getClinicUserAssocPK().setClinic(clinic);
	}

	public User getUser() {
		return getClinicUserAssocPK().getUser();
	}

	public void setUser(User user) {
		getClinicUserAssocPK().setUser(user);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClinicUserAssoc [clinicUserAssocPK=" + clinicUserAssocPK + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clinicUserAssocPK == null) ? 0 : clinicUserAssocPK.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClinicUserAssoc other = (ClinicUserAssoc) obj;
		if (clinicUserAssocPK == null) {
			if (other.clinicUserAssocPK != null)
				return false;
		} else if (!clinicUserAssocPK.equals(other.clinicUserAssocPK))
			return false;
		return true;
	}

	@Override
	public int compareTo(ClinicUserAssoc o) {
		// TODO Auto-generated method stub
		return 0;
	}



	
}
