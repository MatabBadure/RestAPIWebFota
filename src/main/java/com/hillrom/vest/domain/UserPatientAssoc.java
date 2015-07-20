package com.hillrom.vest.domain;


import java.io.Serializable;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;

/**
 * A Clinic.
 */
@Entity
@Table(name = "USER_PATIENT_ASSOC")
@SQLDelete(sql="UPDATE USER_PATIENT_ASSOC SET is_deleted = 1 WHERE id = ?")
public class UserPatientAssoc implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PATIENT_ID") 
    private PatientInfo patient;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID") 
    private User user;
    
    @Column(name="user_role", nullable = false)
    private String userRole;
    
    @Column(name="relation_label", nullable = false)
    private String relationshipLabel;

    public UserPatientAssoc() {
		super();
	}

	public UserPatientAssoc(PatientInfo patient, User user,
			String userRole, String relationshipLabel) {
		super();
		this.patient = patient;
		this.user = user;
		this.userRole = userRole;
		this.relationshipLabel = relationshipLabel;
	}

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserPatientAssoc clinicPatientAssoc = (UserPatientAssoc) o;

        if ( ! Objects.equals(id, clinicPatientAssoc.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	@Override
	public String toString() {
		return "UserPatientAssoc [id=" + id + ", patient=" + patient
				+ ", user=" + user + ", userRole=" + userRole
				+ ", relationshipLabel=" + relationshipLabel + "]";
	}

}
