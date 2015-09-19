package com.hillrom.vest.web.rest.dto;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class StatisticsVO {

	private int missedTherapy;
	private int nonCompliance;
	private int settingDeviation;
	private int noEvent;

	private DateTime startTimestamp;
	
	private DateTime endTimestamp;

	public StatisticsVO(int missedTherapy, int nonCompliance,
			int settingDeviation, int noEvent, LocalDate startTimestamp,LocalDate endTimestamp) {
		super();
		this.missedTherapy = missedTherapy;
		this.nonCompliance = nonCompliance;
		this.settingDeviation = settingDeviation;
		this.noEvent = noEvent;
		this.startTimestamp = startTimestamp.toDateTimeAtCurrentTime();
		this.endTimestamp = endTimestamp.toDateTimeAtCurrentTime();
	}

	public StatisticsVO() {
		super();
	}

	public int getMissedTherapy() {
		return missedTherapy;
	}

	public void setMissedTherapy(int missedTherapy) {
		this.missedTherapy = missedTherapy;
	}

	public int getNonCompliance() {
		return nonCompliance;
	}

	public void setNonCompliance(int nonCompliance) {
		this.nonCompliance = nonCompliance;
	}

	public int getSettingDeviation() {
		return settingDeviation;
	}

	public void setSettingDeviation(int settingDeviation) {
		this.settingDeviation = settingDeviation;
	}

	public int getNoEvent() {
		return noEvent;
	}

	public void setNoEvent(int noEvent) {
		this.noEvent = noEvent;
	}

	public DateTime getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(DateTime startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public DateTime getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(DateTime endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	
}
