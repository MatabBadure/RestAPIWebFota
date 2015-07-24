package com.hillrom.vest.domain;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.JsonIdentityInfo;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.ObjectIdGenerators;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Clinic.
 */
@Entity
@Table(name = "CLINIC")
@SQLDelete(sql="UPDATE CLINIC SET is_deleted = 1 WHERE id = ?")
public class Clinic implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "zipcode")
    private Integer zipcode;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "phone_number")
    private Long phoneNumber;

    @Column(name = "fax_number")
    private Long faxNumber;

    @Column(name = "hillrom_id")
    private String hillromId;
    
    @Column(name = "clinic_admin_id")
    private Long clinicAdminId;

    @ManyToOne
    @JoinColumn(name="parent_clinic_id")
    @JsonManagedReference
    private Clinic parentClinic;
 
    @OneToMany(mappedBy="parentClinic")
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
    @JsonBackReference
    private List<Clinic> childClinics = new ArrayList<Clinic>();

    @OneToMany(mappedBy = "clinic",fetch=FetchType.LAZY)
    @JsonIgnore
    private Set<ClinicPatientAssoc> clinicPatientAssoc = new HashSet<>();
    
    @ManyToMany(mappedBy="clinics")
    @JsonBackReference
    private Set<UserExtension> users = new HashSet<>();
    
    @Column(name="is_deleted", nullable = false)
    private boolean deleted = false;
    
    @Column(name="is_parent", nullable = false)
    private boolean parent = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(Long faxNumber) {
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

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Clinic clinic = (Clinic) o;

        if ( ! Objects.equals(id, clinic.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Clinic{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", address='" + address + "'" +
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
