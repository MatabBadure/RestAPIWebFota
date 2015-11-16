package com.hillrom.vest.domain;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.joda.time.DateTime;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.JsonIdentityInfo;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.ObjectIdGenerators;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Clinic.
 */
@Entity
@Audited
@Table(name = "CLINIC")
@SQLDelete(sql="UPDATE CLINIC SET is_deleted = 1 WHERE id = ?")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class,property= "@id,@name")
public class Clinic implements Serializable {

    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "address1")
    private String address1;
    
    @Column(name = "address2")
    private String address2;

    @Column(name = "zipcode")
    private Integer zipcode;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "fax_number")
    private String faxNumber;

    @Column(name = "hillrom_id")
    private String hillromId;
    
    @Column(name = "clinic_admin_id")
    private Long clinicAdminId;

    @NotAudited
    @ManyToOne
    @JoinColumn(name="parent_clinic_id")
    @JsonManagedReference
    private Clinic parentClinic;
 
    @NotAudited
    @OneToMany(mappedBy="parentClinic")
    @JsonIgnore
    private List<Clinic> childClinics = new ArrayList<Clinic>();

    @NotAudited
    @OneToMany(mappedBy = "clinicPatientAssocPK.clinic",fetch=FetchType.LAZY)
    @JsonIgnore
    private Set<ClinicPatientAssoc> clinicPatientAssoc = new HashSet<>();
    
    @ManyToMany(mappedBy="clinics")
    @JsonBackReference
    private Set<UserExtension> users = new HashSet<>();
    
    @Column(name="is_deleted", nullable = false)
    private boolean deleted = false;
    
    @Column(name="is_parent", nullable = false)
    private boolean parent = false;

    @Column(name="created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable=false, updatable=false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdAt;

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public Integer getZipcode() {
        return zipcode;
    }

    public void setZipcode(Integer zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getHillromId() {
        return hillromId;
    }

    public void setHillromId(String hillromId) {
        this.hillromId = hillromId;
    }

    public Long getClinicAdminId() {
		return clinicAdminId;
	}

	public void setClinicAdminId(Long clinicAdminId) {
		this.clinicAdminId = clinicAdminId;
	}

	public Clinic getParentClinic() {
		return parentClinic;
	}

	public void setParentClinic(Clinic parentClinic) {
		this.parentClinic = (parentClinic != null) ? parentClinic : null;
	}

	public List<Clinic> getChildClinics() {
		return childClinics;
	}

	public void setChildClinics(List<Clinic> childClinics) {
		this.childClinics = childClinics;
	}

    public Set<UserExtension> getUsers() {
		return users;
	}

	public void setUsers(Set<UserExtension> users) {
		this.users = users;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.deleted = isDeleted;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public Set<ClinicPatientAssoc> getClinicPatientAssoc() {
		return clinicPatientAssoc;
	}

	public void setClinicPatientAssoc(Set<ClinicPatientAssoc> clinicPatientAssoc) {
		this.clinicPatientAssoc = clinicPatientAssoc;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Clinic other = (Clinic) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return "Clinic{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", address1='" + address1 + "'" +
                ", address2='" + address2 + "'" +
                ", zipcode='" + zipcode + "'" +
                ", city='" + city + "'" +
                ", state='" + state + "'" +
                ", phoneNumber='" + phoneNumber + "'" +
                ", faxNumber='" + faxNumber + "'" +
                ", hillromId='" + hillromId + "'" +
                ", parentClinic='" + parentClinic + "'" +
                ", deleted='" + deleted + "'" +
                ", isParent='" + parent + "'" +
                '}';
    }
}
