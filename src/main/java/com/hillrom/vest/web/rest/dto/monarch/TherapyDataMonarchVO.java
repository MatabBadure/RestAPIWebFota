package com.hillrom.vest.web.rest.dto.monarch;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.Note;
import com.hillrom.vest.domain.NoteMonarch;
import com.hillrom.vest.domain.util.DateTimeSerializer;

public class TherapyDataMonarchVO implements Serializable,Comparable<TherapyDataMonarchVO> {

	@JsonSerialize(using= DateTimeSerializer.class)
	private DateTime timestamp;
	private int treatmentsPerDay;
	private int sessionNo;
	private int frequency;
	private int pressure;
	private int programmedCoughPauses;
	private int normalCoughPauses;
	private int coughPauses;
	private Note note;
	@JsonSerialize(using= DateTimeSerializer.class)
	private DateTime start;
	@JsonSerialize(using= DateTimeSerializer.class)
	private DateTime end;
	private int coughPauseDuration;
	private int duration;
	private double hmr;
	private boolean missedTherapy;
	private NoteMonarch noteMonarch;
	private int intensity;
	
	public TherapyDataMonarchVO(DateTime timestamp, int treatmentsPerDay,int sessionNo,
			int frequency, int intensity, int programmedCoughPauses,
			int normalCoughPauses, int coughPauses, NoteMonarch note, DateTime start,
			DateTime end, int coughPauseDuration, int duration, double hmr,boolean missedTherapy) {
		super();
		this.timestamp = timestamp;
		this.treatmentsPerDay = treatmentsPerDay;
		this.sessionNo = sessionNo;
		this.sessionNo = sessionNo;
		this.frequency = frequency;
		this.intensity = intensity;
		this.programmedCoughPauses = programmedCoughPauses;
		this.normalCoughPauses = normalCoughPauses;
		this.coughPauses = coughPauses;
		this.noteMonarch = note;
		this.start = start;
		this.end = end;
		this.coughPauseDuration = coughPauseDuration;
		this.duration = duration;
		this.hmr = hmr;
		this.missedTherapy = missedTherapy;
	}
	
	public TherapyDataMonarchVO(DateTime timestamp, int frequency, int intensity,
			int programmedCoughPauses, int normalCoughPauses, int coughPauses,
			NoteMonarch note, DateTime start, DateTime end, int coughPauseDuration,
			int duration, double hmr, boolean missedTherapy) {
		super();
		this.timestamp = timestamp;
		this.frequency = frequency;
		this.pressure = pressure;
		this.programmedCoughPauses = programmedCoughPauses;
		this.normalCoughPauses = normalCoughPauses;
		this.coughPauses = coughPauses;
		this.noteMonarch = note;
		this.start = start;
		this.end = end;
		this.coughPauseDuration = coughPauseDuration;
		this.duration = duration;
		this.hmr = hmr;
		this.missedTherapy = missedTherapy;
	}

	public TherapyDataMonarchVO() {
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
	
	public NoteMonarch getNoteMonarch() {
		return noteMonarch;
	}

	public void setNoteMonarch(NoteMonarch noteMonarch) {
		this.noteMonarch = noteMonarch;
	}
	public int getIntensity() {
		return intensity;
	}

	public void setIntensity(int intensity) {
		this.intensity = intensity;
	}

	@Override
	public String toString() {
		return "TherapyDataMonarchVO [timestamp=" + timestamp + ", treatmentsPerDay="+treatmentsPerDay
				+ " sessionNo="+ sessionNo + ", frequency=" + frequency + ", intensity="
				+ intensity + ", programmedCoughPauses=" + programmedCoughPauses
				+ ", normalCoughPauses=" + normalCoughPauses + ", coughPauses="
				+ coughPauses + ", note=" + note + ", start=" + start
				+ ", end=" + end + ", coughPauseDuration=" + coughPauseDuration
				+ ", duration=" + duration + ", hmr=" + hmr + "missedTherapy="+missedTherapy+"]";
	}

	@Override
	public int compareTo(TherapyDataMonarchVO o) {
		return this.timestamp.compareTo(o.getTimestamp());
	}

	@JsonIgnore
	public LocalDate getDate(){
		return this.timestamp.toLocalDate();
	}
}
