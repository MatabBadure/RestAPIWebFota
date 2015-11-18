package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * A Clinic.
 */
@Entity
@Audited
@Table(name = "ENTITY_USER_ASSOC")
@AssociationOverrides({
    @AssociationOverride(name = "clinicPatientAssocPK.clinic",
        joinColumns = @JoinColumn(name = "ENTITY_ID", referencedColumnName="id")),
    @AssociationOverride(name = "clinicPatientAssocPK.user",
        joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName="id")) })
public class EntityUserAssoc implements Serializable {

	@EmbeddedId
	private EntityUserAssocPK entityUserAssocPK;
    
    @Column(name="user_role")
    String userRole;
    
	public EntityUserAssoc() {
		super();
	}

	public EntityUserAssoc(EntityUserAssocPK entityUserAssocPK, String userRole) {
		super();
		this.entityUserAssocPK = entityUserAssocPK;
		this.userRole = userRole;
	}
	public EntityUserAssoc(User user, Clinic clinic, String userRole) {
		super();
		this.entityUserAssocPK = new EntityUserAssocPK(user, clinic);
		this.userRole = userRole;
	}

	public Clinic getClinic() {
		return getEntityUserAssocPK().getClinic();
	}

	public void setClinic(Clinic clinic) {
		getEntityUserAssocPK().setClinic(clinic);
	}

	public User getUser() {
		return getEntityUserAssocPK().getUser();
	}

	public void setUser(User user) {
		getEntityUserAssocPK().setUser(user);
	}



	public EntityUserAssocPK getEntityUserAssocPK() {
		return entityUserAssocPK;
	}

	public void setEntityUserAssocPK(EntityUserAssocPK entityUserAssocPK) {
		this.entityUserAssocPK = entityUserAssocPK;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((entityUserAssocPK == null) ? 0 : entityUserAssocPK
						.hashCode());
		result = prime * result + ((userRole == null) ? 0 : userRole.hashCode());
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
		EntityUserAssoc other = (EntityUserAssoc) obj;
		if (entityUserAssocPK == null) {
			if (other.entityUserAssocPK != null)
				return false;
		} else if (!entityUserAssocPK.equals(other.entityUserAssocPK))
			return false;
		if (userRole == null) {
			if (other.userRole != null)
				return false;
		} else if (!userRole.equals(other.userRole))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClinicPatientAssoc [clinicPatientAssocPK="
				+ entityUserAssocPK + ", mrnId=" + userRole + "]";
	}

}
