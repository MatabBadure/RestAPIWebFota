package com.hillrom.vest.batch.processing;

import java.util.List;

import javax.inject.Inject;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.VestDeviceBadData;
import com.hillrom.vest.repository.VestDeviceBadDataRepository;

public class CustomItemReaderListener implements ItemReadListener<List<PatientVestDeviceData>> {

	
	String patientDeviceRawData;
	
	@Inject
	VestDeviceBadDataRepository badDataRepository;
	
	@Override
	public void afterRead(List<PatientVestDeviceData> item) {	
	}

	@Override
	public void beforeRead() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void onReadError(Exception ex) {
		badDataRepository.save(new VestDeviceBadData(patientDeviceRawData));
	}
	
	@Value("#{jobParameters['rawData']}")
	public void setRawData(final String rawData) {
		this.patientDeviceRawData = rawData;

	}

}