package com.hillrom.vest.web.rest.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Size;

public class MessageToUserAssoDTO {
	
	private Long id;
	
	private Long userId;
    
    private Long messageId;
    
    private boolean archived;
    
    private boolean read;

    private String clinicId;
    
	public MessageToUserAssoDTO() {
		super();
	}


	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}





	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}





	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}





	/**
	 * @return the messageId
	 */
	public Long getMessageId() {
		return messageId;
	}





	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}











	/**
	 * @return the archived
	 */
	public boolean isArchived() {
		return archived;
	}





	/**
	 * @param archived the archived to set
	 */
	public void setArchived(boolean archived) {
		this.archived = archived;
	}


	



	/**
	 * @return the read
	 */
	public boolean isRead() {
		return read;
	}





	/**
	 * @param read the read to set
	 */
	public void setRead(boolean read) {
		this.read = read;
	}



	/**
	 * @return the clinicId
	 */
	public String getClinicId() {
		return clinicId;
	}





	/**
	 * @param clinicId the clinicId to set
	 */
	public void setClinicId(String clinicId) {
		this.clinicId = clinicId;
	}
	


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (archived ? 1231 : 1237);
		result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
		result = prime * result + (read ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((clinicId == null) ? 0 : clinicId.hashCode());
		return result;
	}





	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageToUserAssoDTO other = (MessageToUserAssoDTO) obj;
		if (archived != other.archived)
			return false;
		if (messageId == null) {
			if (other.messageId != null)
				return false;
		} else if (!messageId.equals(other.messageId))
			return false;
		if (read != other.read)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (clinicId == null) {
			if (other.clinicId != null)
				return false;
		} else if (!clinicId.equals(other.clinicId))
			return false;
		return true;
	}





	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageToUserAssoDTO [id=" + id + ", userId=" + userId + ", messageId=" + messageId + ", archived=" + archived
				+ ", read=" + read + "]";
	}










	








	

}
