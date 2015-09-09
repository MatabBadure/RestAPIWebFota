package com.hillrom.vest.service;

import java.util.List;
import java.util.Map;

import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;


public interface DeviceLogParser {

	public PatientVestDeviceRawLog parseBase64StringToPatientVestDeviceRawLog(Map<String,String> deviceRawLogData);
	public List<PatientVestDeviceData> parseBase64StringToPatientVestDeviceLogEntry(String base64String);
}
