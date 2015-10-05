package com.hillrom.vest.web.rest.dto;

import java.io.Serializable;

import com.hillrom.vest.domain.User;

public class CareGiverVO implements Serializable{

	private String userRole;
	private String relationshipLabel;
	private User user;
	private Long userId;
	private String patientId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public CareGiverVO(String userRole, String relationshipLabel, User user,Long userId, String patientId) {
		super();
		this.userRole = userRole;
		this.relationshipLabel = relationshipLabel;
		this.user = user;
		this.userId = userId;
		this.patientId = patientId;
	}
	
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	public String getRelationshipLabel() {
		return relationshipLabel;
	}
	public void setRelationshipLabel(String relationshipLabel) {
		this.relationshipLabel = relationshipLabel;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
}
