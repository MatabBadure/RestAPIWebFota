package com.hillrom.vest.service;

import java.util.List;

import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.domain.PatientVestDeviceRawLogMonarch;


public interface DeviceLogParser {

	public PatientVestDeviceRawLog parseBase64StringToPatientVestDeviceRawLog(String base16String) throws Exception;
	public List<PatientVestDeviceData> parseBase64StringToPatientVestDeviceLogEntry(String base64String) throws Exception;
}
