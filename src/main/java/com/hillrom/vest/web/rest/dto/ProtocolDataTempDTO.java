package com.hillrom.vest.web.rest.dto;



public class ProtocolDataTempDTO {

	private String id;
	
	private String patient_id;
	
	private String type;
	
	private int treatmentsPerDay;
	
	private int minMinutesPerTreatment;
	
	private int maxMinutesPerTreatment;
	
	private String treatmentLabel;
	
	private Integer minFrequency;
	
	private Integer maxFrequency;
	
	private Integer minPressure;
	
	private Integer maxPressure;

	private int to_be_inserted;
	
	public ProtocolDataTempDTO() {
		super();

	}



	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the patient_id
	 */
	public String getPatient_id() {
		return patient_id;
	}

	/**
	 * @param patient_id the patient_id to set
	 */
	public void setPatient_id(String patient_id) {
		this.patient_id = patient_id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the treatmentsPerDay
	 */
	public int getTreatmentsPerDay() {
		return treatmentsPerDay;
	}

	/**
	 * @param treatmentsPerDay the treatmentsPerDay to set
	 */
	public void setTreatmentsPerDay(int treatmentsPerDay) {
		this.treatmentsPerDay = treatmentsPerDay;
	}

	/**
	 * @return the minMinutesPerTreatment
	 */
	public int getMinMinutesPerTreatment() {
		return minMinutesPerTreatment;
	}

	/**
	 * @param minMinutesPerTreatment the minMinutesPerTreatment to set
	 */
	public void setMinMinutesPerTreatment(int minMinutesPerTreatment) {
		this.minMinutesPerTreatment = minMinutesPerTreatment;
	}

	/**
	 * @return the maxMinutesPerTreatment
	 */
	public int getMaxMinutesPerTreatment() {
		return maxMinutesPerTreatment;
	}

	/**
	 * @param maxMinutesPerTreatment the maxMinutesPerTreatment to set
	 */
	public void setMaxMinutesPerTreatment(int maxMinutesPerTreatment) {
		this.maxMinutesPerTreatment = maxMinutesPerTreatment;
	}

	/**
	 * @return the treatmentLabel
	 */
	public String getTreatmentLabel() {
		return treatmentLabel;
	}

	/**
	 * @param treatmentLabel the treatmentLabel to set
	 */
	public void setTreatmentLabel(String treatmentLabel) {
		this.treatmentLabel = treatmentLabel;
	}

	/**
	 * @return the minFrequency
	 */
	public Integer getMinFrequency() {
		return minFrequency;
	}

	/**
	 * @param minFrequency the minFrequency to set
	 */
	public void setMinFrequency(Integer minFrequency) {
		this.minFrequency = minFrequency;
	}

	/**
	 * @return the maxFrequency
	 */
	public Integer getMaxFrequency() {
		return maxFrequency;
	}

	/**
	 * @param maxFrequency the maxFrequency to set
	 */
	public void setMaxFrequency(Integer maxFrequency) {
		this.maxFrequency = maxFrequency;
	}

	/**
	 * @return the minPressure
	 */
	public Integer getMinPressure() {
		return minPressure;
	}

	/**
	 * @param minPressure the minPressure to set
	 */
	public void setMinPressure(Integer minPressure) {
		this.minPressure = minPressure;
	}

	/**
	 * @return the maxPressure
	 */
	public Integer getMaxPressure() {
		return maxPressure;
	}

	/**
	 * @param maxPressure the maxPressure to set
	 */
	public void setMaxPressure(Integer maxPressure) {
		this.maxPressure = maxPressure;
	}

	/**
	 * @return the to_be_inserted
	 */
	public int getTo_be_inserted() {
		return to_be_inserted;
	}

	/**
	 * @param to_be_inserted the to_be_inserted to set
	 */
	public void setTo_be_inserted(int to_be_inserted) {
		this.to_be_inserted = to_be_inserted;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((maxFrequency == null) ? 0 : maxFrequency.hashCode());
		result = prime * result + maxMinutesPerTreatment;
		result = prime * result + ((maxPressure == null) ? 0 : maxPressure.hashCode());
		result = prime * result + ((minFrequency == null) ? 0 : minFrequency.hashCode());
		result = prime * result + minMinutesPerTreatment;
		result = prime * result + ((minPressure == null) ? 0 : minPressure.hashCode());
		result = prime * result + ((patient_id == null) ? 0 : patient_id.hashCode());
		result = prime * result + to_be_inserted;
		result = prime * result + ((treatmentLabel == null) ? 0 : treatmentLabel.hashCode());
		result = prime * result + treatmentsPerDay;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProtocolDataTempDTO other = (ProtocolDataTempDTO) obj;
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
		if (maxMinutesPerTreatment != other.maxMinutesPerTreatment)
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
		if (patient_id == null) {
			if (other.patient_id != null)
				return false;
		} else if (!patient_id.equals(other.patient_id))
			return false;
		if (to_be_inserted != other.to_be_inserted)
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



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProtocolDataTempDTO [id=" + id + ", patient_id=" + patient_id + ", type=" + type + ", treatmentsPerDay="
				+ treatmentsPerDay + ", minMinutesPerTreatment=" + minMinutesPerTreatment + ", maxMinutesPerTreatment="
				+ maxMinutesPerTreatment + ", treatmentLabel=" + treatmentLabel + ", minFrequency=" + minFrequency
				+ ", maxFrequency=" + maxFrequency + ", minPressure=" + minPressure + ", maxPressure=" + maxPressure
				+ ", to_be_inserted=" + to_be_inserted + "]";
	}
	
	
	
}
