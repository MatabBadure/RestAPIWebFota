package com.hillrom.vest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import com.hillrom.vest.domain.util.ISO8601LocalDateDeserializer;
import com.hillrom.vest.domain.util.MMDDYYYYLocalDateSerializer;

@Entity
@Table(name = "MESSAGES")
public class Messages {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "message_datetime")
    private DateTime messageDatetime;

	@Column(name="from_user_id")
	private Long fromUserId;
	
	@Column(name="message_subject")
	private String messageSubject;

	@Column(name="message_size_MBs")
	private Long messageSizeMBs;

	@Column(name="message_type")
	private String messageType;
	

	@Column(name="to_message_id")
	private Long toMessageId;
	
	@Column(name="root_message_id")
	private Long rootMessageId;
	

	@Column(name="message_text")
	private String messageText;

	



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
	 * @return the messageDatetime
	 */
	public DateTime getMessageDatetime() {
		return messageDatetime;
	}

	/**
	 * @param messageDatetime the messageDatetime to set
	 */
	public void setMessageDatetime(DateTime messageDatetime) {
		this.messageDatetime = messageDatetime;
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
	 * @return the messageSizeMBs
	 */
	public Long getMessageSizeMBs() {
		return messageSizeMBs;
	}

	/**
	 * @param messageSizeMBs the messageSizeMBs to set
	 */
	public void setMessageSizeMBs(Long messageSizeMBs) {
		this.messageSizeMBs = messageSizeMBs;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromUserId == null) ? 0 : fromUserId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((messageDatetime == null) ? 0 : messageDatetime.hashCode());
		result = prime * result + ((messageSizeMBs == null) ? 0 : messageSizeMBs.hashCode());
		result = prime * result + ((messageSubject == null) ? 0 : messageSubject.hashCode());
		result = prime * result + ((messageText == null) ? 0 : messageText.hashCode());
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result + ((rootMessageId == null) ? 0 : rootMessageId.hashCode());
		result = prime * result + ((toMessageId == null) ? 0 : toMessageId.hashCode());
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
		Messages other = (Messages) obj;
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
		if (messageDatetime == null) {
			if (other.messageDatetime != null)
				return false;
		} else if (!messageDatetime.equals(other.messageDatetime))
			return false;
		if (messageSizeMBs == null) {
			if (other.messageSizeMBs != null)
				return false;
		} else if (!messageSizeMBs.equals(other.messageSizeMBs))
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
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [id=" + id + ", messageDatetime=" + messageDatetime + ", fromUserId=" + fromUserId
				+ ", messageSubject=" + messageSubject + ", messageSizeMBs=" + messageSizeMBs + ", messageType="
				+ messageType + ", toMessageId=" + toMessageId + ", rootMessageId=" + rootMessageId + ", messageText="
				+ messageText + "]";
	}


	
}
