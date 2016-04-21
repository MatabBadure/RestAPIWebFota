package com.hillrom.vest.web.rest.dto;

public class BenchMarkDataVO {

	private  String groupLabel;
	private int patientCount;
	
	private int adherenceScoreBenchMark;
	private int hMRRunrateBenchMark;
	private int hMRDeviationBenchMark;
	private int settingDeviationBenchMark;
	private int missedTherapyDaysBenchMark;

	public BenchMarkDataVO(String groupLabel, int patientCount) {
		super();
		this.groupLabel = groupLabel;
		this.patientCount = patientCount;
	}
	public String getGroupLabel() {
		return groupLabel;
	}
	public void setGroupLabel(String groupLabel) {
		this.groupLabel = groupLabel;
	}
	public int getPatientCount() {
		return patientCount;
	}
	public void setPatientCount(int patientCount) {
		this.patientCount = patientCount;
	}
	public int getAdherenceScoreBenchMark() {
		return adherenceScoreBenchMark;
	}
	public void setAdherenceScoreBenchMark(int adherenceScoreBenchMark) {
		this.adherenceScoreBenchMark = adherenceScoreBenchMark;
	}
	public int gethMRRunrateBenchMark() {
		return hMRRunrateBenchMark;
	}
	public void sethMRRunrateBenchMark(int hMRRunrateBenchMark) {
		this.hMRRunrateBenchMark = hMRRunrateBenchMark;
	}
	public int gethMRDeviationBenchMark() {
		return hMRDeviationBenchMark;
	}
	public void sethMRDeviationBenchMark(int hMRDeviationBenchMark) {
		this.hMRDeviationBenchMark = hMRDeviationBenchMark;
	}
	public int getSettingDeviationBenchMark() {
		return settingDeviationBenchMark;
	}
	public void setSettingDeviationBenchMark(int settingDeviationBenchMark) {
		this.settingDeviationBenchMark = settingDeviationBenchMark;
	}
	public int getMissedTherapyDaysBenchMark() {
		return missedTherapyDaysBenchMark;
	}
	public void setMissedTherapyDaysBenchMark(int missedTherapyDaysBenchMark) {
		this.missedTherapyDaysBenchMark = missedTherapyDaysBenchMark;
	}
	
	
	
}
