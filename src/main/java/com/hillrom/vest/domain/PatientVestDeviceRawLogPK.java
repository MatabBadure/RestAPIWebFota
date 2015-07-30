package com.hillrom.vest.domain;

import java.io.Serializable;


public class PatientVestDeviceRawLogPK implements Serializable {

	private static final long serialVersionUID = 1L;

    private Long hubReceiveTime;

    private String deviceAddress;

	public Long getHubReceiveTime() {
		return hubReceiveTime;
	}

	public void setHubReceiveTime(Long hubReceiveTime) {
		this.hubReceiveTime = hubReceiveTime;
	}

	public String getDeviceAddress() {
		return deviceAddress;
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deviceAddress == null) ? 0 : deviceAddress.hashCode());
		result = prime * result
				+ ((hubReceiveTime == null) ? 0 : hubReceiveTime.hashCode());
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
		PatientVestDeviceRawLogPK other = (PatientVestDeviceRawLogPK) obj;
		if (deviceAddress == null) {
			if (other.deviceAddress != null)
				return false;
		} else if (!deviceAddress.equals(other.deviceAddress))
			return false;
		if (hubReceiveTime == null) {
			if (other.hubReceiveTime != null)
				return false;
		} else if (!hubReceiveTime.equals(other.hubReceiveTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatientVestDeviceRawLogPK [hubReceiveTime=" + hubReceiveTime
				+ ", deviceAddress=" + deviceAddress + "]";
	}
    
	

}
