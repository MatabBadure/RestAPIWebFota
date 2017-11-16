package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Embeddable
public class ClinicUserAssocPK implements Serializable {

	private static final long serialVersionUID = 1L;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CLINICS_ID", referencedColumnName="id")
    @JsonIgnore
    private Clinic clinic;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USERS_ID", referencedColumnName="id") 
    private User user;
	
	public ClinicUserAssocPK() {
		super();
	}

	public ClinicUserAssocPK(Clinic clinic, User user) {
		super();
		this.clinic = clinic;
		this.user = user;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clinic == null) ? 0 : clinic.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		ClinicUserAssocPK other = (ClinicUserAssocPK) obj;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClinicUserAssocPK [clinic=" + clinic + ", user=" + user + "]";
	}

	
}
