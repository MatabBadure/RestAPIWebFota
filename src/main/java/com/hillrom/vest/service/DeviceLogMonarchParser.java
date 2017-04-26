package com.hillrom.vest.service;

import java.util.List;

import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.domain.PatientVestDeviceRawLogMonarch;


public interface DeviceLogMonarchParser {

	public PatientVestDeviceRawLogMonarch parseBase64StringToPatientMonarchDeviceRawLog(String base16String) throws Exception;
	public List<PatientVestDeviceDataMonarch> parseBase64StringToPatientMonarchDeviceLogEntry(String base64String) throws Exception;
}
