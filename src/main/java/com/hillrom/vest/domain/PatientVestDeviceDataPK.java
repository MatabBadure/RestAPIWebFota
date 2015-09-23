package com.hillrom.vest.domain;

import java.io.Serializable;

public class PatientVestDeviceDataPK implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long timestamp;
	private String serialNumber;
	private String bluetoothId;
	private String eventId;

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getBluetoothId() {
		return bluetoothId;
	}

	public void setBluetoothId(String bluetoothId) {
		this.bluetoothId = bluetoothId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	@Override
	public String toString() {
		return "PatientVestDeviceDataPK [timestamp=" + timestamp
				+ ", bluetoothId=" + bluetoothId + ", serialNumber="
				+ serialNumber + ", eventId=" + eventId + "]";
	}
	
}
