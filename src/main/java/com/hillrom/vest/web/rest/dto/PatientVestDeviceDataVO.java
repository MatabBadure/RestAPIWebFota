package com.hillrom.vest.web.rest.dto;

public class PatientVestDeviceDataVO {

	private Long timestamp;
	private Integer sequenceNumber;
	private String eventId; 
	private String patientId; 
	private String serialNumber;
	private String bluetoothId; 
	private String hubId; 
	private double hmr;
	private Integer frequency;
	private Integer pressure;
	private Integer duration; 
	private Integer checksum; 
	private Long userId;
	
	public PatientVestDeviceDataVO(Long timestamp, Integer sequenceNumber,
			String eventId, String patientId, String serialNumber,
			String bluetoothId, String hubId, double hmr, Integer frequency,
			Integer pressure, Integer duration, Integer checksum, Long userId) {
		super();
		this.timestamp = timestamp;
		this.sequenceNumber = sequenceNumber;
		this.eventId = eventId;
		this.patientId = patientId;
		this.serialNumber = serialNumber;
		this.bluetoothId = bluetoothId;
		this.hubId = hubId;
		this.hmr = hmr;
		this.frequency = frequency;
		this.pressure = pressure;
		this.duration = duration;
		this.checksum = checksum;
		this.userId = userId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getBluetoothId() {
		return bluetoothId;
	}

	public void setBluetoothId(String bluetoothId) {
		this.bluetoothId = bluetoothId;
	}

	public String getHubId() {
		return hubId;
	}

	public void setHubId(String hubId) {
		this.hubId = hubId;
	}

	public double getHmr() {
		return hmr;
	}

	public void setHmr(double hmr) {
		this.hmr = hmr;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public Integer getPressure() {
		return pressure;
	}

	public void setPressure(Integer pressure) {
		this.pressure = pressure;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getChecksum() {
		return checksum;
	}

	public void setChecksum(Integer checksum) {
		this.checksum = checksum;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
