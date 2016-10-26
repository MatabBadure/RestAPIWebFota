package com.hillrom.vest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.ISO8601LocalDateDeserializer;
import com.hillrom.vest.domain.util.MMDDYYYYLocalDateSerializer;

@Entity
@Table(name = "ADHERENCE_RESET")
@SQLDelete(sql="Update ADHERENCE_RESET SET is_deleted = 1 where id = ?")
public class AdherenceReset {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	

	@JsonIgnore
	@ManyToOne(optional = false, targetEntity = PatientInfo.class,fetch=FetchType.LAZY)
	@JoinColumn(name = "patient_id", referencedColumnName = "id")
	private PatientInfo patient;
	
	
	@JsonIgnore
	@ManyToOne(optional = false, targetEntity = User.class,fetch=FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User patientUser;

	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = MMDDYYYYLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name="reset_start_date")
	private LocalDate resetStartDate;

	@Column(name="reset_score")
	private Integer resetScore;
	
	//hill-1847
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(name="reset_date")
    private DateTime resetDate;
	//hill-1847
	
	@Column(name="justification")
	private String justification;
	
	@Column(name="created_by")
	private Long createdBy;
	
	@Column(name="is_deleted")
	private boolean deleted;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.deleted = isDeleted;
	}

	public User getPatientUser() {
		return patientUser;
	}

	public void setPatientUser(User patientUser) {
		this.patientUser = patientUser;
	}

	public PatientInfo getPatient() {
		return patient;
	}

	public void setPatient(PatientInfo patient) {
		this.patient = patient;
	}

	public LocalDate getResetStartDate() {
		return resetStartDate;
	}

	public void setResetStartDate(LocalDate resetStartDate) {
		this.resetStartDate = resetStartDate;
	}
	
    public Integer getResetScore() {
        return resetScore;
    }

    public void setResetScore(Integer resetScore) {
        this.resetScore = resetScore;
    }
    
    
	//hill-1847
  	public DateTime getResetDate() {
  		return resetDate;
  	}

  	public void setResetDate(DateTime resetDate) {
  		this.resetDate = resetDate;
  	}
  	//hill-1847
	
	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}
	
	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
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
		AdherenceReset other = (AdherenceReset) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AdherenceReset [id=" + id  
				+ ", patientUser="+ patientUser + ", patient=" + patient 
				+ ", resetStartDate=" + resetStartDate + ", resetScore=" + resetScore 
				+ ", resetDate=" + resetDate + ", justification=" + justification 
				+ ", createdBy=" + createdBy + "]";
		
	}
	
}
