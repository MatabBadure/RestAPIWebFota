package com.hillrom.vest.batch.processing.monarch;

import java.util.List;

import javax.inject.Inject;

import org.springframework.batch.item.ItemWriter;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.PatientMonarchDeviceDataRepository;
import com.hillrom.vest.repository.PatientVestDeviceRepository;
import com.hillrom.vest.service.PatientVestDeviceService;

public class PatientMonarchDeviceDataWriter implements ItemWriter<List<PatientVestDeviceDataMonarch>>{
	
	@Inject
	private PatientMonarchDeviceDataRepository deviceDataRepository;
	
	@Inject
	private PatientVestDeviceService deviceService;
	
	@Override
	public void write(List<? extends List<PatientVestDeviceDataMonarch>> vestDeviceData)
			throws Exception {
		if(vestDeviceData.size() > 0){
			if(!vestDeviceData.get(0).isEmpty()){
				User patientUser = vestDeviceData.get(0).get(0).getPatientUser();
				PatientInfo patient = vestDeviceData.get(0).get(0).getPatient();
				Double maxHMR = 0d;
				for(List<PatientVestDeviceDataMonarch> devData : vestDeviceData){
					deviceDataRepository.save(devData);
					for(PatientVestDeviceDataMonarch deviceEvent : devData){
						if(maxHMR < deviceEvent.getHmr())
							maxHMR = deviceEvent.getHmr();
					}
				}
				deviceService.updateHMR(patientUser, patient);
			}
		}
	}
}
