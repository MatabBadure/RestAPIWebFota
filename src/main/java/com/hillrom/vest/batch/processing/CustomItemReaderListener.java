package com.hillrom.vest.batch.processing;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.inject.Inject;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.VestDeviceBadData;
import com.hillrom.vest.repository.VestDeviceBadDataRepository;
import com.hillrom.vest.service.MailService;

public class CustomItemReaderListener implements ItemReadListener<List<PatientVestDeviceData>> {

	
	String patientDeviceRawData;
	
	@Inject
	VestDeviceBadDataRepository badDataRepository;
	
	@Inject
	MailService mailServie;
	
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
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter( writer );
		ex.printStackTrace( printWriter );
		mailServie.sendStatusOnDataIngestionRequest(patientDeviceRawData,"FAILED" ,true, writer.toString());
	}
	
	@Value("#{jobParameters['rawData']}")
	public void setRawData(final String rawData) {
		this.patientDeviceRawData = rawData;

	}

}