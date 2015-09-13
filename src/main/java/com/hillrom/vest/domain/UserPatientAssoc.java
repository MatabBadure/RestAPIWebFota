package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Clinic.
 */
@Entity
@Table(name = "USER_PATIENT_ASSOC")
@AssociationOverrides({
    @AssociationOverride(name = "userPatientAssocPK.user",
        joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName="id")),
    @AssociationOverride(name = "userPatientAssocPK.patient",
        joinColumns = @JoinColumn(name = "PATIENT_ID", referencedColumnName="id")) })
public class UserPatientAssoc implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private UserPatientAssocPK userPatientAssocPK;
    
    @Column(name="user_role", nullable = false)
    private String userRole;
    
    @Column(name="relation_label", nullable = false)
    private String relationshipLabel;

    public UserPatientAssoc() {
		super();
	}

	public UserPatientAssoc(UserPatientAssocPK userPatientAssocPK,
			String userRole, String relationshipLabel) {
		super();
		this.userPatientAssocPK = userPatientAssocPK;
		this.userRole = userRole;
		this.relationshipLabel = relationshipLabel;
	}

	public UserPatientAssocPK getUserPatientAssocPK() {
		return userPatientAssocPK;
	}

	public void setUserPatientAssocPK(UserPatientAssocPK userPatientAssocPK) {
		this.userPatientAssocPK = userPatientAssocPK;
	}
	
	public PatientInfo getPatient() {
		return getUserPatientAssocPK().getPatient();
	}

	public void setPatient(PatientInfo patient) {
		getUserPatientAssocPK().setPatient(patient);
	}

	public User getUser() {
		return getUserPatientAssocPK().getUser();
	}

	public void setUser(User user) {
		getUserPatientAssocPK().setUser(user);
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getRelationshipLabel() {
		return relationshipLabel;
	}

	public void setRelationshipLabel(String relationshipLabel) {
		this.relationshipLabel = relationshipLabel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((relationshipLabel == null) ? 0 : relationshipLabel
						.hashCode());
		result = prime
				* result
				+ ((userPatientAssocPK == null) ? 0 : userPatientAssocPK
						.hashCode());
		result = prime * result
				+ ((userRole == null) ? 0 : userRole.hashCode());
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
		UserPatientAssoc other = (UserPatientAssoc) obj;
		if (relationshipLabel == null) {
			if (other.relationshipLabel != null)
				return false;
		} else if (!relationshipLabel.equals(other.relationshipLabel))
			return false;
		if (userPatientAssocPK == null) {
			if (other.userPatientAssocPK != null)
				return false;
		} else if (!userPatientAssocPK.equals(other.userPatientAssocPK))
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
		return "UserPatientAssoc ["+"userRole=" + userRole + ", relationshipLabel="
				+ relationshipLabel + "]";
	}

}
