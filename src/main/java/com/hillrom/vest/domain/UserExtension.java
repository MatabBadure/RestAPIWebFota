package com.hillrom.vest.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.JsonManagedReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hillrom.vest.repository.HillRomUserVO;

/**
 * A UserExtension.
 */
@Entity
@Table(name = "USER_EXTENSION")
@PrimaryKeyJoinColumn(name="USER_ID",referencedColumnName="id")
@SQLDelete(sql="UPDATE USER_EXTENSION SET is_deleted = 1 WHERE USER_ID = ?")
@NamedNativeQuery(query="select user.id,user.first_name,user.last_name,user.email,authority.name from  USER_EXTENSION userExt join USER user "
		+ " join  USER_AUTHORITY user_authority join  AUTHORITY authority "
		+ " where user.id = userExt.user_id and user_authority.user_id = user.id "
		+ " and user_authority.authority_name = authority.name "
		+ " and (lower(user.first_name) like lower(:queryString) or "
		+ " lower(user.last_name) like lower(:queryString) or "
		+ " lower(user.email) like lower(:queryString)) order by user.first_name,user.last_name,user.email",
		name="findHillRomTeamUserBy",
		resultSetMapping="hillromTeamUser")
@SqlResultSetMapping(name="hillromTeamUser", 
classes={ 
  @ConstructorResult(targetClass=HillRomUserVO.class, columns={
      @ColumnResult(name="ID", type=Long.class),
      @ColumnResult(name="FIRST_NAME", type=String.class),
      @ColumnResult(name="LAST_NAME", type=String.class),
      @ColumnResult(name="EMAIL", type=String.class),
      @ColumnResult(name="NAME", type=String.class)
  })
}
)
public class UserExtension extends User implements Serializable {

    @Column(name = "speciality")
    private String speciality;

    @Column(name = "credentials")
    private String credentials;
    
    @Column(name = "primary_phone")
    private Long primaryPhone;

    @Column(name = "mobile_phone")
    private Long mobilePhone;

    @Column(name = "fax_number")
    private Long faxNumber;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "npi_number")
    private String npiNumber;
    
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "CLINIC_USER_ASSOC",
            joinColumns = {@JoinColumn(name = "users_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "clinics_id", referencedColumnName = "id")})
    @JsonManagedReference
    private Set<Clinic> clinics = new HashSet<>();
    
    @Column(name="is_deleted", nullable = false)
    private boolean deleted = false;
    
	public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public Long getPrimaryPhone() {
		return primaryPhone;
	}

	public void setPrimaryPhone(Long primaryPhone) {
		this.primaryPhone = primaryPhone;
	}

	public Long getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(Long mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Long getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(Long faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getNpiNumber() {
		return npiNumber;
	}

	public void setNpiNumber(String npiNumber) {
		this.npiNumber = npiNumber;
	}

	public Set<Clinic> getClinics() {
		return clinics;
	}

	public void setClinics(Set<Clinic> clinics) {
		this.clinics = clinics;
	}

	@Override
	public String toString() {
		return "UserExtension [speciality=" + speciality + ", credentials="
				+ credentials + ", primaryPhone=" + primaryPhone
				+ ", mobilePhone=" + mobilePhone + ", faxNumber=" + faxNumber
				+ ", address=" + address + ", city=" + city + ", state="
				+ state + ", npiNumber=" + npiNumber + "]";
	}

}
