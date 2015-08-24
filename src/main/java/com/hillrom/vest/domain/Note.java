package com.hillrom.vest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import com.hillrom.vest.domain.util.CustomLocalDateSerializer;
import com.hillrom.vest.domain.util.ISO8601LocalDateDeserializer;

@Entity
@Table(name = "PATIENT_NOTE")
@SQLDelete(sql="Update PATIENT_NOTE SET is_deleted = 1 where id = ?")
public class Note {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "created_on")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate createdOn;

	@JsonIgnore
	@ManyToOne(optional = false, targetEntity = User.class)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User patientUser;
	
	@JsonIgnore
	@ManyToOne(optional = false, targetEntity = PatientInfo.class)
	@JoinColumn(name = "patient_id", referencedColumnName = "id")
	private PatientInfo patient;

	@Column(name="note")
	private String note;

	@Column(name="modified_at")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime modifiedAt = DateTime.now();
	
	@Column(name="is_deleted")
	private boolean isDeleted;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDate createdOn) {
		this.createdOn = createdOn;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public DateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(DateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
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
		Note other = (Note) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Note [id=" + id + ", createdOn=" + createdOn + ", patientUSer="
				+ patientUser + ", patient=" + patient + ", note=" + note
				+ ", modifiedAt=" + modifiedAt + "]";
	}
	
}
