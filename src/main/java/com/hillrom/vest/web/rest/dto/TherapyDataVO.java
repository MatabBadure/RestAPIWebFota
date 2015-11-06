package com.hillrom.vest.web.rest.dto;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.hillrom.vest.domain.Note;

public class TherapyDataVO implements Serializable,Comparable<TherapyDataVO> {

	private DateTime timestamp;
	private int treatmentsPerDay;
	private int sessionNo;
	private int frequency;
	private int pressure;
	private int programmedCoughPauses;
	private int normalCoughPauses;
	private int coughPauses;
	private Note note;
	private DateTime start;
	private DateTime end;
	private int coughPauseDuration;
	private int duration;
	private double hmr;
	private boolean missedTherapy;

	public TherapyDataVO(DateTime timestamp, int treatmentsPerDay,int sessionNo,
			int frequency, int pressure, int programmedCoughPauses,
			int normalCoughPauses, int coughPauses, Note note, DateTime start,
			DateTime end, int coughPauseDuration, int duration, double hmr,boolean missedTherapy) {
		super();
		this.timestamp = timestamp;
		this.treatmentsPerDay = treatmentsPerDay;
		this.sessionNo = sessionNo;
		this.sessionNo = sessionNo;
		this.frequency = frequency;
		this.pressure = pressure;
		this.programmedCoughPauses = programmedCoughPauses;
		this.normalCoughPauses = normalCoughPauses;
		this.coughPauses = coughPauses;
		this.note = note;
		this.start = start;
		this.end = end;
		this.coughPauseDuration = coughPauseDuration;
		this.duration = duration;
		this.hmr = hmr;
		this.missedTherapy = missedTherapy;
	}

	public TherapyDataVO() {
		super();
	}

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

	public int getSessionNo() {
		return sessionNo;
	}

	public void setSessionNo(int sessionNo) {
		this.sessionNo = sessionNo;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getPressure() {
		return pressure;
	}

	public void setPressure(int pressure) {
		this.pressure = pressure;
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

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public double getHmr() {
		return hmr;
	}

	public void setHmr(double hmr) {
		this.hmr = hmr;
	}

	public boolean isMissedTherapy() {
		return missedTherapy;
	}

	public void setMissedTherapy(boolean missedTherapy) {
		this.missedTherapy = missedTherapy;
	}

	@Override
	public String toString() {
		return "TherapyDataVO [timestamp=" + timestamp + ", treatmentsPerDay="+treatmentsPerDay
				+ " sessionNo="+ sessionNo + ", frequency=" + frequency + ", pressure="
				+ pressure + ", programmedCoughPauses=" + programmedCoughPauses
				+ ", normalCoughPauses=" + normalCoughPauses + ", coughPauses="
				+ coughPauses + ", note=" + note + ", start=" + start
				+ ", end=" + end + ", coughPauseDuration=" + coughPauseDuration
				+ ", duration=" + duration + ", hmr=" + hmr + "missedTherapy="+missedTherapy+"]";
	}

	@Override
	public int compareTo(TherapyDataVO o) {
		return this.timestamp.compareTo(o.getTimestamp());
	}

	
}
