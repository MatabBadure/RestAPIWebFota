package com.hillrom.vest.batch.processing.monarch;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.inject.Inject;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.domain.VestDeviceBadData;
import com.hillrom.vest.domain.VestDeviceBadDataMonarch;
import com.hillrom.vest.repository.VestDeviceBadDataRepository;
import com.hillrom.vest.repository.VestDeviceBadDataRepositoryMonarch;
import com.hillrom.vest.service.MailService;

public class CustomItemReaderListenerMonarch implements ItemReadListener<List<PatientVestDeviceDataMonarch>> {

	
	String patientDeviceRawDataMonarch;
	
	@Inject
	VestDeviceBadDataRepositoryMonarch badDataRepositoryMonarch;
	
	@Inject
	MailService mailServie;
	
	@Override
	public void afterRead(List<PatientVestDeviceDataMonarch> item) {	
	}

	@Override
	public void beforeRead() {
	}

	@Override	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void onReadError(Exception ex) {
		badDataRepositoryMonarch.save(new VestDeviceBadDataMonarch(patientDeviceRawDataMonarch));
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter( writer );
		ex.printStackTrace( printWriter );
		mailServie.sendStatusOnDataIngestionRequest(patientDeviceRawDataMonarch,"FAILED" ,true, writer.toString());
	}
	
	@Value("#{jobParameters['rawData']}")
	public void setRawData(final String rawData) {
		this.patientDeviceRawDataMonarch = rawData;

	}

}