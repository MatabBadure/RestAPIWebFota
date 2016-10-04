package com.hillrom.vest.web.rest.dto;

import javax.validation.constraints.Size;

public class NoteDTO {
	
	@Size(max = 50)
	private String createdOn;

	@Size(max = 50)
    private String userId;
	
	@Size(max = 50)
    private String patientId;

	@Size(max = 5000)
    private String note;

	@Size(max = 50)
    private String modifiedAt;
	
	private Boolean isDeleted;

	public NoteDTO() {
		super();
	}

	public NoteDTO(String createdOn, String userId, String patientId, String note, 
			String modifiedAt, Boolean isDeleted) {
		super();
		this.createdOn = createdOn;
		this.userId = userId;
		this.patientId = patientId;
		this.note = note;
		this.modifiedAt = modifiedAt;
		this.isDeleted = isDeleted;		
	}


	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
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
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public String getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(String modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
		
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	@Override
	public String toString() {
		return "NoteDTO [createdOn=" + createdOn + ", userId="
				+ userId + ",patientId=" 
				+ patientId + ", note=" + note + ", modifiedAt=" 
				+ modifiedAt + ", isDeleted=" + isDeleted + "]";
	}
	
}
