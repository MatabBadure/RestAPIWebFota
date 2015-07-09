package com.hillrom.vest.domain;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.JsonIdentityInfo;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.ObjectIdGenerators;

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

    @ManyToOne
    @JoinColumn(name="parent_clinic_id")
    private Clinic parentClinic;
 
    @OneToMany(mappedBy="parentClinic")
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
    private Set<Clinic> childClinics = new HashSet<Clinic>();

    @Column(name = "npi_number")
    private String npiNumber;

    @ManyToMany
    @JoinTable(name = "CLINIC_USER_ASSOC",
               joinColumns = @JoinColumn(name="clinics_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="users_id", referencedColumnName="ID"))
    private Set<User> users = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "CLINIC_PATIENTS_ASSOC",
               joinColumns = @JoinColumn(name="clinics_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="patients_id", referencedColumnName="ID"))
    private Set<PatientInfo> patients = new HashSet<>();
    
    @Column(name="is_deleted", nullable = false)
    @JsonIgnore
    private boolean deleted = false;

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

    public Clinic getParentClinic() {
		return parentClinic;
	}

	public void setParentClinic(Clinic parentClinic) {
		this.parentClinic = (parentClinic != null) ? parentClinic : null;
	}

	public Set<Clinic> getChildClinics() {
		return childClinics;
	}

	public void setChildClinics(Set<Clinic> childClinics) {
		this.childClinics = childClinics;
	}

	public String getNpiNumber() {
        return npiNumber;
    }

    public void setNpiNumber(String npiNumber) {
        this.npiNumber = npiNumber;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<PatientInfo> getPatients() {
        return patients;
    }

    public void setPatients(Set<PatientInfo> patientInfos) {
        this.patients = patientInfos;
    }

    public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.deleted = isDeleted;
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
                ", npiNumber='" + npiNumber + "'" +
                ", deleted='" + deleted + "'" +
                '}';
    }
}
