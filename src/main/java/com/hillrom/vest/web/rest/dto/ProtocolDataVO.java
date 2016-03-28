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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((maxFrequency == null) ? 0 : maxFrequency.hashCode());
		result = prime * result
				+ ((maxPressure == null) ? 0 : maxPressure.hashCode());
		result = prime * result
				+ ((minFrequency == null) ? 0 : minFrequency.hashCode());
		result = prime * result + minMinutesPerTreatment;
		result = prime * result
				+ ((minPressure == null) ? 0 : minPressure.hashCode());
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
		ProtocolDataVO other = (ProtocolDataVO) obj;
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
		if (maxPressure == null) {
			if (other.maxPressure != null)
				return false;
		} else if (!maxPressure.equals(other.maxPressure))
			return false;
		if (minFrequency == null) {
			if (other.minFrequency != null)
				return false;
		} else if (!minFrequency.equals(other.minFrequency))
			return false;
		if (minMinutesPerTreatment != other.minMinutesPerTreatment)
			return false;
		if (minPressure == null) {
			if (other.minPressure != null)
				return false;
		} else if (!minPressure.equals(other.minPressure))
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
