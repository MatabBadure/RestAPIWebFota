package com.hillrom.vest.domain;

import java.io.Serializable;

public class PatientVestDeviceDataPK implements Serializable {

	private Long timestamp;
	private String bluetoothId;
	private String eventId;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bluetoothId == null) ? 0 : bluetoothId.hashCode());
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
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
		PatientVestDeviceDataPK other = (PatientVestDeviceDataPK) obj;
		if (bluetoothId == null) {
			if (other.bluetoothId != null)
				return false;
		} else if (!bluetoothId.equals(other.bluetoothId))
			return false;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatientVestDeviceDataPK [timestamp=" + timestamp
				+ ", bluetoothId=" + bluetoothId + ", eventId=" + eventId + "]";
	}

	
}
