package com.hillrom.vest.web.rest.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.CustomLocalDateSerializer;
import com.hillrom.vest.domain.util.ISO8601LocalDateDeserializer;

public class AdherenceTrendVO implements Serializable{

	private int updatedScore;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate date;
	private Map<String,Integer> notificationPoints = new HashMap<>();
	
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
	
	
	
}
