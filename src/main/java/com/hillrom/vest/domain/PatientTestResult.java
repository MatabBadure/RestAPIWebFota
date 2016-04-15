package com.hillrom.vest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.ISO8601LocalDateDeserializer;
import com.hillrom.vest.domain.util.MMDDYYYYLocalDateSerializer;

@Entity
@Audited
@Table(name = "TEST_RESULTS")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class,property= "id")
public class PatientTestResult extends AbstractAuditingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private PatientInfo patientInfo;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = MMDDYYYYLocalDateSerializer.class)
	@Column(name = "test_result_date")
	private LocalDate testResultDate;

	@Column(name = "FVC_L")
	private double FVC_L;

	@Column(name = "FEV1_L")
	private double FEV1_L;

	@Column(name = "PEF_L_Min")
	private double PEF_L_Min;

	@Column(name = "FVC_P")
	private double FVC_P;

	@Column(name = "FEV1_P")
	private double FEV1_P;

	@Column(name = "PEF_P")
	private double PEF_P;

	@Column(name = "FEV1_TO_FVC_RATIO")
	private double FEV1_TO_FVC_RATIO;

	@Column(name = "Comments")
	private String comments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PatientInfo getPatientInfo() {
		return patientInfo;
	}

	public void setPatientInfo(PatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}

	public LocalDate getTestResultDate() {
		return testResultDate;
	}

	public void setTestResultDate(LocalDate testResultDate) {
		this.testResultDate = testResultDate;
	}

	public double getFVC_L() {
		return FVC_L;
	}

	public void setFVC_L(double fVC_L) {
		FVC_L = fVC_L;
	}

	public double getFEV1_L() {
		return FEV1_L;
	}

	public void setFEV1_L(double fEV1_L) {
		FEV1_L = fEV1_L;
	}

	public double getPEF_L_Min() {
		return PEF_L_Min;
	}

	public void setPEF_L_Min(double pEF_L_Min) {
		PEF_L_Min = pEF_L_Min;
	}

	public double getFVC_P() {
		return FVC_P;
	}

	public void setFVC_P(double fVC_P) {
		FVC_P = fVC_P;
	}

	public double getFEV1_P() {
		return FEV1_P;
	}

	public void setFEV1_P(double fEV1_P) {
		FEV1_P = fEV1_P;
	}

	public double getPEF_P() {
		return PEF_P;
	}

	public void setPEF_P(double pEF_P) {
		PEF_P = pEF_P;
	}

	public double getFEV1_TO_FVC_RATIO() {
		return FEV1_TO_FVC_RATIO;
	}

	public void setFEV1_TO_FVC_RATIO(double fEV1_TO_FVC_RATIO) {
		FEV1_TO_FVC_RATIO = fEV1_TO_FVC_RATIO;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@JsonInclude
    public String getLastModifiedBy() {
        return super.getLastModifiedBy();
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(FEV1_L);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(FEV1_P);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(FEV1_TO_FVC_RATIO);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(FVC_L);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(FVC_P);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(PEF_L_Min);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(PEF_P);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((testResultDate == null) ? 0 : testResultDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((patientInfo == null) ? 0 : patientInfo.hashCode());
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
		PatientTestResult other = (PatientTestResult) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (patientInfo == null) {
			if (other.patientInfo != null)
				return false;
		} else if (!patientInfo.equals(other.patientInfo))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
}
