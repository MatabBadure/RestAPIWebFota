package com.hillrom.vest.web.rest.dto.monarch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.NotificationMonarch;
import com.hillrom.vest.domain.util.ISO8601LocalDateDeserializer;
import com.hillrom.vest.domain.util.MMDDYYYYLocalDateSerializer;

public class AdherenceTrendMonarchVO implements Serializable{

	private int updatedScore;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = MMDDYYYYLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate date;
	private Map<String,Integer> notificationPoints = new HashMap<>();
	private List<NotificationMonarch> prevNotificationDetails = new ArrayList(); 
	//hill-1847
	private boolean scoreReset;
	//hill-1847
	public int getUpdatedScore() {
		return updatedScore;
	}
	public void setUpdatedScore(int updatedScore) {
		this.updatedScore = updatedScore;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public Map<String, Integer> getNotificationPoints() {
		return notificationPoints;
	}
	public void setNotificationPoints(Map<String, Integer> notificationPoints) {
		this.notificationPoints = notificationPoints;
	}
	
	
	/**
	 * @return the prevNotificationDetails
	 */
	public List<NotificationMonarch> getPrevNotificationDetails() {
		return prevNotificationDetails;
	}
	/**
	 * @param prevNotificationDetails the prevNotificationDetails to set
	 */
	public void setPrevNotificationDetails(List<NotificationMonarch> prevNotificationDetails) {
		this.prevNotificationDetails = prevNotificationDetails;
	}
	//hill-1847
	public boolean isScoreReset() {
		return scoreReset;
	}
	public void setScoreReset(boolean scoreReset) {
		this.scoreReset = scoreReset;
	}
	//hill-1847
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AdherenceTrendMonarchVO [updatedScore=" + updatedScore + ", date=" + date + ", notificationPoints="
				+ notificationPoints + ", prevNotificationDetails=" + prevNotificationDetails + ", scoreReset=" + scoreReset + "]";
	}	
	
	
}
