package com.hillrom.vest.web.rest.dto;

import org.joda.time.LocalDate;

public class StatisticsVO {

	private int missedTherapy;
	private int nonCompliance;
	private int settingDeviation;
	private int noEvent;
	private LocalDate startTimestamp;
	private LocalDate endTimestamp;

	public StatisticsVO(int missedTherapy, int nonCompliance,
			int settingDeviation, int noEvent, LocalDate startTimestamp,LocalDate endTimestamp) {
		super();
		this.missedTherapy = missedTherapy;
		this.nonCompliance = nonCompliance;
		this.settingDeviation = settingDeviation;
		this.noEvent = noEvent;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
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

	public LocalDate getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(LocalDate startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public LocalDate getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(LocalDate endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

}
