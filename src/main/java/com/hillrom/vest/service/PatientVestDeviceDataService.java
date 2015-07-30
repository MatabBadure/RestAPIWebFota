package com.hillrom.vest.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.repository.PatientVestDeviceRawLogRepository;
import com.hillrom.vest.web.rest.util.PaginationUtil;

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
		PatientVestDeviceRawLog deviceRawLog = deviceLogParser
				.parseBase64StringToPatientVestDeviceRawLog(rawData);
		List<PatientVestDeviceData> patientVestDeviceRecords = deviceLogParser
				.parseBase64StringToPatientVestDeviceLogEntry(deviceRawLog
						.getDeviceData());
		
		String deviceAddress = deviceRawLog.getDeviceAddress();

		Optional<PatientInfo> patientFromDB = patientInfoRepository
				.findByBluetoothId(deviceAddress);

		PatientInfo patientInfo = null;
		List<PatientVestDeviceData> recordsToBeInserted = null;
		
		if(patientFromDB.isPresent()){
			patientInfo = patientFromDB.get();
			recordsToBeInserted = filterNewRecords(patientFromDB.get(),deviceRawLog,patientVestDeviceRecords);
		}else{
			patientInfo = new PatientInfo();
			patientInfo.setBluetoothId(deviceAddress);
			patientInfo.setHubId(deviceRawLog.getHubId());
			PatientInfo newPatientInfo = patientInfoRepository.save(patientInfo);
			patientVestDeviceRecords.stream().forEach(deviceData -> {
				deviceData.setHubId(deviceRawLog.getHubId());
				deviceData.setSerialNumber(deviceRawLog.getDeviceSerialNumber());
				deviceData.setPatient(newPatientInfo);
				deviceData.setBluetoothId(deviceRawLog.getDeviceAddress());
			});
			recordsToBeInserted = patientVestDeviceRecords;
		}

		if(recordsToBeInserted.size() > 0){			
			deviceDataRepository.save(recordsToBeInserted);
		}
		deviceRawLogRepository.save(deviceRawLog);
		return patientVestDeviceRecords;
	}

	private List<PatientVestDeviceData> filterNewRecords(PatientInfo patientInfo,PatientVestDeviceRawLog deviceRawLog,List<PatientVestDeviceData> patientVestDeviceRecords){
		Pageable pageable = PaginationUtil.generatePageRequest(1, 1);
		Page<PatientVestDeviceData> latestVestDeviceDataForPatientInDB = deviceDataRepository
				.findLatest(patientInfo.getId(),pageable);
		List<PatientVestDeviceData> latestVestDeviceDataRecords = latestVestDeviceDataForPatientInDB.getContent();
		if(latestVestDeviceDataRecords.size() > 0){
			PatientVestDeviceData latestDeviceData = latestVestDeviceDataRecords.get(0); 
			// Removing Duplicate Records
			patientVestDeviceRecords = patientVestDeviceRecords
					.stream()
					.filter(record -> record.getTimestamp() > latestDeviceData
							.getTimestamp()).collect(Collectors.toList());
			//Updating the new records with required fields
			patientVestDeviceRecords.forEach(deviceData -> {
				deviceData.setHubId(deviceRawLog.getHubId());
				deviceData.setSerialNumber(deviceRawLog.getDeviceSerialNumber());
				deviceData.setPatient(patientInfo);
				deviceData.setBluetoothId(deviceRawLog.getDeviceAddress());
			});
		}
		return patientVestDeviceRecords;
	}
}
