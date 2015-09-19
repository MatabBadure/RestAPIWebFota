package com.hillrom.vest.web.rest.dto;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class TreatmentStatisticsVO implements Serializable{

	private int avgTreatments;
	private int avgTreatmentDuration;
	private DateTime startTime;
	private DateTime endTime;
	
	public TreatmentStatisticsVO(int avgTreatments, int avgTreatmentDuration,
			DateTime startTime, DateTime endTime) {
		super();
		this.avgTreatments = avgTreatments;
		this.avgTreatmentDuration = avgTreatmentDuration;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public TreatmentStatisticsVO(LocalDate startTime, LocalDate endTime) {
		super();
		this.startTime = startTime.toDateTimeAtCurrentTime();
		this.endTime = endTime.toDateTimeAtCurrentTime();
	}


	public int getAvgTreatments() {
		return avgTreatments;
	}
	public void setAvgTreatments(int avgTreatments) {
		this.avgTreatments = avgTreatments;
	}
	public int getAvgTreatmentDuration() {
		return avgTreatmentDuration;
	}
	public void setAvgTreatmentDuration(int avgTreatmentDuration) {
		this.avgTreatmentDuration = avgTreatmentDuration;
	}
	public DateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}
	public DateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}
	
	
}
