package com.hillrom.vest.batch.processing.monarch;

import java.util.List;

import javax.inject.Inject;

import org.springframework.batch.item.ItemWriter;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.PatientVestDeviceRepository;
import com.hillrom.vest.repository.monarch.PatientMonarchDeviceDataRepository;
import com.hillrom.vest.service.PatientVestDeviceService;
import com.hillrom.vest.service.monarch.PatientVestDeviceMonarchService;

public class PatientMonarchDeviceDataWriter implements ItemWriter<List<PatientVestDeviceDataMonarch>>{
	
	@Inject
	private PatientMonarchDeviceDataRepository deviceDataRepositoryMonarch;
	
	@Inject
	private PatientVestDeviceMonarchService deviceServiceMonarch;
	
	@Override
	public void write(List<? extends List<PatientVestDeviceDataMonarch>> vestDeviceDataMonarch)
			throws Exception {
		if(vestDeviceDataMonarch.size() > 0){
			if(!vestDeviceDataMonarch.get(0).isEmpty()){
				User patientUser = vestDeviceDataMonarch.get(0).get(0).getPatientUser();
				PatientInfo patient = vestDeviceDataMonarch.get(0).get(0).getPatient();
				for(List<PatientVestDeviceDataMonarch> devDataMonarch : vestDeviceDataMonarch){
					deviceDataRepositoryMonarch.save(devDataMonarch);
				}
				deviceServiceMonarch.updateHMR(patientUser, patient);
			}
		}
	}
}
