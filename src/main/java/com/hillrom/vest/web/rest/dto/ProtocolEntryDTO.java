package com.hillrom.vest.web.rest.dto;

import javax.validation.constraints.Size;

public class ProtocolEntryDTO {
	
	@Size(max = 50)
	private String treatmentLabel;
	
	@Size(max = 50)
	private int minMinutesPerTreatment;
	
	@Size(max = 50)
	private int maxMinutesPerTreatment;
	
	@Size(max = 50)
	private Integer minFrequency;
	
	@Size(max = 50)
	private Integer maxFrequency;
	
	@Size(max = 50)
	private Integer minPressure;
	
	@Size(max = 50)
	private Integer maxPressure;
	
	public ProtocolEntryDTO() {
		super();
	}

	public ProtocolEntryDTO(String treatmentLabel, int minMinutesPerTreatment,
			int maxMinutesPerTreatment, Integer minFrequency,
			Integer maxFrequency, Integer minPressure, Integer maxPressure) {
		super();
		this.treatmentLabel = treatmentLabel;
		this.minMinutesPerTreatment = minMinutesPerTreatment;
		this.maxMinutesPerTreatment = maxMinutesPerTreatment;
		this.minFrequency = minFrequency;
		this.maxFrequency = maxFrequency;
		this.minPressure = minPressure;
		this.maxPressure = maxPressure;
	}

	public String getTreatmentLabel() {
		return treatmentLabel;
	}

	public void setTreatmentLabel(String treatmentLabel) {
		this.treatmentLabel = treatmentLabel;
	}

	public int getMinMinutesPerTreatment() {
		return minMinutesPerTreatment;
	}

	public void setMinMinutesPerTreatment(int minMinutesPerTreatment) {
		this.minMinutesPerTreatment = minMinutesPerTreatment;
	}

	public int getMaxMinutesPerTreatment() {
		return maxMinutesPerTreatment;
	}

	public void setMaxMinutesPerTreatment(int maxMinutesPerTreatment) {
		this.maxMinutesPerTreatment = maxMinutesPerTreatment;
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

}
