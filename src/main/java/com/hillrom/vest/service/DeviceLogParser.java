package com.hillrom.vest.service;

import com.hillrom.vest.domain.PatientVestDeviceRawLog;


public interface DeviceLogParser {

	public PatientVestDeviceRawLog parseBase16StringToPatientVestDeviceRawLog(String base16String);
	//public DeviceLogEntry parseBase64StringToDeviceLogEntry(String base64String);
}
