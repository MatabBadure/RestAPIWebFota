package com.hillrom.vest.web.rest.dto;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.util.ISO8601LocalDateDeserializer;
import com.hillrom.vest.domain.util.MMDDYYYYLocalDateSerializer;

public class AdherenceResetDTO {
	
	
	@Size(max = 50)
    private String patientId;
	
	@Size(max = 50)
    private String userId;
	
	@Size(max = 50)
    private String resetStartDate;
	
	@Size(max = 50)
    private String resetScore;
	
	@Size(max = 50)
    private String resetDate;
	
	@Size(max = 50)
    private String justification;
	
	@Size(max = 50)
    private String createdBy;
	
	@Size(max = 50)
    private String deviceType;
	
	private Boolean isDeleted;

	public AdherenceResetDTO() {
		super();
	}

	public AdherenceResetDTO(String userId, String patientId, String resetStartDate, String resetScore,
			String resetDate, String justification, String createdBy, Boolean isDeleted, String deviceType) {
		super();
		this.userId = userId;
		this.patientId = patientId;
		this.resetStartDate = resetStartDate;
		this.resetScore = resetScore;
		this.resetDate = resetDate;
		this.justification = justification;
		this.createdBy = createdBy;
		this.isDeleted = isDeleted;
		this.deviceType = deviceType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	
	public String getResetStartDate() {
		return resetStartDate;
	}

	public void setResetStartDate(String resetStartDate) {
		this.resetStartDate = resetStartDate;
	}
	
	public String getResetScore() {
		return resetScore;
	}

	public void setResetScore(String resetScore) {
		this.resetScore = resetScore;
	}
	
	public String getResetDate() {
		return resetDate;
	}

	public void setResetDate(String resetDate) {
		this.resetDate = resetDate;
	}
		
	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public String toString() {
		return "AdherenceResetDTO [userId="+ userId + ",patientId="
				+ patientId + ", resetStartDate=" + resetStartDate + ", resetScore="
				+ resetScore + ", resetDate=" + resetDate + ", deviceType=" + deviceType + ", justification="
				+ justification + ", createdBy=" + createdBy + ", isDeleted=" + isDeleted + "]";
		
	}
	
}
