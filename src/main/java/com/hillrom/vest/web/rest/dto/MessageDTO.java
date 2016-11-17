package com.hillrom.vest.web.rest.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Size;

public class MessageDTO {
	
	private Long id;
	
	private Long fromUserId;

    private String messageSubject;
    
    private Long messageSizeMbs;
    
    private String messageType;
	
    private Long toMessageId;
    
    private Long rootMessageId;
    
    private String messageText;
    
    private List<Long> toUserIds;
    
	


	public MessageDTO() {
		super();
	}








	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromUserId == null) ? 0 : fromUserId.hashCode());
		result = prime * result + ((messageSizeMbs == null) ? 0 : messageSizeMbs.hashCode());
		result = prime * result + ((messageSubject == null) ? 0 : messageSubject.hashCode());
		result = prime * result + ((messageText == null) ? 0 : messageText.hashCode());
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result + ((rootMessageId == null) ? 0 : rootMessageId.hashCode());
		result = prime * result + ((toMessageId == null) ? 0 : toMessageId.hashCode());
		result = prime * result + ((toUserIds == null) ? 0 : toUserIds.hashCode());
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
		MessageDTO other = (MessageDTO) obj;
		if (fromUserId == null) {
			if (other.fromUserId != null)
				return false;
		} else if (!fromUserId.equals(other.fromUserId))
			return false;
		if (messageSizeMbs == null) {
			if (other.messageSizeMbs != null)
				return false;
		} else if (!messageSizeMbs.equals(other.messageSizeMbs))
			return false;
		if (messageSubject == null) {
			if (other.messageSubject != null)
				return false;
		} else if (!messageSubject.equals(other.messageSubject))
			return false;
		if (messageText == null) {
			if (other.messageText != null)
				return false;
		} else if (!messageText.equals(other.messageText))
			return false;
		if (messageType == null) {
			if (other.messageType != null)
				return false;
		} else if (!messageType.equals(other.messageType))
			return false;
		if (rootMessageId == null) {
			if (other.rootMessageId != null)
				return false;
		} else if (!rootMessageId.equals(other.rootMessageId))
			return false;
		if (toMessageId == null) {
			if (other.toMessageId != null)
				return false;
		} else if (!toMessageId.equals(other.toMessageId))
			return false;
		if (toUserIds == null) {
			if (other.toUserIds != null)
				return false;
		} else if (!toUserIds.equals(other.toUserIds))
			return false;
		return true;
	}








	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageDTO [id=" + id + ", fromUserId=" + fromUserId + ", messageSubject=" + messageSubject + ", messageSizeMbs="
				+ messageSizeMbs + ", messageType=" + messageType + ", toMessageId=" + toMessageId + ", rootMessageId="
				+ rootMessageId + ", messageText=" + messageText + ", toUserIds=" + toUserIds + "]";
	}




	/**
	 * @return the fromUserId
	 */
	public Long getId() {
		return id;
	}




	/**
	 * @param fromUserId the fromUserId to set
	 */
	public void setId(Long id) {
		this.id = id;
	}




	/**
	 * @return the fromUserId
	 */
	public Long getFromUserId() {
		return fromUserId;
	}




	/**
	 * @param fromUserId the fromUserId to set
	 */
	public void setFromUserId(Long fromUserId) {
		this.fromUserId = fromUserId;
	}




	/**
	 * @return the messageSubject
	 */
	public String getMessageSubject() {
		return messageSubject;
	}




	/**
	 * @param messageSubject the messageSubject to set
	 */
	public void setMessageSubject(String messageSubject) {
		this.messageSubject = messageSubject;
	}




	/**
	 * @return the messageSizeMbs
	 */
	public Long getMessageSizeMbs() {
		return messageSizeMbs;
	}




	/**
	 * @param messageSizeMbs the messageSizeMbs to set
	 */
	public void setMessageSizeMbs(Long messageSizeMbs) {
		this.messageSizeMbs = messageSizeMbs;
	}




	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}




	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}




	/**
	 * @return the toMessageId
	 */
	public Long getToMessageId() {
		return toMessageId;
	}




	/**
	 * @param toMessageId the toMessageId to set
	 */
	public void setToMessageId(Long toMessageId) {
		this.toMessageId = toMessageId;
	}




	/**
	 * @return the rootMessageId
	 */
	public Long getRootMessageId() {
		return rootMessageId;
	}




	/**
	 * @param rootMessageId the rootMessageId to set
	 */
	public void setRootMessageId(Long rootMessageId) {
		this.rootMessageId = rootMessageId;
	}




	/**
	 * @return the messageText
	 */
	public String getMessageText() {
		return messageText;
	}




	/**
	 * @param messageText the messageText to set
	 */
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}




	/**
	 * @return the toUserIds
	 */
	public List<Long> getToUserIds() {
		return toUserIds;
	}




	/**
	 * @param toUserIds the toUserIds to set
	 */
	public void setToUserIds(List<Long> toUserIds) {
		this.toUserIds = toUserIds;
	}

	

}
