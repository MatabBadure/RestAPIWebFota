package com.hillrom.vest.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.repository.PatientVestDeviceRawLogRepository;

@Service
@Transactional
public class PatientVestDeviceDataService {

	@Inject
	private DeviceLogParser deviceLogParser;

	@Inject
	private PatientVestDeviceDataRepository deviceDataRepository;

	@Inject
	private PatientVestDeviceRawLogRepository deviceRawLogRepository;

	@Inject
	private PatientInfoRepository patientInfoRepository;

	public List<PatientVestDeviceData> save(final String rawData) {
		PatientVestDeviceRawLog deviceRawLog = null;
		List<PatientVestDeviceData> patientVestDeviceRecords = null;
		try {
			deviceRawLog = deviceLogParser
					.parseBase64StringToPatientVestDeviceRawLog(rawData);
			
			deviceRawLogRepository.save(deviceRawLog);

			patientVestDeviceRecords = deviceLogParser
					.parseBase64StringToPatientVestDeviceLogEntry(deviceRawLog
							.getDeviceData());
			
			String deviceAddress = deviceRawLog.getDeviceAddress();

			PatientInfo patientInfo = createPatientInfoIfNotExists(deviceRawLog,
					deviceAddress);
			assignDefaultValuesToVestDeviceData(deviceRawLog,
					patientVestDeviceRecords, patientInfo);

			deviceDataRepository.save(patientVestDeviceRecords);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return patientVestDeviceRecords;
		
	}

	private PatientInfo createPatientInfoIfNotExists(
			PatientVestDeviceRawLog deviceRawLog, String deviceAddress) {
		Optional<PatientInfo> patientFromDB = patientInfoRepository
				.findByBluetoothId(deviceAddress);

		PatientInfo patientInfo = null;
		
		if(patientFromDB.isPresent()){
			patientInfo = patientFromDB.get();
		}else{
			patientInfo = new PatientInfo();
			// Assigns the next hillromId for the patient
			patientInfo.setId(patientInfoRepository.id());
			patientInfo.setBluetoothId(deviceAddress);
			patientInfo.setHubId(deviceRawLog.getHubId());
			patientInfo = patientInfoRepository.save(patientInfo);
		}
		return patientInfo;
	}

	private void assignDefaultValuesToVestDeviceData(
			PatientVestDeviceRawLog deviceRawLog,
			List<PatientVestDeviceData> patientVestDeviceRecords,
			PatientInfo newPatientInfo) {
		patientVestDeviceRecords.stream().forEach(deviceData -> {
			deviceData.setHubId(deviceRawLog.getHubId());
			deviceData.setSerialNumber(deviceRawLog.getDeviceSerialNumber());
			deviceData.setPatient(newPatientInfo);
			deviceData.setBluetoothId(deviceRawLog.getDeviceAddress());
		});
	}
}
