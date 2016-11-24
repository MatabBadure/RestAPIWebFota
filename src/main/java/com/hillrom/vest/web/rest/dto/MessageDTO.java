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
    
    private List<String> toClinicIds;
    
	private String fromClinicId;
	
	private boolean isArchived;
	
	private boolean isRead;


	public MessageDTO() {
		super();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fromClinicId == null) ? 0 : fromClinicId.hashCode());
		result = prime * result
				+ ((fromUserId == null) ? 0 : fromUserId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isArchived ? 1231 : 1237);
		result = prime * result + (isRead ? 1231 : 1237);
		result = prime * result
				+ ((messageSizeMbs == null) ? 0 : messageSizeMbs.hashCode());
		result = prime * result
				+ ((messageSubject == null) ? 0 : messageSubject.hashCode());
		result = prime * result
				+ ((messageText == null) ? 0 : messageText.hashCode());
		result = prime * result
				+ ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result
				+ ((rootMessageId == null) ? 0 : rootMessageId.hashCode());
		result = prime * result
				+ ((toClinicIds == null) ? 0 : toClinicIds.hashCode());
		result = prime * result
				+ ((toMessageId == null) ? 0 : toMessageId.hashCode());
		result = prime * result
				+ ((toUserIds == null) ? 0 : toUserIds.hashCode());
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
		MessageDTO other = (MessageDTO) obj;
		if (fromClinicId == null) {
			if (other.fromClinicId != null)
				return false;
		} else if (!fromClinicId.equals(other.fromClinicId))
			return false;
		if (fromUserId == null) {
			if (other.fromUserId != null)
				return false;
		} else if (!fromUserId.equals(other.fromUserId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isArchived != other.isArchived)
			return false;
		if (isRead != other.isRead)
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
		if (toClinicIds == null) {
			if (other.toClinicIds != null)
				return false;
		} else if (!toClinicIds.equals(other.toClinicIds))
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


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getFromUserId() {
		return fromUserId;
	}


	public void setFromUserId(Long fromUserId) {
		this.fromUserId = fromUserId;
	}


	public String getMessageSubject() {
		return messageSubject;
	}


	public void setMessageSubject(String messageSubject) {
		this.messageSubject = messageSubject;
	}


	public Long getMessageSizeMbs() {
		return messageSizeMbs;
	}


	public void setMessageSizeMbs(Long messageSizeMbs) {
		this.messageSizeMbs = messageSizeMbs;
	}


	public String getMessageType() {
		return messageType;
	}


	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}


	public Long getToMessageId() {
		return toMessageId;
	}


	public void setToMessageId(Long toMessageId) {
		this.toMessageId = toMessageId;
	}


	public Long getRootMessageId() {
		return rootMessageId;
	}


	public void setRootMessageId(Long rootMessageId) {
		this.rootMessageId = rootMessageId;
	}


	public String getMessageText() {
		return messageText;
	}


	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}


	public List<Long> getToUserIds() {
		return toUserIds;
	}


	public void setToUserIds(List<Long> toUserIds) {
		this.toUserIds = toUserIds;
	}


	public List<String> getToClinicIds() {
		return toClinicIds;
	}


	public void setToClinicIds(List<String> toClinicIds) {
		this.toClinicIds = toClinicIds;
	}


	public String getFromClinicId() {
		return fromClinicId;
	}


	public void setFromClinicId(String fromClinicId) {
		this.fromClinicId = fromClinicId;
	}


	public boolean isArchived() {
		return isArchived;
	}


	public void setArchived(boolean isArchived) {
		this.isArchived = isArchived;
	}


	public boolean isRead() {
		return isRead;
	}


	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	

	@Override
	public String toString() {
		return "MessageDTO [id=" + id + ", fromUserId=" + fromUserId
				+ ", messageSubject=" + messageSubject + ", messageSizeMbs="
				+ messageSizeMbs + ", messageType=" + messageType
				+ ", toMessageId=" + toMessageId + ", rootMessageId="
				+ rootMessageId + ", messageText=" + messageText
				+ ", toUserIds=" + toUserIds + ", toClinicIds=" + toClinicIds
				+ ", fromClinicId=" + fromClinicId + ", isArchived="
				+ isArchived + ", isRead=" + isRead + "]";
	}

}
