package com.hillrom.vest.web.rest.dto.monarch;



public class ProtocolDataMonarchVO {

	private String id;
	
	private String type;
	
	private int treatmentsPerDay;
	
	private int minMinutesPerTreatment;
	
	private String treatmentLabel;
	
	private Integer minFrequency;
	
	private Integer maxFrequency;
	
	private Integer minIntensity;
	
	private Integer maxIntensity;
	
	public ProtocolDataMonarchVO(String id,String type, int treatmentsPerDay, int minMinutesPerTreatment,
			String treatmentLabel,Integer minFrequency,
			Integer maxFrequency, Integer minIntensity,
			Integer maxIntensity) {
		super();
		this.id = id;
		this.type = type;
		this.treatmentsPerDay = treatmentsPerDay;
		this.minMinutesPerTreatment = minMinutesPerTreatment;
		this.treatmentLabel = treatmentLabel;
		this.minFrequency = minFrequency;
		this.maxFrequency = maxFrequency;
		this.minIntensity = minIntensity;
		this.maxIntensity = maxIntensity;
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

	public Integer getMinIntensity() {
		return minIntensity;
	}

	public void setMinPressure(Integer minIntensity) {
		this.minIntensity = minIntensity;
	}

	public Integer getMaxIntensity() {
		return maxIntensity;
	}

	public void setMaxPressure(Integer maxIntensity) {
		this.maxIntensity = maxIntensity;
	}

	@Override
	public String toString() {
		return "ProtocolDataMonarchVO [ id= "+id+", type=" + type
				+ ", treatmentsPerDay=" + treatmentsPerDay
				+ ", minMinutesPerTreatment=" + minMinutesPerTreatment
				+ ", treatmentLabel=" + treatmentLabel + ", minFrequency="
				+ minFrequency + ", maxFrequency=" + maxFrequency
				+ ", minIntensity=" + minIntensity + ", maxIntensity="
				+ maxIntensity + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((maxFrequency == null) ? 0 : maxFrequency.hashCode());
		result = prime * result
				+ ((maxIntensity == null) ? 0 : maxIntensity.hashCode());
		result = prime * result
				+ ((minFrequency == null) ? 0 : minFrequency.hashCode());
		result = prime * result + minMinutesPerTreatment;
		result = prime * result
				+ ((minIntensity == null) ? 0 : minIntensity.hashCode());
		result = prime * result
				+ ((treatmentLabel == null) ? 0 : treatmentLabel.hashCode());
		result = prime * result + treatmentsPerDay;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProtocolDataMonarchVO other = (ProtocolDataMonarchVO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (maxFrequency == null) {
			if (other.maxFrequency != null)
				return false;
		} else if (!maxFrequency.equals(other.maxFrequency))
			return false;
		if (maxIntensity == null) {
			if (other.maxIntensity != null)
				return false;
		} else if (!maxIntensity.equals(other.maxIntensity))
			return false;
		if (minFrequency == null) {
			if (other.minFrequency != null)
				return false;
		} else if (!minFrequency.equals(other.minFrequency))
			return false;
		if (minMinutesPerTreatment != other.minMinutesPerTreatment)
			return false;
		if (minIntensity == null) {
			if (other.minIntensity != null)
				return false;
		} else if (!minIntensity.equals(other.minIntensity))
			return false;
		if (treatmentLabel == null) {
			if (other.treatmentLabel != null)
				return false;
		} else if (!treatmentLabel.equals(other.treatmentLabel))
			return false;
		if (treatmentsPerDay != other.treatmentsPerDay)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	
    
}
