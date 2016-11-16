package com.hillrom.vest.web.rest.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Size;

public class MessageToUserAssoDTO {
	

	private Long userId;
    
    private Long messageId;
    
    private boolean archived;


	public MessageToUserAssoDTO() {
		super();
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





	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (archived ? 1231 : 1237);
		result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}





	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageToUserAssoDTO [userId=" + userId + ", messageId=" + messageId + ", archived=" + archived + "]";
	}





	








	

}
