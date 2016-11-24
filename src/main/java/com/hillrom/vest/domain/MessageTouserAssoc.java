package com.hillrom.vest.domain;

import javax.persistence.CascadeType;
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
	

	@ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
	@JsonIgnore
	private User user;
	

	@ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "to_message_id")
	@JsonIgnore
	private Messages messages;

	@Column(name="is_archived")
	private Boolean isArchived;
	
	@Column(name="is_read")
	private Boolean isRead;
	
	@Column(name="is_deleted")
	private Boolean isDeleted;

	@ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "to_clinic_id")
	@JsonIgnore
	private Clinic toClinic;
	
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
	 * @return the user
	 */
	public User getUser() {
		return user;
	}


	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}


	/**
	 * @return the messages
	 */
	public Messages getMessages() {
		return messages;
	}


	/**
	 * @param messages the messages to set
	 */
	public void setMessages(Messages messages) {
		this.messages = messages;
	}


	/**
	 * @return the isArchived
	 */
	public Boolean getIsArchived() {
		return isArchived;
	}


	/**
	 * @param isArchived the isArchived to set
	 */
	public void setIsArchived(Boolean isArchived) {
		this.isArchived = isArchived;
	}


	/**
	 * @return the isRead
	 */
	public Boolean getIsRead() {
		return isRead;
	}


	/**
	 * @param isRead the isRead to set
	 */
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}


	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}


	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}


	/**
	 * @return the messageText
	 */
	public Clinic getToClinic() {
		return toClinic;
	}

	/**
	 * @param messageText the messageText to set
	 */
	public void setToClinic(Clinic toClinic) {
		this.toClinic = toClinic;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isArchived == null) ? 0 : isArchived.hashCode());
		result = prime * result + ((isDeleted == null) ? 0 : isDeleted.hashCode());
		result = prime * result + ((isRead == null) ? 0 : isRead.hashCode());
		result = prime * result + ((messages == null) ? 0 : messages.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((toClinic == null) ? 0 : toClinic.hashCode());
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
		if (isArchived == null) {
			if (other.isArchived != null)
				return false;
		} else if (!isArchived.equals(other.isArchived))
			return false;
		if (isDeleted == null) {
			if (other.isDeleted != null)
				return false;
		} else if (!isDeleted.equals(other.isDeleted))
			return false;
		if (isRead == null) {
			if (other.isRead != null)
				return false;
		} else if (!isRead.equals(other.isRead))
			return false;
		if (messages == null) {
			if (other.messages != null)
				return false;
		} else if (!messages.equals(other.messages))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (toClinic == null) {
			if (other.toClinic != null)
				return false;
		} else if (!toClinic.equals(other.toClinic))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageTouserAssoc [id=" + id + ", user=" + user + ", messages=" + messages + ", isArchived="
				+ isArchived + ", isRead=" + isRead + ", isDeleted=" + isDeleted + ", toClinic=" + toClinic + "]";
	}


	
	







	



	
}
