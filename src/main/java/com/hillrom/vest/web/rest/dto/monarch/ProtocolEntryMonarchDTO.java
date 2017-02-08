package com.hillrom.vest.web.rest.dto.monarch;

import javax.validation.constraints.Size;

public class ProtocolEntryMonarchDTO {
	
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
	private Integer minIntensity;
	
	@Size(max = 50)
	private Integer maxIntensity;
	
	public ProtocolEntryMonarchDTO() {
		super();
	}

	public ProtocolEntryMonarchDTO(String treatmentLabel, int minMinutesPerTreatment,
			int maxMinutesPerTreatment, Integer minFrequency,
			Integer maxFrequency, Integer minIntensity, Integer maxIntensity) {
		super();
		this.treatmentLabel = treatmentLabel;
		this.minMinutesPerTreatment = minMinutesPerTreatment;
		this.maxMinutesPerTreatment = maxMinutesPerTreatment;
		this.minFrequency = minFrequency;
		this.maxFrequency = maxFrequency;
		this.minIntensity = minIntensity;
		this.maxIntensity = maxIntensity;
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

	public Integer getMinIntensity() {
		return minIntensity;
	}

	public void setMinIntensity(Integer minIntensity) {
		this.minIntensity = minIntensity;
	}

	public Integer getMaxIntensity() {
		return maxIntensity;
	}

	public void setMaxIntensity(Integer maxIntensity) {
		this.maxIntensity = maxIntensity;
	}
}
