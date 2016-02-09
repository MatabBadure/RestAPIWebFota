package com.hillrom.vest.web.rest.dto;



public class ProtocolDataVO {

	private String id;
	
	private String type;
	
	private int treatmentsPerDay;
	
	private int minMinutesPerTreatment;
	
	private String treatmentLabel;
	
	private Integer minFrequency;
	
	private Integer maxFrequency;
	
	private Integer minPressure;
	
	private Integer maxPressure;
	
	public ProtocolDataVO(String id,String type, int treatmentsPerDay, int minMinutesPerTreatment,
			String treatmentLabel,Integer minFrequency,
			Integer maxFrequency, Integer minPressure,
			Integer maxPressure) {
		super();
		this.id = id;
		this.type = type;
		this.treatmentsPerDay = treatmentsPerDay;
		this.minMinutesPerTreatment = minMinutesPerTreatment;
		this.treatmentLabel = treatmentLabel;
		this.minFrequency = minFrequency;
		this.maxFrequency = maxFrequency;
		this.minPressure = minPressure;
		this.maxPressure = maxPressure;
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTreatmentsPerDay() {
		return treatmentsPerDay;
	}

	public void setTreatmentsPerDay(int treatmentsPerDay) {
		this.treatmentsPerDay = treatmentsPerDay;
	}

	public int getMinMinutesPerTreatment() {
		return minMinutesPerTreatment;
	}

	public void setMinMinutesPerTreatment(int minMinutesPerTreatment) {
		this.minMinutesPerTreatment = minMinutesPerTreatment;
	}

	public String getTreatmentLabel() {
		return treatmentLabel;
	}

	public void setTreatmentLabel(String treatmentLabel) {
		this.treatmentLabel = treatmentLabel;
	}

	public Integer getMinFrequency() {
		return minFrequency;
	}

	public void setMinFrequency(Integer minFrequency) {
		this.minFrequency = minFrequency;
	}

	public Integer getMaxFrequency() {
		return maxFrequency;
	}

	public void setMaxFrequency(Integer maxFrequency) {
		this.maxFrequency = maxFrequency;
	}

	public Integer getMinPressure() {
		return minPressure;
	}

	public void setMinPressure(Integer minPressure) {
		this.minPressure = minPressure;
	}

	public Integer getMaxPressure() {
		return maxPressure;
	}

	public void setMaxPressure(Integer maxPressure) {
		this.maxPressure = maxPressure;
	}

	@Override
	public String toString() {
		return "ProtocolDataVO [ id= "+id+", type=" + type
				+ ", treatmentsPerDay=" + treatmentsPerDay
				+ ", minMinutesPerTreatment=" + minMinutesPerTreatment
				+ ", treatmentLabel=" + treatmentLabel + ", minFrequency="
				+ minFrequency + ", maxFrequency=" + maxFrequency
				+ ", minPressure=" + minPressure + ", maxPressure="
				+ maxPressure + "]";
	}

    
}
