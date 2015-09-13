package com.hillrom.vest.web.rest.dto;

import java.io.Serializable;

import com.hillrom.vest.domain.User;

public class CareGiverVO implements Serializable{

	private String userRole;
	private String relationshipLabel;
	private User user;

	public CareGiverVO(String userRole, String relationshipLabel, User user) {
		super();
		this.userRole = userRole;
		this.relationshipLabel = relationshipLabel;
		this.user = user;
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
