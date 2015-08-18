package com.hillrom.vest.web.rest.dto;

import org.joda.time.DateTime;

import com.hillrom.vest.domain.Note;

public class TherapyDataVO{

	private DateTime timestamp;
	private int treatmentsPerDay;
	private double weightedAvgFrequency;
	private double weightedAvgPressure;
	private int programmedCoughPauses;
	private int normalCoughPauses;
	private int coughPauses;
	private Note note;
	private DateTime start;
	private DateTime end;
	private int coughPauseDuration;
	public DateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(DateTime timestamp) {
		this.timestamp = timestamp;
	}
	public int getTreatmentsPerDay() {
		return treatmentsPerDay;
	}
	public void setTreatmentsPerDay(int treatmentsPerDay) {
		this.treatmentsPerDay = treatmentsPerDay;
	}
	public double getWeightedAvgFrequency() {
		return weightedAvgFrequency;
	}
	public void setWeightedAvgFrequency(double weightedAvgFrequency) {
		this.weightedAvgFrequency = weightedAvgFrequency;
	}
	public double getWeightedAvgPressure() {
		return weightedAvgPressure;
	}
	public void setWeightedAvgPressure(double weightedAvgPressure) {
		this.weightedAvgPressure = weightedAvgPressure;
	}
	public int getProgrammedCoughPauses() {
		return programmedCoughPauses;
	}
	public void setProgrammedCoughPauses(int programmedCoughPauses) {
		this.programmedCoughPauses = programmedCoughPauses;
	}
	public int getNormalCoughPauses() {
		return normalCoughPauses;
	}
	public void setNormalCoughPauses(int normalCoughPauses) {
		this.normalCoughPauses = normalCoughPauses;
	}
	public int getCoughPauses() {
		return coughPauses;
	}
	public void setCoughPauses(int coughPauses) {
		this.coughPauses = coughPauses;
	}
	public Note getNote() {
		return note;
	}
	public void setNote(Note note) {
		this.note = note;
	}
	public DateTime getStart() {
		return start;
	}
	public void setStart(DateTime start) {
		this.start = start;
	}
	public DateTime getEnd() {
		return end;
	}
	public void setEnd(DateTime end) {
		this.end = end;
	}
	public int getCoughPauseDuration() {
		return coughPauseDuration;
	}
	public void setCoughPauseDuration(int coughPauseDuration) {
		this.coughPauseDuration = coughPauseDuration;
	}



}
