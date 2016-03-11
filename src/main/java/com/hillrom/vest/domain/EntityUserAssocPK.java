package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class EntityUserAssocPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID", referencedColumnName="id") 
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ENTITY_ID", referencedColumnName="id") 
    private Clinic clinic;
	
	public EntityUserAssocPK() {
		super();
	}

	public EntityUserAssocPK(User user, Clinic clinic) {
		super();
		this.user = user;
		this.clinic = clinic;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clinic == null) ? 0 : clinic.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		EntityUserAssocPK other = (EntityUserAssocPK) obj;
		if (clinic == null) {
			if (other.clinic != null)
				return false;
		} else if (!clinic.equals(other.clinic))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClinicPatientAssocPK [patient=" + user + ", clinic="
				+ clinic + "]";
	}
}
