package com.hillrom.vest.service;

import java.util.List;

import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.domain.TempPatientVestDeviceData;


public interface DeviceLogParser {

	public PatientVestDeviceRawLog parseBase64StringToPatientVestDeviceRawLog(String base16String);
	public List<PatientVestDeviceData> parseBase64StringToPatientVestDeviceLogEntry(String base64String);
	public List<TempPatientVestDeviceData> parseBase64StringToPatientVestDeviceLogEntryForTemp(String base64String);
}
