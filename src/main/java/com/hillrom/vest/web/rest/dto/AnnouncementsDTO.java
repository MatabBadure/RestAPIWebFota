package com.hillrom.vest.web.rest.dto;

/*import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Size;*/

import org.joda.time.LocalDate;

public class AnnouncementsDTO {
	
	private Long id;

	private String name;
    
    private String subject;
    
	private LocalDate startDate;
	
    private LocalDate endDate;
    
	private LocalDate createdDate;
	
    private LocalDate modifiedDate;
    
    private String sentTo;
    
    private String clicicType;
    
    private String pdfFilePath;
    
    private String patientType;


    public LocalDate getStartDate() {
		return startDate;
	}


	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}


	public LocalDate getEndDate() {
		return endDate;
	}


	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}


	public LocalDate getCreatedDate() {
		return createdDate;
	}


	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}


	public LocalDate getModifiedDate() {
		return modifiedDate;
	}


	public void setModifiedDate(LocalDate modifiedDate) {
		this.modifiedDate = modifiedDate;
	}


 	
	public AnnouncementsDTO() {
		super();
	}


    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getSentTo() {
		return sentTo;
	}


	public void setSentTo(String sentTo) {
		this.sentTo = sentTo;
	}


	public String getClicicType() {
		return clicicType;
	}


	public void setClicicType(String clicicType) {
		this.clicicType = clicicType;
	}


	public String getPdfFilePath() {
		return pdfFilePath;
	}


	public void setPdfFilePath(String pdfFilePath) {
		this.pdfFilePath = pdfFilePath;
	}


	public String getPatientType() {
		return patientType;
	}


	public void setPatientType(String patientType) {
		this.patientType = patientType;
	}


	//@Override
	/*public int hashCode() {
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
	}*/


	/*@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnnouncementsDTO other = (AnnouncementsDTO) obj;
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
*/

	

	/*@Override
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
*/
}
