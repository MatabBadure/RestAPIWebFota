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
@Table(name = "MESSAGE_TOUSER_ASSOC")
public class MessageTouserAssoc {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	

	@Column(name="to_user_id")
	private Long toUserId;
	

	@Column(name="to_message_id")
	private Long toMessageId;


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
	 * @return the toUserId
	 */
	public Long getToUserId() {
		return toUserId;
	}


	/**
	 * @param toUserId the toUserId to set
	 */
	public void setToUserId(Long toUserId) {
		this.toUserId = toUserId;
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


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((toMessageId == null) ? 0 : toMessageId.hashCode());
		result = prime * result + ((toUserId == null) ? 0 : toUserId.hashCode());
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
		MessageTouserAssoc other = (MessageTouserAssoc) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (toMessageId == null) {
			if (other.toMessageId != null)
				return false;
		} else if (!toMessageId.equals(other.toMessageId))
			return false;
		if (toUserId == null) {
			if (other.toUserId != null)
				return false;
		} else if (!toUserId.equals(other.toUserId))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageTouserAssoc [id=" + id + ", toUserId=" + toUserId + ", toMessageId=" + toMessageId + "]";
	}


	



	
}
